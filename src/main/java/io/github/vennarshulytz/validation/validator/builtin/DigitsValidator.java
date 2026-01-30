package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.utils.Decimals;
import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 数字位数校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class DigitsValidator implements FieldValidator {

    @Override
    public String validate(String fieldName, Object value, Map<String, String> params) {
        if (value == null) {
            return null;
        }

        int maxInteger = Integer.parseInt(params.getOrDefault("integer", "0"));
        int maxFraction = Integer.parseInt(params.getOrDefault("fraction", "0"));

        BigDecimal bd = Decimals.toBigDecimal(value);
        if (bd == null) {
            return params.getOrDefault("message", getDefaultMessage())
                    .replace("{integer}", String.valueOf(maxInteger))
                    .replace("{fraction}", String.valueOf(maxFraction));
        }

        // 去除尾部零
        bd = bd.stripTrailingZeros();

        int integerPartLength;
        int fractionPartLength;

        // 处理科学计数法表示
        if (bd.scale() <= 0) {
            // 没有小数部分
            integerPartLength = bd.precision() + (-bd.scale());
            fractionPartLength = 0;
        } else {
            // 有小数部分
            integerPartLength = bd.precision() - bd.scale();
            fractionPartLength = bd.scale();
        }

        // 处理纯小数情况（如0.123）
        if (integerPartLength < 0) {
            integerPartLength = 0;
        }

        if (integerPartLength > maxInteger || fractionPartLength > maxFraction) {
            return params.getOrDefault("message", getDefaultMessage())
                    .replace("{integer}", String.valueOf(maxInteger))
                    .replace("{fraction}", String.valueOf(maxFraction));
        }

        return null;
    }

    @Override
    public String getDefaultMessage() {
        return "数字格式不正确（整数位最多{integer}位，小数位最多{fraction}位）";
    }
}
