package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * PositiveOrZeroValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("PositiveOrZeroValidator 测试")
class PositiveOrZeroValidatorTest extends ValidatorTestBase {

    private PositiveOrZeroValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PositiveOrZeroValidator();
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
    }

    @Test
    @DisplayName("零应该通过校验")
    void shouldPassWhenZero() {
        assertValid(validator, 0);
        assertValid(validator, 0L);
        assertValid(validator, 0.0);
        assertValid(validator, BigDecimal.ZERO);
    }

    @Test
    @DisplayName("负数应该校验失败")
    void shouldFailWhenNegative() {
        assertInvalid(validator, -1);
        assertInvalid(validator, -100L);
        assertInvalid(validator, -0.001);
        assertInvalid(validator, new BigDecimal("-0.001"));
    }
}