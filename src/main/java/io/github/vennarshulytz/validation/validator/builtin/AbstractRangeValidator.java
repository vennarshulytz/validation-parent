package io.github.vennarshulytz.validation.validator.builtin;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 范围校验器基类
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public abstract class AbstractRangeValidator {

    protected Long toLong(Object value) {
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Integer) {
            Integer i = (Integer) value;
            return i.longValue();
        }
        if (value instanceof Short) {
            Short s = (Short) value;
            return s.longValue();
        }
        if (value instanceof Byte) {
            Byte b = (Byte) value;
            return b.longValue();
        }
        return null;
    }

    protected BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof BigInteger) {
            BigInteger bi = (BigInteger) value;
            return new BigDecimal(bi);
        }
        if (value instanceof Double) {
            Double d = (Double) value;
            return BigDecimal.valueOf(d);
        }
        if (value instanceof Float) {
            Float f = (Float) value;
            return BigDecimal.valueOf(f);
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
