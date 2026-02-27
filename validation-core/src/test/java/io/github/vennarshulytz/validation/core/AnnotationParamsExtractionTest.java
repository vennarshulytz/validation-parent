package io.github.vennarshulytz.validation.core;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.annotation.constraints.*;
import io.github.vennarshulytz.validation.validator.builtin.EmailValidator;
import io.github.vennarshulytz.validation.validator.builtin.MaxValidator;
import io.github.vennarshulytz.validation.validator.builtin.MinValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 注解参数提取测试
 */
@DisplayName("注解参数提取测试")
class AnnotationParamsExtractionTest {

    // private ValidationEngine validationEngine;
    //
    // @BeforeEach
    // void setUp() {
    //     BeanFactory beanFactory = mock(BeanFactory.class);
    //     ValidatorRegistry validatorRegistry = new ValidatorRegistry(beanFactory);
    //     MessageResolver messageResolver = new MessageResolver(null, false);
    //     validationEngine = new ValidationEngine(validatorRegistry, messageResolver);
    // }

    // 用于测试的方法
    public void testMethod(
            @MaxCheck(value = 100, message = "最大值不能超过100") Integer maxValue,
            @MinCheck(value = 10, message = "最小值不能小于10") Integer minValue,
            @SizeCheck(min = 5, max = 20, message = "长度必须在5-20之间") String sizeValue,
            @DecimalMaxCheck(value = "99.99", inclusive = false) Double decimalMax,
            @DigitsCheck(integer = 5, fraction = 2) Double digits
    ) {
    }

    @Test
    @DisplayName("MaxCheck注解参数提取")
    void shouldExtractMaxCheckParams() throws Exception {
        Method method = getClass().getMethod("testMethod", Integer.class, Integer.class, 
                String.class, Double.class, Double.class);
        Annotation[] annotations = method.getParameterAnnotations()[0];
        MaxCheck maxCheck = (MaxCheck) annotations[0];

        assertEquals(100, maxCheck.value());
        assertEquals("最大值不能超过100", maxCheck.message());
    }

    @Test
    @DisplayName("MinCheck注解参数提取")
    void shouldExtractMinCheckParams() throws Exception {
        Method method = getClass().getMethod("testMethod", Integer.class, Integer.class, 
                String.class, Double.class, Double.class);
        Annotation[] annotations = method.getParameterAnnotations()[1];
        MinCheck minCheck = (MinCheck) annotations[0];

        assertEquals(10, minCheck.value());
        assertEquals("最小值不能小于10", minCheck.message());
    }

    @Test
    @DisplayName("SizeCheck注解参数提取")
    void shouldExtractSizeCheckParams() throws Exception {
        Method method = getClass().getMethod("testMethod", Integer.class, Integer.class, 
                String.class, Double.class, Double.class);
        Annotation[] annotations = method.getParameterAnnotations()[2];
        SizeCheck sizeCheck = (SizeCheck) annotations[0];

        assertEquals(5, sizeCheck.min());
        assertEquals(20, sizeCheck.max());
        assertEquals("长度必须在5-20之间", sizeCheck.message());
    }

    @Test
    @DisplayName("DecimalMaxCheck注解参数提取")
    void shouldExtractDecimalMaxCheckParams() throws Exception {
        Method method = getClass().getMethod("testMethod", Integer.class, Integer.class, 
                String.class, Double.class, Double.class);
        Annotation[] annotations = method.getParameterAnnotations()[3];
        DecimalMaxCheck decimalMaxCheck = (DecimalMaxCheck) annotations[0];

        assertEquals("99.99", decimalMaxCheck.value());
        assertFalse(decimalMaxCheck.inclusive());
    }

    @Test
    @DisplayName("DigitsCheck注解参数提取")
    void shouldExtractDigitsCheckParams() throws Exception {
        Method method = getClass().getMethod("testMethod", Integer.class, Integer.class, 
                String.class, Double.class, Double.class);
        Annotation[] annotations = method.getParameterAnnotations()[4];
        DigitsCheck digitsCheck = (DigitsCheck) annotations[0];

        assertEquals(5, digitsCheck.integer());
        assertEquals(2, digitsCheck.fraction());
    }

    @Test
    @DisplayName("ValidateWith元注解正确配置")
    void shouldHaveCorrectValidateWithMetaAnnotation() {
        assertTrue(MaxCheck.class.isAnnotationPresent(ValidateWith.class));
        ValidateWith validateWith = MaxCheck.class.getAnnotation(ValidateWith.class);
        assertEquals(MaxValidator.class, validateWith.validator());

        assertTrue(MinCheck.class.isAnnotationPresent(ValidateWith.class));
        validateWith = MinCheck.class.getAnnotation(ValidateWith.class);
        assertEquals(MinValidator.class, validateWith.validator());

        assertTrue(EmailCheck.class.isAnnotationPresent(ValidateWith.class));
        validateWith = EmailCheck.class.getAnnotation(ValidateWith.class);
        assertEquals(EmailValidator.class, validateWith.validator());
    }
}