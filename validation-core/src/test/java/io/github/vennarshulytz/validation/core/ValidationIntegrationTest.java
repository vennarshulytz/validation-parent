package io.github.vennarshulytz.validation.core;

import com.google.common.collect.ImmutableList;
import io.github.vennarshulytz.validation.annotation.FieldConfig;
import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.annotation.ValidationRule;
import io.github.vennarshulytz.validation.annotation.ValidationRules;
import io.github.vennarshulytz.validation.core.model.Address;
import io.github.vennarshulytz.validation.core.model.Department;
import io.github.vennarshulytz.validation.core.model.Employee;
import io.github.vennarshulytz.validation.i18n.MessageResolver;
import io.github.vennarshulytz.validation.validator.CustomValidator;
import io.github.vennarshulytz.validation.validator.FieldValidator;
import io.github.vennarshulytz.validation.validator.ValidationResult;
import io.github.vennarshulytz.validation.validator.builtin.NotBlankValidator;
import io.github.vennarshulytz.validation.validator.builtin.NotNullValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 集成测试 - 模拟完整校验流程
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
class ValidationIntegrationTest {

    private ValidationEngine validationEngine;
    private BeanFactory mockBeanFactory;

    @BeforeEach
    void setUp() {
        mockBeanFactory = mock(BeanFactory.class);

        // Mock自定义校验器
        DepartmentCustomValidator customValidator = new DepartmentCustomValidator();
        when(mockBeanFactory.getBean(DepartmentCustomValidator.class)).thenReturn(customValidator);

        // 对于内置校验器，抛出NoSuchBeanDefinitionException让其自动实例化
        when(mockBeanFactory.getBean(NotNullValidator.class))
                .thenThrow(new NoSuchBeanDefinitionException(NotNullValidator.class));
        when(mockBeanFactory.getBean(NotBlankValidator.class))
                .thenThrow(new NoSuchBeanDefinitionException(NotBlankValidator.class));

        ValidatorRegistry validatorRegistry = new ValidatorRegistry(mockBeanFactory);
        MessageResolver messageResolver = new MessageResolver(null, false);
        validationEngine = new ValidationEngine(validatorRegistry, messageResolver);
    }

    @Test
    @DisplayName("集成测试 - 完整校验流程（快速失败模式）")
    void testFullValidation_FailFastMode() {
        Department department = createInvalidDepartment();

        ValidationContext context = new ValidationContext(ValidationMode.FAIL_FAST, false);

        // 模拟注解配置：Employee类型，空路径（排除managerList1）
        simulateValidationRulesInit(context);

        validationEngine.validate(department, context);

        ValidationResult result = context.getResult();
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrors().size()); // 快速失败，只有第一个错误
    }

    @Test
    @DisplayName("集成测试 - 完整校验流程（全量校验模式）")
    void testFullValidation_FailAllMode() {
        Department department = createInvalidDepartment();

        ValidationContext context = new ValidationContext(ValidationMode.FAIL_ALL, false);
        simulateValidationRulesInit(context);

        validationEngine.validate(department, context);

        ValidationResult result = context.getResult();
        assertTrue(result.hasErrors());
        assertTrue(result.getErrors().size() > 1); // 全量校验，收集所有错误
    }

    @Test
    @DisplayName("集成测试 - 路径精确匹配与排除")
    void testPathMatchingAndExclusion() {
        Department department = createTestDepartmentForPathTest();

        ValidationContext context = new ValidationContext(ValidationMode.FAIL_ALL, false);

        // 配置规则：
        // 1. Employee类型，path=""，校验id和name不为null（排除managerList1）
        // 2. Employee类型，path="managerList1"，校验phone和address不为blank
        simulatePathExclusionRules(context);

        validationEngine.validate(department, context);

        ValidationResult result = context.getResult();

        // 打印所有错误便于调试
        for (ValidationResult.FieldError error : result.getErrors()) {
            System.out.println("Error: " + error.getField() + " - " + error.getMessage());
        }

        assertTrue(result.hasErrors());

        // 验证 manager1 的 id 被校验（因为path=""排除了managerList1）
        boolean hasManager1IdError = result.getErrors().stream()
                .anyMatch(e -> e.getField().equals("manager1.id"));
        assertTrue(hasManager1IdError, "manager1.id 应该被校验");

        // 验证 managerList1 中的 phone 被校验（因为path="managerList1"）
        boolean hasManagerList1PhoneError = result.getErrors().stream()
                .anyMatch(e -> e.getField().contains("managerList1") && e.getField().contains("phone"));
        assertTrue(hasManagerList1PhoneError, "managerList1中的phone应该被校验");
    }

    @Test
    @DisplayName("集成测试 - 自定义校验器")
    void testCustomValidator() {
        Department department = new Department();
        department.setName("这是一个超级超级超级超级超级超级超级超级超级超级超级超级超级超级超级超级超级超级长的部门名称这是一个超级超级超级超级超级超级超级超级超级超级超级超级超级超级超级超级超级超级长的部门名称这是一个超级超级超级超级超级超级超级超级超级超级超级超级超级超级超级超级超级超级长的部门名称");
        department.setManagerList1(new ArrayList<>()); // 空列表

        ValidationContext context = new ValidationContext(ValidationMode.FAIL_ALL, false);
        simulateCustomValidatorRule(context);

        validationEngine.validate(department, context);

        ValidationResult result = context.getResult();
        assertTrue(result.hasErrors());

        // 验证自定义校验器的错误
        boolean hasNameLengthError = result.getErrors().stream()
                .anyMatch(e -> e.getMessage().contains("长度不能超过100"));
        boolean hasEmptyListError = result.getErrors().stream()
                .anyMatch(e -> e.getMessage().contains("不能为空"));

        assertTrue(hasNameLengthError);
        assertTrue(hasEmptyListError);
    }

    @Test
    @DisplayName("集成测试 - 嵌套路径校验")
    void testNestedPathValidation() {
        Department department = createDepartmentWithNestedAddress();

        ValidationContext context = new ValidationContext(ValidationMode.FAIL_ALL, false);
        simulateNestedPathRule(context);

        validationEngine.validate(department, context);

        ValidationResult result = context.getResult();
        assertTrue(result.hasErrors());

        // 验证嵌套路径中的Address.id被校验
        boolean hasAddressIdError = result.getErrors().stream()
                .anyMatch(e -> e.getField().contains("address1") && e.getField().contains("id"));
        assertTrue(hasAddressIdError);
    }

    // ===== 辅助方法 =====

    private Department createInvalidDepartment() {
        Department department = new Department();
        department.setName("技术部");

        Employee manager1 = new Employee();
        manager1.setId(null); // invalid
        manager1.setName(null); // invalid
        department.setManager1(manager1);

        Employee manager2 = new Employee();
        manager2.setId(null); // invalid
        manager2.setName(null); // invalid
//        department.setManager1(manager2);

        Employee manager3 = new Employee();
        manager3.setId(null); // invalid
        manager3.setName(null); // invalid
        department.setManagerList1(ImmutableList.of(manager2, manager3));

        return department;
    }

    private Department createTestDepartmentForPathTest() {
        Department department = new Department();
        department.setName("技术部");

        // manager1 - id为null，应该被path=""的规则校验
        Employee manager1 = new Employee();
        manager1.setId(null);
        manager1.setName("经理1");
        manager1.setPhone("13800000001");
        department.setManager1(manager1);

        // managerList1 - phone为blank，应该被path="managerList1"的规则校验
        List<Employee> managerList1 = new ArrayList<>();
        Employee emp1 = new Employee();
        emp1.setId("e001");
        emp1.setName("员工1");
        emp1.setPhone("   "); // blank
        emp1.setAddress("地址1");
        managerList1.add(emp1);
        department.setManagerList1(managerList1);

        return department;
    }

    private Department createDepartmentWithNestedAddress() {
        Department department = new Department();
        department.setName("技术部");

        List<Employee> managerList1 = new ArrayList<>();
        Employee emp1 = new Employee();
        emp1.setId("e001");
        emp1.setName("员工1");
        emp1.setPhone("13800000001");
        emp1.setAddress("地址1");

        Address address1 = new Address();
        address1.setId(null); // null - 应该被校验
        address1.setProvince("北京");
        emp1.setAddress1(address1);

        managerList1.add(emp1);
        department.setManagerList1(managerList1);

        return department;
    }

    private void simulateValidationRulesInit(ValidationContext context) {
        // 模拟：Employee类型，path=""，校验id不为null
        context.initRules(new MockValidationRules(
                new MockValidationRule(Employee.class, "",
                        new MockValidateWith(NotNullValidator.class,
                                new MockFieldConfig(new String[]{"id"}, "不能为 null")))
        ));
    }

    private void simulatePathExclusionRules(ValidationContext context) {
        context.initRules(new MockValidationRules(
                // 规则1：Employee, path="", 校验id和name
                new MockValidationRule(Employee.class, "",
                        new MockValidateWith(NotNullValidator.class,
                                new MockFieldConfig(new String[]{"id", "name"}, "不能为 null"))),
                // 规则2：Employee, path="managerList1", 校验phone和address
                new MockValidationRule(Employee.class, "managerList1",
                        new MockValidateWith(NotBlankValidator.class,
                                new MockFieldConfig(new String[]{"phone", "address"}, "不能为 blank")))
        ));
    }

    private void simulateCustomValidatorRule(ValidationContext context) {
        context.initRules(new MockValidationRules(
                new MockValidationRuleWithCustom(DepartmentCustomValidator.class)
        ));
    }

    private void simulateNestedPathRule(ValidationContext context) {
        context.initRules(new MockValidationRules(
                new MockValidationRule(Address.class, "managerList1.address1",
                        new MockValidateWith(NotNullValidator.class,
                                new MockFieldConfig(new String[]{"id"}, "id 不能为 null")))
        ));
    }

    // ===== 自定义校验器 =====

    public static class DepartmentCustomValidator implements CustomValidator<Department> {
        @Override
        public void validate(Department department, ValidationResult result) {
            if (department.getName() != null && department.getName().length() > 100) {
                result.addError("name", "部门名称长度不能超过100", department.getName());
            }
            if (department.getManagerList1() != null && department.getManagerList1().isEmpty()) {
                result.addError("managerList1", "管理员列表不能为空", null);
            }
        }
    }

    // ===== Mock注解类 =====

    private static class MockValidationRules implements ValidationRules {
        private final ValidationRule[] rules;

        MockValidationRules(ValidationRule... rules) {
            this.rules = rules;
        }

        @Override
        public Class<? extends java.lang.annotation.Annotation> annotationType() {
            return ValidationRules.class;
        }

        @Override
        public ValidationRule[] value() {
            return rules;
        }
    }

    private static class MockValidationRule implements ValidationRule {
        private final Class<?> type;
        private final String path;
        private final ValidateWith[] validators;

        MockValidationRule(Class<?> type, String path, ValidateWith... validators) {
            this.type = type;
            this.path = path;
            this.validators = validators;
        }

        @Override
        public Class<? extends java.lang.annotation.Annotation> annotationType() {
            return ValidationRule.class;
        }

        @Override
        public Class<?> type() { return type; }

        @Override
        public String path() { return path; }

        @Override
        public ValidateWith[] validators() { return validators; }

        @Override
        public Class<? extends CustomValidator> custom() { return CustomValidator.class; }
    }

    private static class MockValidationRuleWithCustom implements ValidationRule {
        private final Class<? extends CustomValidator> customClass;

        MockValidationRuleWithCustom(Class<? extends CustomValidator> customClass) {
            this.customClass = customClass;
        }

        @Override
        public Class<? extends java.lang.annotation.Annotation> annotationType() {
            return ValidationRule.class;
        }

        @Override
        public Class<?> type() { return Object.class; }

        @Override
        public String path() { return ""; }

        @Override
        public ValidateWith[] validators() {
            return new ValidateWith[0];
        }

        @Override
        public Class<? extends CustomValidator> custom() { return customClass; }
    }

    private static class MockValidateWith implements ValidateWith {
        private final Class<? extends FieldValidator> validator;
        private final FieldConfig[] fields;

        MockValidateWith(Class<? extends FieldValidator> validator,
                         FieldConfig... fields) {
            this.validator = validator;
            this.fields = fields;
        }

        @Override
        public Class<? extends java.lang.annotation.Annotation> annotationType() {
            return ValidateWith.class;
        }

        @Override
        public Class<? extends FieldValidator> validator() { return validator; }

        @Override
        public FieldConfig[] fields() { return fields; }

        @Override
        public String message() { return ""; }

        @Override
        public String[] params() { return new String[0]; }
    }

    private static class MockFieldConfig implements FieldConfig {
        private final String[] names;
        private final String message;

        MockFieldConfig(String[] names, String message) {
            this.names = names;
            this.message = message;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return FieldConfig.class;
        }

        @Override
        public String[] names() { return names; }

        @Override
        public String message() { return message; }

        @Override
        public String[] params() { return new String[0]; }
    }
}
