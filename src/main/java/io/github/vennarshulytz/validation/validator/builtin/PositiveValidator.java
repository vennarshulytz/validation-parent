package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.util.Map;

/**
 * 正数校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class PositiveValidator extends AbstractSignValidator implements FieldValidator {

    @Override
    public String validate(String fieldName, Object value, Map<String, String> params) {
        if (value == null) {
            return null;
        }

        int signum = getSignum(value);
        if (signum == Integer.MIN_VALUE) {
            return null; // 无法判断的类型跳过
        }

        if (signum <= 0) {
            return params.getOrDefault("message", getDefaultMessage());
        }

        return null;
    }



    @Override
    public String getDefaultMessage() {
        return "必须是正数";
    }
}
