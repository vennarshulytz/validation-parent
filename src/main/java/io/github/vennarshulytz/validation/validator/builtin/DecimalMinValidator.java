package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.utils.Decimals;
import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 小数最小值校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class DecimalMinValidator implements FieldValidator {

    @Override
    public String validate(String fieldName, Object value, Map<String, String> params) {
        if (value == null) {
            return null;
        }

        String minStr = params.get("value");
        if (minStr == null || minStr.isEmpty()) {
            return null;
        }

        boolean inclusive = !"false".equalsIgnoreCase(params.get("inclusive"));

        BigDecimal minValue;
        try {
            minValue = new BigDecimal(minStr);
        } catch (NumberFormatException e) {
            return "min参数格式错误";
        }

        BigDecimal actualValue = Decimals.toBigDecimal(value);
        if (actualValue == null) {
            return null;
        }

        int comparison = actualValue.compareTo(minValue);
        boolean valid = inclusive ? comparison >= 0 : comparison > 0;

        if (!valid) {
            return params.getOrDefault("message", getDefaultMessage())
                    .replace("{value}", minStr);
        }

        return null;
    }

    @Override
    public String getDefaultMessage() {
        return "必须大于或等于{value}";
    }
}
