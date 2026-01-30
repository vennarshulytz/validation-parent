package io.github.vennarshulytz.validation.validator.builtin;

import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.util.Collection;
import java.util.Map;

/**
 * 非空集合校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class NotEmptyValidator implements FieldValidator {

    @Override
    public String validate(String fieldName, Object value, Map<String, String> params) {
        if (value == null) {
            return params.getOrDefault("message", getDefaultMessage());
        }

        if (value instanceof String) {
            String str = (String) value;
            if (str.isEmpty()) {
                return params.getOrDefault("message", getDefaultMessage());
            }
        }

        if (value instanceof Collection<?>) {
            Collection<?> col = (Collection<?>) value;
            if (col.isEmpty()) {
                return params.getOrDefault("message", getDefaultMessage());
            }
        }

        if (value instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) value;
            if (map.isEmpty()) {
                return params.getOrDefault("message", getDefaultMessage());
            }

        }

        if (value.getClass().isArray()) {
            if (java.lang.reflect.Array.getLength(value) == 0) {
                return params.getOrDefault("message", getDefaultMessage());
            }
        }

        return null;
    }

    @Override
    public String getDefaultMessage() {
        return "不能为空";
    }
}
