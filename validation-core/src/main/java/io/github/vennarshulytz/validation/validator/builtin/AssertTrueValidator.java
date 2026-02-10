package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.annotation.constraints.AssertTrueCheck;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 必须为true校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class AssertTrueValidator implements FieldValidator {

    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, boolean enableI18n) {
        if (value == null) {
            return null;
        }

        if (value instanceof Boolean) {
            if (!(Boolean) value) {
                return getErrorMessage(MessageConstants.AssertTrue, params, enableI18n);
            }
        }

        return null;
    }

    @Override
    public Map<String, Object> parseParams(Annotation annotation) {

        if (annotation instanceof AssertTrueCheck) {
            AssertTrueCheck assertTrueCheck = (AssertTrueCheck) annotation;
            Map<String, Object> params = new HashMap<>();
            params.put("message", assertTrueCheck.message());
            return params;
        }
        return Collections.emptyMap();
    }

    @Override
    public String getDefaultMessage() {
        // 必须为true
        return "must be true";
    }
}
