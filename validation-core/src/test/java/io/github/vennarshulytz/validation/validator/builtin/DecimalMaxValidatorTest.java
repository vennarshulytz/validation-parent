package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * DecimalMaxValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("DecimalMaxValidator 测试")
class DecimalMaxValidatorTest extends ValidatorTestBase {

    private DecimalMaxValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DecimalMaxValidator();
    }

    @Test
    @DisplayName("null应该通过校验")
    void shouldPassWhenNull() {
        assertValid(validator, null, params("value", "100"));
    }

    @Test
    @DisplayName("小于最大值应该通过校验")
    void shouldPassWhenLessThanMax() {
        assertValid(validator, 99, params("value", "100"));
        assertValid(validator, 99.99, params("value", "100"));
        assertValid(validator, new BigDecimal("99.999"), params("value", "100"));
    }

    @Test
    @DisplayName("等于最大值（包含边界）应该通过校验")
    void shouldPassWhenEqualToMaxInclusive() {
        assertValid(validator, 100, params("value", "100", "inclusive", "true"));
        assertValid(validator, new BigDecimal("100.00"), params("value", "100"));
    }

    @Test
    @DisplayName("等于最大值（不包含边界）应该校验失败")
    void shouldFailWhenEqualToMaxExclusive() {
        assertInvalid(validator, 100, params("value", "100", "inclusive", "false"));
    }

    @Test
    @DisplayName("大于最大值应该校验失败")
    void shouldFailWhenGreaterThanMax() {
        assertInvalid(validator, 101, params("value", "100"));
        assertInvalid(validator, 100.01, params("value", "100"));
        assertInvalid(validator, new BigDecimal("100.001"), params("value", "100"));
    }

    @Test
    @DisplayName("支持各种数值类型")
    void shouldSupportVariousNumericTypes() {
        assertValid(validator, (byte) 50, params("value", "100"));
        assertValid(validator, (short) 50, params("value", "100"));
        assertValid(validator, 50, params("value", "100"));
        assertValid(validator, 50L, params("value", "100"));
        assertValid(validator, 50.0f, params("value", "100"));
        assertValid(validator, 50.0d, params("value", "100"));
        assertValid(validator, new BigInteger("50"), params("value", "100"));
        assertValid(validator, new BigDecimal("50"), params("value", "100"));
        assertValid(validator, "50", params("value", "100"));
    }

    @Test
    @DisplayName("支持小数最大值")
    void shouldSupportDecimalMax() {
        assertValid(validator, 99.99, params("value", "99.999"));
        assertInvalid(validator, 100.0, params("value", "99.999"));
    }
}