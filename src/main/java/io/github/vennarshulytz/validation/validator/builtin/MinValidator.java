package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 最小值校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class MinValidator extends AbstractRangeValidator implements FieldValidator {

    @Override
    public String validate(String fieldName, Object value, Map<String, String> params) {
        if (value == null) {
            return null;
        }

        String minStr = params.get("value");
        if (minStr == null || minStr.isEmpty()) {
            return null;
        }

        long minValue;
        try {
            minValue = Long.parseLong(minStr);
        } catch (NumberFormatException e) {
            return "min参数格式错误";
        }

        Long actualValue = toLong(value);
        if (actualValue == null) {
            BigDecimal bd = toBigDecimal(value);
            if (bd != null && bd.compareTo(BigDecimal.valueOf(minValue)) < 0) {
                return params.getOrDefault("message", getDefaultMessage())
                        .replace("{value}", minStr);
            }
            return null;
        }

        if (actualValue < minValue) {
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
