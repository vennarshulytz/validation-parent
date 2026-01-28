package io.github.vennarshulytz.validation.validator.builtin;

import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.util.Map;

/**
 * 非空白字符串校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class NotBlankValidator implements FieldValidator {

    @Override
    public String validate(String fieldName, Object value, Map<String, String> params) {
        if (value == null) {
            return params.getOrDefault("message", getDefaultMessage());
        }
        if (value instanceof String) {
            String str = (String) value;
            if (str.trim().isEmpty()) {
                return params.getOrDefault("message", getDefaultMessage());
            }
        }
        return null;
    }

    @Override
    public String getDefaultMessage() {
        return "不能为空白";
    }
}
