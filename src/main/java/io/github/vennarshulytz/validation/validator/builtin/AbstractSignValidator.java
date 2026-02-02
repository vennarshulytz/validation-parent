package io.github.vennarshulytz.validation.validator.builtin;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 正负校验器基类
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public abstract class AbstractSignValidator {

    /**
     * 获取数值的符号：1正，0零，-1负，Integer.MIN_VALUE表示无法判断
     */
    protected int getSignum(Object value) {
        if (value instanceof Long) {
            Long l = (Long) value;
            return Long.signum(l);
        }
        if (value instanceof Integer) {
            Integer i = (Integer) value;
            return Integer.signum(i);
        }
        if (value instanceof Double) {
            Double d = (Double) value;
            return (int) Math.signum(d);
        }
        if (value instanceof Float) {
            Float f = (Float) value;
            return (int) Math.signum(f);
        }
        if (value instanceof Short) {
            Short s = (Short) value;
            return Integer.signum(s);
        }
        if (value instanceof Byte) {
            Byte b = (Byte) value;
            return Integer.signum(b);
        }
        if (value instanceof BigDecimal) {
            BigDecimal bd = (BigDecimal) value;
            return bd.signum();
        }
        if (value instanceof BigInteger) {
            BigInteger bi = (BigInteger) value;
            return bi.signum();
        }
        return Integer.MIN_VALUE;
    }
}
