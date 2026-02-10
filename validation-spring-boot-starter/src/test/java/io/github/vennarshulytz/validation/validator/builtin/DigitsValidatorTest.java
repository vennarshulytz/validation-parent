package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * DigitsValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("DigitsValidator 测试")
class DigitsValidatorTest extends ValidatorTestBase {

    private DigitsValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DigitsValidator();
    }

    @Test
    @DisplayName("null应该通过校验")
    void shouldPassWhenNull() {
        assertValid(validator, null, params("integer", "5", "fraction", "2"));
    }

    @Test
    @DisplayName("符合位数要求应该通过校验")
    void shouldPassWhenDigitsMatch() {
        assertValid(validator, 123, params("integer", "3", "fraction", "0"));
        assertValid(validator, 12.34, params("integer", "2", "fraction", "2"));
        assertValid(validator, new BigDecimal("123.45"), params("integer", "3", "fraction", "2"));
    }

    @Test
    @DisplayName("整数部分超出应该校验失败")
    void shouldFailWhenIntegerPartExceeds() {
        assertInvalid(validator, 1234, params("integer", "3", "fraction", "0"));
        assertInvalid(validator, 1234.56, params("integer", "3", "fraction", "2"));
    }

    @Test
    @DisplayName("小数部分超出应该校验失败")
    void shouldFailWhenFractionPartExceeds() {
        assertInvalid(validator, 12.345, params("integer", "5", "fraction", "2"));
        assertInvalid(validator, new BigDecimal("1.123"), params("integer", "5", "fraction", "2"));
    }

    @Test
    @DisplayName("纯小数应该正确校验")
    void shouldValidatePureDecimals() {
        assertValid(validator, 0.12, params("integer", "1", "fraction", "2"));
        assertValid(validator, new BigDecimal("0.99"), params("integer", "1", "fraction", "2"));
    }

    @Test
    @DisplayName("零应该通过校验")
    void shouldPassForZero() {
        assertValid(validator, 0, params("integer", "1", "fraction", "0"));
        assertValid(validator, 0.0, params("integer", "1", "fraction", "1"));
    }

    @Test
    @DisplayName("负数应该正确校验")
    void shouldValidateNegativeNumbers() {
        assertValid(validator, -123, params("integer", "3", "fraction", "0"));
        assertValid(validator, -12.34, params("integer", "2", "fraction", "2"));
        assertInvalid(validator, -1234, params("integer", "3", "fraction", "0"));
    }

    @Test
    @DisplayName("支持字符串类型数字")
    void shouldSupportStringNumbers() {
        assertValid(validator, "123.45", params("integer", "3", "fraction", "2"));
        assertInvalid(validator, "1234.567", params("integer", "3", "fraction", "2"));
    }
}