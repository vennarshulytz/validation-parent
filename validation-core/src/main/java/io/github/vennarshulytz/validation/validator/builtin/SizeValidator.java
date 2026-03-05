package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.annotation.constraints.SizeCheck;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 大小校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class SizeValidator implements FieldValidator {
    
    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, boolean enableI18n) {
        if (value == null) {
            return null;
        }
        
        // int min = Integer.parseInt(params.getOrDefault("min", "0"));
        // int max = Integer.parseInt(params.getOrDefault("max", String.valueOf(Integer.MAX_VALUE)));

        int min;
        Object minObject = params.get("min");
        if (minObject == null) {
            min = 0;
        } else if (minObject instanceof Integer) {
            min = (int) minObject;
        } else {
            // min 参数格式错误可能会有异常，直接抛出
            min = Integer.parseInt(minObject.toString());
        }

        int max;
        Object maxObject = params.get("max");
        if (maxObject == null) {
            max = Integer.MAX_VALUE;
        } else if (maxObject instanceof Integer) {
            max = (int) maxObject;
        } else {
            // max 参数格式错误可能会有异常，直接抛出
            max = Integer.parseInt(maxObject.toString());

        }

        int size = getSize(value);
        
        if (size < min || size > max) {
            return getErrorMessage(MessageConstants.Size, params, enableI18n);

        }
        
        return null;
    }

    private int getSize(Object value) {
        if (value instanceof String) {
            String str = (String) value;
            return str.length();
        }
        if (value instanceof Collection<?>) {
            Collection<?> col = (Collection<?>) value;
            return col.size();
        }
        if (value instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) value;
            return map.size();
        }
        if (value.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(value);
        }
        return 0;
    }

    @Override
    public Map<String, Object> parseParams(Annotation annotation) {

        if (annotation instanceof SizeCheck) {
            SizeCheck sizeCheck = (SizeCheck) annotation;
            Map<String, Object> params = new HashMap<>(5);
            params.put("message", sizeCheck.message());
            params.put("min", sizeCheck.min());
            params.put("max", sizeCheck.max());
            return params;
        }
        return Collections.emptyMap();
    }

    @Override
    public String getDefaultMessage() {
        // 大小不符合要求
        return "size must be between {min} and {max}";
    }
}