package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.annotation.constraints.AssertFalseCheck;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 必须为false校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class AssertFalseValidator implements FieldValidator {


    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, boolean enableI18n) {
        if (value == null) {
            return null;
        }

        if (value instanceof Boolean) {
            if ((Boolean) value) {
                return getErrorMessage(MessageConstants.AssertFalse, params, enableI18n);
            }
        }

        return null;
    }



    @Override
    public Map<String, Object> parseParams(Annotation annotation) {

        if (annotation instanceof AssertFalseCheck) {
            AssertFalseCheck assertFalseCheck = (AssertFalseCheck) annotation;
            Map<String, Object> params = new HashMap<>(2);
            params.put("message", assertFalseCheck.message());
            return params;
        }
        return Collections.emptyMap();
    }

    @Override
    public String getDefaultMessage() {
        // 必须为false
        return "must be false";
    }
}
