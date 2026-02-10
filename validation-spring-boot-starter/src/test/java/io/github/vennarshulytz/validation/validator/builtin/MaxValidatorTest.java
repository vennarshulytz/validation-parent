package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * MaxValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("MaxValidator 测试")
class MaxValidatorTest extends ValidatorTestBase {

    private MaxValidator validator;

    @BeforeEach
    void setUp() {
        validator = new MaxValidator();
    }

    @Test
    @DisplayName("null应该通过校验")
    void shouldPassWhenNull() {
        assertValid(validator, null, params("value", "100"));
    }

    @Test
    @DisplayName("小于等于最大值应该通过校验")
    void shouldPassWhenLessOrEqual() {
        assertValid(validator, 99, params("value", "100"));
        assertValid(validator, 100, params("value", "100"));
        assertValid(validator, 100L, params("value", "100"));
    }

    @Test
    @DisplayName("大于最大值应该校验失败")
    void shouldFailWhenGreater() {
        assertInvalid(validator, 101, params("value", "100"));
        assertInvalid(validator, 101L, params("value", "100"));
    }

    @Test
    @DisplayName("支持各种整数类型")
    void shouldSupportVariousIntegerTypes() {
        assertValid(validator, (byte) 50, params("value", "100"));
        assertValid(validator, (short) 50, params("value", "100"));
        assertValid(validator, 50, params("value", "100"));
        assertValid(validator, 50L, params("value", "100"));
    }

    @Test
    @DisplayName("支持BigDecimal和BigInteger")
    void shouldSupportBigTypes() {
        assertValid(validator, new BigDecimal("99"), params("value", "100"));
        assertValid(validator, new BigInteger("99"), params("value", "100"));
        assertInvalid(validator, new BigDecimal("101"), params("value", "100"));
    }

    @Test
    @DisplayName("支持负数")
    void shouldSupportNegativeNumbers() {
        assertValid(validator, -100, params("value", "-50"));
        assertInvalid(validator, -40, params("value", "-50"));
    }
}