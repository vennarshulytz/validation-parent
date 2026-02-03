package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.annotation.constraints.MinCheck;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 最小值校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class MinValidator extends AbstractRangeValidator implements FieldValidator {

    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, boolean enableI18n) {
        if (value == null) {
            return null;
        }

//        String minStr = params.get("value");
//        if (minStr == null || minStr.isEmpty()) {
//            return null;
//        }
//
//        long minValue;
//        try {
//            minValue = Long.parseLong(minStr);
//        } catch (NumberFormatException e) {
//            return "min参数格式错误";
//        }

        Object minObject = params.get("value");
        if (minObject == null) {
            return null;
        }
        long minValue;
        if (minObject instanceof Long) {
            minValue = (Long) minObject;
        } else {
            // value 参数格式错误可能会有异常，直接抛出
            minValue = Long.parseLong(minObject.toString());
        }

        Long actualValue = toLong(value);
        if (actualValue == null) {
            BigDecimal bd = toBigDecimal(value);
            if (bd != null && bd.compareTo(BigDecimal.valueOf(minValue)) < 0) {
                return getErrorMessage(MessageConstants.Min, params, enableI18n);

            }
            return null;
        }

        if (actualValue < minValue) {
            return getErrorMessage(MessageConstants.Min, params, enableI18n);

        }

        return null;
    }

    @Override
    public Map<String, Object> parseParams(Annotation annotation) {

        if (annotation instanceof MinCheck) {
            MinCheck minCheck = (MinCheck) annotation;
            Map<String, Object> params = new HashMap<>();
            params.put("message", minCheck.message());
            params.put("value", minCheck.value());
            return params;
        }
        return Collections.emptyMap();
    }

    @Override
    public String getDefaultMessage() {
        // 必须大于或等于{value}
        return "must be greater than or equal to {value}";
    }
}
