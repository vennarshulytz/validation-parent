package io.github.vennarshulytz.validation.validator.builtin;

import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.util.Map;

/**
 * 非空校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class NotNullValidator implements FieldValidator  {

    @Override
    public String validate(String fieldName, Object value, Map<String, String> params) {
        if (value == null) {
            return params.getOrDefault("message", getDefaultMessage());
        }
        return null;
    }

    @Override
    public String getDefaultMessage() {
        return "不能为 null";
    }

}
