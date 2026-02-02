package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.util.Collection;
import java.util.Map;

/**
 * 大小校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class SizeValidator implements FieldValidator {
    
    @Override
    public String validate(String fieldName, Object value, Map<String, String> params) {
        if (value == null) {
            return null;
        }
        
        int min = Integer.parseInt(params.getOrDefault("min", "0"));
        int max = Integer.parseInt(params.getOrDefault("max", String.valueOf(Integer.MAX_VALUE)));
        
        int size = getSize(value);
        
        if (size < min || size > max) {
            String message = params.get("message");
            if (message != null) {
                return message.replace("{min}", String.valueOf(min))
                              .replace("{max}", String.valueOf(max));
            }
            return "大小必须在 " + min + " 和 " + max + " 之间";
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
    public String getDefaultMessage() {
        return "大小不符合要求";
    }
}