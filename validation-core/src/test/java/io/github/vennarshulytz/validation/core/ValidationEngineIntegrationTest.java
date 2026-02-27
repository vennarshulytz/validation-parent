package io.github.vennarshulytz.validation.core;

import com.google.common.collect.ImmutableMap;
import io.github.vennarshulytz.validation.i18n.MessageResolver;
import io.github.vennarshulytz.validation.validator.FieldValidator;
import io.github.vennarshulytz.validation.validator.ValidationResult;
import io.github.vennarshulytz.validation.validator.builtin.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * ValidationEngine 集成测试
 */
@DisplayName("ValidationEngine 集成测试")
class ValidationEngineIntegrationTest {

    // private ValidationEngine validationEngine;
    private ValidatorRegistry validatorRegistry;

    @BeforeEach
    void setUp() {
        BeanFactory beanFactory = mock(BeanFactory.class);
        when(beanFactory.getBean(any(Class.class)))
                .thenThrow(new NoSuchBeanDefinitionException("mocked"));
        // when(beanFactory.getBean(any(Class.class)))
        //         .thenAnswer(invocation -> {
        //             Class<?> requiredType = invocation.getArgument(0);
        //             throw new NoSuchBeanDefinitionException(requiredType);
        //         });
        validatorRegistry = new ValidatorRegistry(beanFactory);
        MessageResolver messageResolver = new MessageResolver(null, false);
        // validationEngine = new ValidationEngine(validatorRegistry, messageResolver);
    }

    @Test
    @DisplayName("多个校验器组合使用")
    void shouldValidateWithMultipleValidators() {

        Map<String, Object> params = ImmutableMap.of("message", "mocked");

        // 测试 NotNull
        NotNullValidator notNullValidator = validatorRegistry.getFieldValidator(NotNullValidator.class);
        assertNull(notNullValidator.validate("field", "value", params, false));
        assertNotNull(notNullValidator.validate("field", null, params, false));

        // 测试 Positive
        PositiveValidator positiveValidator = validatorRegistry.getFieldValidator(PositiveValidator.class);
        assertNull(positiveValidator.validate("field", 100, params, false));
        assertNotNull(positiveValidator.validate("field", -1, params, false));

        // 测试 Email
        EmailValidator emailValidator = validatorRegistry.getFieldValidator(EmailValidator.class);
        assertNull(emailValidator.validate("field", "test@example.com", params, false));
        assertNotNull(emailValidator.validate("field", "invalid", params, false));
    }

    @Test
    @DisplayName("校验复杂对象场景")
    void shouldValidateComplexScenario() {
        // 模拟订单校验场景
        Map<String, Object> params = new HashMap<>();
        params.put("message", "mocked");

        // 订单金额必须为正数
        PositiveValidator positiveValidator = validatorRegistry.getFieldValidator(PositiveValidator.class);
        assertNull(positiveValidator.validate("amount", new BigDecimal("99.99"), params, false));
        assertNotNull(positiveValidator.validate("amount", new BigDecimal("-1"), params, false));

        // 订单数量必须在1-999之间
        MinValidator minValidator = validatorRegistry.getFieldValidator(MinValidator.class);
        MaxValidator maxValidator = validatorRegistry.getFieldValidator(MaxValidator.class);

        params.put("message", "mocked");
        params.put("value", "1");
        assertNull(minValidator.validate("quantity", 10, params, false));
        assertNotNull(minValidator.validate("quantity", 0, params, false));

        params.clear();
        params.put("message", "mocked");
        params.put("value", "999");
        assertNull(maxValidator.validate("quantity", 100, params, false));
        assertNotNull(maxValidator.validate("quantity", 1000, params, false));

        // 预约时间必须是将来
        FutureValidator futureValidator = validatorRegistry.getFieldValidator(FutureValidator.class);
        assertNull(futureValidator.validate("appointmentTime", LocalDateTime.now().plusDays(1), params, false));
        assertNotNull(futureValidator.validate("appointmentTime", LocalDateTime.now().minusDays(1), params, false));
    }

    @Test
    @DisplayName("ValidationContext快速失败模式")
    void shouldStopOnFirstErrorInFailFastMode() {
        ValidationContext context = new ValidationContext(ValidationMode.FAIL_FAST, false);
        ValidationResult result = context.getResult();

        result.addError("field1", "error1", null);
        assertTrue(result.shouldStop());
        assertEquals(1, result.getErrors().size());
    }

    @Test
    @DisplayName("ValidationContext收集全部错误模式")
    void shouldCollectAllErrorsInFailAllMode() {
        ValidationContext context = new ValidationContext(ValidationMode.FAIL_ALL, false);
        ValidationResult result = context.getResult();

        result.addError("field1", "error1", null);
        assertFalse(result.shouldStop());

        result.addError("field2", "error2", null);
        assertFalse(result.shouldStop());

        assertEquals(2, result.getErrors().size());
    }

    @Test
    @DisplayName("校验器实例缓存")
    void shouldCacheValidatorInstances() {
        FieldValidator validator1 = validatorRegistry.getFieldValidator(NotNullValidator.class);
        FieldValidator validator2 = validatorRegistry.getFieldValidator(NotNullValidator.class);

        assertSame(validator1, validator2, "校验器实例应该被缓存复用");
    }

    @Test
    @DisplayName("所有内置校验器都能正确获取")
    void shouldGetAllBuiltinValidators() {
        assertNotNull(validatorRegistry.getFieldValidator(NotNullValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(NullValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(NotBlankValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(NotEmptyValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(DecimalMaxValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(DecimalMinValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(DigitsValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(MaxValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(MinValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(PositiveValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(PositiveOrZeroValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(NegativeValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(NegativeOrZeroValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(AssertTrueValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(AssertFalseValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(SizeValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(FutureValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(FutureOrPresentValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(PastValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(PastOrPresentValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(EmailValidator.class));
        assertNotNull(validatorRegistry.getFieldValidator(PatternValidator.class));
    }
}