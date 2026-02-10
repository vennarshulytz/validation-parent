package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * NegativeValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("NegativeValidator 测试")
class NegativeValidatorTest extends ValidatorTestBase {

    private NegativeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new NegativeValidator();
    }

    @Test
    @DisplayName("null应该通过校验")
    void shouldPassWhenNull() {
        assertValid(validator, null);
    }

    @Test
    @DisplayName("负数应该通过校验")
    void shouldPassWhenNegative() {
        assertValid(validator, -1);
        assertValid(validator, -100L);
        assertValid(validator, -0.001);
        assertValid(validator, -0.001f);
        assertValid(validator, new BigDecimal("-0.001"));
        assertValid(validator, new BigInteger("-1"));
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
    @DisplayName("正数应该校验失败")
    void shouldFailWhenPositive() {
        assertInvalid(validator, 1);
        assertInvalid(validator, 100L);
        assertInvalid(validator, 0.001);
        assertInvalid(validator, new BigDecimal("0.001"));
    }
}