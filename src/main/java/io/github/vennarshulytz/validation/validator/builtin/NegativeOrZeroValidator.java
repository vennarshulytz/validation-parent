package io.github.vennarshulytz.validation.validator.builtin;

import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.util.Map;

/**
 * 负数或零校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class NegativeOrZeroValidator extends AbstractSignValidator implements FieldValidator {

    @Override
    public String validate(String fieldName, Object value, Map<String, String> params) {
        if (value == null) {
            return null;
        }

        int signum = getSignum(value);
        if (signum == Integer.MIN_VALUE) {
            return null;
        }

        if (signum > 0) {
            return params.getOrDefault("message", getDefaultMessage());
        }

        return null;
    }

    @Override
    public String getDefaultMessage() {
        return "必须是负数或零";
    }
}
