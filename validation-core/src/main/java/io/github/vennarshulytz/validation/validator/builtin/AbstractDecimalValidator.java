package io.github.vennarshulytz.validation.validator.builtin;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 小数校验器基类
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public abstract class AbstractDecimalValidator {

    protected BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }
        if (value instanceof Long) {
            return BigDecimal.valueOf((Long) value);
        }
        if (value instanceof Integer) {
            return BigDecimal.valueOf((Integer) value);
        }
        if (value instanceof Double) {
            return BigDecimal.valueOf((Double) value);
        }
        if (value instanceof Float) {
            return BigDecimal.valueOf((Float) value);
        }
        if (value instanceof Short) {
            return BigDecimal.valueOf((Short) value);
        }
        if (value instanceof Byte) {
            return BigDecimal.valueOf((Byte) value);
        }
        if (value instanceof String) {
            try {
                return new BigDecimal((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
