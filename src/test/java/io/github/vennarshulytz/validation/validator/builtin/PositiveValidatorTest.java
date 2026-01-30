package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * PositiveValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("PositiveValidator 测试")
class PositiveValidatorTest extends ValidatorTestBase {

    private PositiveValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PositiveValidator();
    }

    @Test
    @DisplayName("null应该通过校验")
    void shouldPassWhenNull() {
        assertValid(validator, null);
    }

    @Test
    @DisplayName("正数应该通过校验")
    void shouldPassWhenPositive() {
        assertValid(validator, 1);
        assertValid(validator, 100L);
        assertValid(validator, 0.001);
        assertValid(validator, 0.001f);
        assertValid(validator, new BigDecimal("0.001"));
        assertValid(validator, new BigInteger("1"));
    }

    @Test
    @DisplayName("零应该校验失败")
    void shouldFailWhenZero() {
        assertInvalid(validator, 0);
        assertInvalid(validator, 0L);
        assertInvalid(validator, 0.0);
        assertInvalid(validator, BigDecimal.ZERO);
    }

    @Test
    @DisplayName("负数应该校验失败")
    void shouldFailWhenNegative() {
        assertInvalid(validator, -1);
        assertInvalid(validator, -100L);
        assertInvalid(validator, -0.001);
        assertInvalid(validator, new BigDecimal("-0.001"));
    }

    @Test
    @DisplayName("支持各种数值类型")
    void shouldSupportVariousNumericTypes() {
        assertValid(validator, (byte) 1);
        assertValid(validator, (short) 1);
        assertValid(validator, 1);
        assertValid(validator, 1L);
        assertValid(validator, 1.0f);
        assertValid(validator, 1.0d);
    }
}