package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.annotation.constraints.NegativeCheck;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 负数校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class NegativeValidator extends AbstractSignValidator implements FieldValidator {

    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, boolean enableI18n) {
        if (value == null) {
            return null;
        }

        int signum = getSignum(value);
        if (signum == Integer.MIN_VALUE) {
            return null;
        }

        if (signum >= 0) {
            return getErrorMessage(MessageConstants.Negative, params, enableI18n);
        }

        return null;
    }

    @Override
    public Map<String, Object> parseParams(Annotation annotation) {

        if (annotation instanceof NegativeCheck) {
            NegativeCheck negativeCheck = (NegativeCheck) annotation;
            Map<String, Object> params = new HashMap<>();
            params.put("message", negativeCheck.message());
            return params;
        }
        return Collections.emptyMap();
    }

    @Override
    public String getDefaultMessage() {
        // 必须是负数
        return "must be less than 0";
    }
}
