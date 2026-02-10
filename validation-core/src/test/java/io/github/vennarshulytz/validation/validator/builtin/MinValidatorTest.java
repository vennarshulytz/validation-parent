package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * MinValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("MinValidator 测试")
class MinValidatorTest extends ValidatorTestBase {

    private MinValidator validator;

    @BeforeEach
    void setUp() {
        validator = new MinValidator();
    }

    @Test
    @DisplayName("null应该通过校验")
    void shouldPassWhenNull() {
        assertValid(validator, null, params("value", "10"));
    }

    @Test
    @DisplayName("大于等于最小值应该通过校验")
    void shouldPassWhenGreaterOrEqual() {
        assertValid(validator, 11, params("value", "10"));
        assertValid(validator, 10, params("value", "10"));
        assertValid(validator, 10L, params("value", "10"));
    }

    @Test
    @DisplayName("小于最小值应该校验失败")
    void shouldFailWhenLess() {
        assertInvalid(validator, 9, params("value", "10"));
        assertInvalid(validator, 9L, params("value", "10"));
    }

    @Test
    @DisplayName("支持各种整数类型")
    void shouldSupportVariousIntegerTypes() {
        assertValid(validator, (byte) 50, params("value", "10"));
        assertValid(validator, (short) 50, params("value", "10"));
        assertValid(validator, 50, params("value", "10"));
        assertValid(validator, 50L, params("value", "10"));
    }

    @Test
    @DisplayName("支持BigDecimal和BigInteger")
    void shouldSupportBigTypes() {
        assertValid(validator, new BigDecimal("100"), params("value", "10"));
        assertValid(validator, new BigInteger("100"), params("value", "10"));
        assertInvalid(validator, new BigDecimal("5"), params("value", "10"));
    }

    @Test
    @DisplayName("支持负数")
    void shouldSupportNegativeNumbers() {
        assertValid(validator, -40, params("value", "-50"));
        assertInvalid(validator, -60, params("value", "-50"));
    }
}