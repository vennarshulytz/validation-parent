package io.github.vennarshulytz.validation.core;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.annotation.ValidationRule;
import io.github.vennarshulytz.validation.annotation.ValidationRules;
import io.github.vennarshulytz.validation.template.ValidationRuleTemplate;
import io.github.vennarshulytz.validation.validator.CustomValidator;
import io.github.vennarshulytz.validation.validator.ValidationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 校验上下文测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
class ValidationContextTest {

    @Test
    @DisplayName("测试快速失败模式")
    void testFailFastMode() {
        ValidationContext context = new ValidationContext(ValidationMode.FAIL_FAST, false);
        ValidationResult result = context.getResult();

        result.addError("field1", "error1", null);

        assertTrue(result.shouldStop());
        assertEquals(1, result.getErrors().size());
    }

    @Test
    @DisplayName("测试全量校验模式")
    void testFailAllMode() {
        ValidationContext context = new ValidationContext(ValidationMode.FAIL_ALL, false);
        ValidationResult result = context.getResult();

        result.addError("field1", "error1", null);
        result.addError("field2", "error2", null);

        assertFalse(result.shouldStop());
        assertEquals(2, result.getErrors().size());
    }

    @Test
    @DisplayName("测试排除路径收集")
    void testExcludedPathsCollection() {
        ValidationContext context = new ValidationContext(ValidationMode.FAIL_FAST, false);

        // 模拟初始化规则
        ValidationRuleCache ruleCache = new ValidationRuleCache(1024);

        ValidationRules rules = createMockValidationRules();
        ValidationRuleCache.CachedRuleInfo cachedRuleInfo = ruleCache.parseRules(rules);
        context.setCachedRuleInfo(cachedRuleInfo);


        // 验证排除路径
        Set<String> excludedPaths = context.getExcludedPaths(MockEmployee.class);
        assertTrue(excludedPaths.contains("managerList1"));
    }

    // 创建模拟的ValidationRules
    private ValidationRules createMockValidationRules() {
        return new ValidationRules() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return ValidationRules.class;
            }

            @Override
            public Class<? extends ValidationRuleTemplate> template() {
                return ValidationRuleTemplate.class;
            }

            @Override
            public ValidationRule[] value() {
                return new ValidationRule[]{
                        createMockRule(MockEmployee.class, ""),
                        createMockRule(MockEmployee.class, "managerList1")
                };
            }
        };
    }

    private ValidationRule createMockRule(Class<?> type, String path) {
        return new ValidationRule() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return ValidationRule.class;
            }

            @Override
            public Class<?> type() {
                return type;
            }

            @Override
            public String path() {
                return path;
            }

            @Override
            public ValidateWith[] validators() {
                return new ValidateWith[0];
            }

            @Override
            public Class<? extends CustomValidator> custom() {
                return CustomValidator.class;
            }
        };
    }

    // 模拟类
    private static class MockEmployee {}
}