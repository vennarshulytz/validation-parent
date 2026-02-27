package io.github.vennarshulytz.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vennarshulytz.validation.annotation.*;
import io.github.vennarshulytz.validation.annotation.constraints.NotNullCheck;
import io.github.vennarshulytz.validation.core.ValidationMode;
import io.github.vennarshulytz.validation.exception.ValidationException;
import io.github.vennarshulytz.validation.model.Address;
import io.github.vennarshulytz.validation.model.Department;
import io.github.vennarshulytz.validation.model.Employee;
import io.github.vennarshulytz.validation.validator.CustomValidator;
import io.github.vennarshulytz.validation.validator.ValidationResult;
import io.github.vennarshulytz.validation.validator.builtin.NotBlankValidator;
import io.github.vennarshulytz.validation.validator.builtin.NotNullValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web层集成测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@SpringBootTest(classes = ValidationWebIntegrationTest.TestApplication.class)
@AutoConfigureMockMvc
class ValidationWebIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Web测试 - 校验通过")
    void testValidation_Pass() throws Exception {
        Department department = createValidDepartment();

        mockMvc.perform(post("/test/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("tag", "true")
                        .content(objectMapper.writeValueAsString(department)))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));
    }

    @Test
    @DisplayName("Web测试 - 校验失败")
    void testValidation_Fail() throws Exception {
        Department department = createInvalidDepartment();

        mockMvc.perform(post("/test/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("tag", "true")
                        .content(objectMapper.writeValueAsString(department)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("Web测试 - @NotNullCheck校验")
    void testNotNullCheck() throws Exception {
        Department department = createValidDepartment();

        // 不传tag参数
        mockMvc.perform(post("/test/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(department)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("tag"));
    }

    @Test
    @DisplayName("Web测试 - 路径排除逻辑")
    void testPathExclusion() throws Exception {
        Department department = new Department();
        department.setName("技术部");

        // manager1 - id为null
        Employee manager1 = new Employee();
        manager1.setId(null); // 应该被path=""规则校验
        manager1.setName("经理");
        manager1.setPhone("13800000001");
        manager1.setNumber("001");
        department.setManager1(manager1);

        // managerList1 - 正常数据
        List<Employee> managerList1 = new ArrayList<>();
        Employee emp1 = new Employee();
        emp1.setId("e001");
        emp1.setName("员工1");
        emp1.setPhone("13800000002");
        emp1.setAddress("地址1");
        Address addr1 = new Address();
        addr1.setId("a001");
        emp1.setAddress1(addr1);
        managerList1.add(emp1);
        department.setManagerList1(managerList1);

        mockMvc.perform(post("/test/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("tag", "true")
                        .content(objectMapper.writeValueAsString(department)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("manager1.id"));
    }

    private Department createValidDepartment() {
        Department department = new Department();
        department.setName("技术部");

        Employee manager1 = new Employee();
        manager1.setId("m001");
        manager1.setName("经理1");
        manager1.setPhone("13800000001");
        manager1.setNumber("001");
        department.setManager1(manager1);

        List<Employee> managerList1 = new ArrayList<>();
        Employee emp1 = new Employee();
        emp1.setId("e001");
        emp1.setName("员工1");
        emp1.setPhone("13800000002");
        emp1.setAddress("地址1");
        Address addr1 = new Address();
        addr1.setId("a001");
        addr1.setProvince("北京");
        emp1.setAddress1(addr1);
        managerList1.add(emp1);
        department.setManagerList1(managerList1);

        return department;
    }

    private Department createInvalidDepartment() {
        Department department = new Department();
        department.setName("技术部");

        Employee manager1 = new Employee();
        manager1.setId(null); // invalid
        manager1.setName("经理1");
        manager1.setPhone("13800000001");
        manager1.setNumber("001");
        department.setManager1(manager1);

        return department;
    }

    // ===== 测试应用 =====

    //    @SpringBootApplication
    @Configuration
    @EnableAutoConfiguration
    @EnableValidation(mode = ValidationMode.FAIL_FAST, enableI18n = false)
    static class TestApplication {

        @Bean
        public TestController testController() {
            return new TestController();
        }

        @Bean
        public TestDepartmentCustomValidator testDepartmentCustomValidator() {
            return new TestDepartmentCustomValidator();
        }

        @Bean
        public TestExceptionHandler testExceptionHandler() {
            return new TestExceptionHandler();
        }
    }

    @ValidatedExt
    @RestController
    @RequestMapping("/test")
    static class TestController {

        @PostMapping("/save")
        public String save(
                @RequestBody
                @ValidationRules({
                        @ValidationRule(type = Employee.class, validators = {
                                @ValidateWith(validator = NotNullValidator.class,
                                        fields = {@FieldConfig(names = {"id", "name"}, message = "不能为 null")}),
                                @ValidateWith(validator = NotNullValidator.class,
                                        fields = {
                                                @FieldConfig(names = {"phone"}, message = "phone 不能为 null"),
                                                @FieldConfig(names = {"number"}, message = "number 不能为 null")
                                        })
                        }),
                        @ValidationRule(type = Employee.class, path = "managerList1", validators = {
                                @ValidateWith(validator = NotBlankValidator.class,
                                        fields = {
                                                @FieldConfig(names = {"phone"}, message = "phone 不能为 blank"),
                                                @FieldConfig(names = {"address"}, message = "address 不能为 blank")
                                        })
                        }),
                        @ValidationRule(type = Address.class, path = "managerList1.address1", validators = {
                                @ValidateWith(validator = NotNullValidator.class,
                                        fields = {@FieldConfig(names = {"id"}, message = "id 不能为 null")})
                        }),
                        @ValidationRule(custom = TestDepartmentCustomValidator.class)
                }) Department department,
                @NotNullCheck(message = "tag 不能为 null") @RequestParam(required = false) Boolean tag) {

            return "success";
        }
    }

    @Component
    static class TestDepartmentCustomValidator implements CustomValidator<Department> {
        @Override
        public void validate(Department department, ValidationResult result) {
            if (department.getName() != null && department.getName().length() > 100) {
                result.addError("name", "部门名称长度不能超过100", department.getName());
            }
        }
    }

    @RestControllerAdvice
    static class TestExceptionHandler {
        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", "参数校验失败");

            List<Map<String, Object>> errors = new ArrayList<>();
            for (ValidationResult.FieldError error : ex.getErrors()) {
                Map<String, Object> errorMap = new HashMap<>();
                errorMap.put("field", error.getField());
                errorMap.put("message", error.getMessage());
                errorMap.put("rejectedValue", error.getRejectedValue());
                errors.add(errorMap);
            }
            response.put("errors", errors);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
