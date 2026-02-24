package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.annotation.constraints.DigitsCheck;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 数字位数校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class DigitsValidator extends AbstractDecimalValidator implements FieldValidator {

    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, boolean enableI18n) {
        if (value == null) {
            return null;
        }

        // int maxInteger = Integer.parseInt(params.getOrDefault("integer", "0"));
        int maxInteger;
        Object maxIntegerObject = params.get("integer");
        if (maxIntegerObject == null) {
            maxInteger = 0;
        } else if (maxIntegerObject instanceof Integer) {
            maxInteger = (int) maxIntegerObject;
        } else {
            // integer 参数格式错误可能会有异常，直接抛出
            maxInteger = Integer.parseInt(maxIntegerObject.toString());
        }

        // int maxFraction = Integer.parseInt(params.getOrDefault("fraction", "0"));

        int maxFraction;
        Object maxFractionObject = params.get("fraction");
        if (maxFractionObject == null) {
            maxFraction = 0;
        } else if (maxFractionObject instanceof Integer) {
            maxFraction = (int) maxFractionObject;
        } else {
            // fraction 参数格式错误可能会有异常，直接抛出
            maxFraction = Integer.parseInt(maxFractionObject.toString());
        }



        BigDecimal bd = toBigDecimal(value);
        if (bd == null) {
            return getErrorMessage(MessageConstants.Digits, params, enableI18n);
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
            return getErrorMessage(MessageConstants.Digits, params, enableI18n);
        }

        return null;
    }

    @Override
    public Map<String, Object> parseParams(Annotation annotation) {

        if (annotation instanceof DigitsCheck) {
            DigitsCheck digitsCheck = (DigitsCheck) annotation;
            Map<String, Object> params = new HashMap<>();
            params.put("message", digitsCheck.message());
            params.put("integer", digitsCheck.integer());
            params.put("fraction", digitsCheck.fraction());
            return params;
        }
        return Collections.emptyMap();
    }


    @Override
    public String getDefaultMessage() {
        // 数字格式不正确（整数位最多{integer}位，小数位最多{fraction}位）
        return "numeric value out of bounds (<{integer} digits>.<{fraction} digits> expected)";
    }
}
