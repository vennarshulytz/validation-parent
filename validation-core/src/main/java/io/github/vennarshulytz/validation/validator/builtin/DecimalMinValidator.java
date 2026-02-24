package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.annotation.constraints.DecimalMinCheck;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 小数最小值校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class DecimalMinValidator extends AbstractDecimalValidator implements FieldValidator {

    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, boolean enableI18n) {
        if (value == null) {
            return null;
        }

        // String minStr = (String) params.get("value");
        // if (minStr == null || minStr.isEmpty()) {
        //     return null;
        // }

        Object minObject = params.get("value");
        if (minObject == null) {
            return null;
        }
        String minStr = minObject.toString();
        if (minStr.isEmpty()) {
            return null;
        }

        // boolean inclusive = !"false".equalsIgnoreCase(params.get("inclusive"));
        boolean inclusive;
        Object inclusiveObject = params.get("inclusive");
        if (inclusiveObject == null) {
            inclusive = true;
        } else if (inclusiveObject instanceof Boolean) {
            inclusive = (boolean) inclusiveObject;
        } else {
            inclusive = Boolean.parseBoolean((String) inclusiveObject);
        }

        // BigDecimal minValue;
        // try {
        //     minValue = new BigDecimal(minStr);
        // } catch (NumberFormatException e) {
        //     return "min参数格式错误";
        // }
        // value 参数格式错误可能会有异常，直接抛出
        BigDecimal minValue = new BigDecimal(minStr);

        BigDecimal actualValue = toBigDecimal(value);
        if (actualValue == null) {
            return null;
        }

        int comparison = actualValue.compareTo(minValue);
        boolean valid = inclusive ? comparison >= 0 : comparison > 0;

        if (!valid) {
            return getErrorMessage(MessageConstants.DecimalMin, params, enableI18n);
        }

        return null;
    }

    @Override
    public Map<String, Object> parseParams(Annotation annotation) {

        if (annotation instanceof DecimalMinCheck) {
            DecimalMinCheck decimalMinCheck = (DecimalMinCheck) annotation;
            Map<String, Object> params = new HashMap<>();
            params.put("message", decimalMinCheck.message());
            params.put("value", decimalMinCheck.value());
            params.put("inclusive", decimalMinCheck.inclusive());
            return params;
        }
        return Collections.emptyMap();
    }


    @Override
    public String getDefaultMessage() {
        // 必须大于(或等于){value}
        return "must be greater than ${inclusive == true ? 'or equal to ' : ''}{value}";
    }
}
