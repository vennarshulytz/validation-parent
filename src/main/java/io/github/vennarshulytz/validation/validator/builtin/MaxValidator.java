package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 最大值校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class MaxValidator extends AbstractRangeValidator implements FieldValidator {

    @Override
    public String validate(String fieldName, Object value, Map<String, String> params) {
        if (value == null) {
            return null;
        }

        String maxStr = params.get("value");
        if (maxStr == null || maxStr.isEmpty()) {
            return null;
        }

        long maxValue;
        try {
            maxValue = Long.parseLong(maxStr);
        } catch (NumberFormatException e) {
            return "max参数格式错误";
        }

        Long actualValue = toLong(value);
        if (actualValue == null) {
            // 尝试使用BigDecimal进行比较
            BigDecimal bd = toBigDecimal(value);
            if (bd != null && bd.compareTo(BigDecimal.valueOf(maxValue)) > 0) {
                return params.getOrDefault("message", getDefaultMessage())
                        .replace("{value}", maxStr);
            }
            return null;
        }

        if (actualValue > maxValue) {
            return params.getOrDefault("message", getDefaultMessage())
                    .replace("{value}", maxStr);
        }

        return null;
    }


    @Override
    public String getDefaultMessage() {
        return "必须小于或等于{value}";
    }
}
