package io.github.vennarshulytz.validation.validator.builtin;

import io.github.vennarshulytz.validation.utils.Decimals;
import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 小数最大值校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class DecimalMaxValidator implements FieldValidator {

    @Override
    public String validate(String fieldName, Object value, Map<String, String> params) {
        if (value == null) {
            return null;
        }

        String maxStr = params.get("value");
        if (maxStr == null || maxStr.isEmpty()) {
            return null;
        }

        boolean inclusive = !"false".equalsIgnoreCase(params.get("inclusive"));

        BigDecimal maxValue;
        try {
            maxValue = new BigDecimal(maxStr);
        } catch (NumberFormatException e) {
            return "max参数格式错误";
        }

        BigDecimal actualValue = Decimals.toBigDecimal(value);
        if (actualValue == null) {
            return null; // 无法转换的类型跳过校验
        }

        int comparison = actualValue.compareTo(maxValue);
        boolean valid = inclusive ? comparison <= 0 : comparison < 0;

        if (!valid) {
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

