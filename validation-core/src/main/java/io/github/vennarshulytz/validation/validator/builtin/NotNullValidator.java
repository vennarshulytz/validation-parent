package io.github.vennarshulytz.validation.validator.builtin;

import io.github.vennarshulytz.validation.annotation.constraints.NotNullCheck;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 非空校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class NotNullValidator implements FieldValidator  {

    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, boolean enableI18n) {
        if (value == null) {
            return getErrorMessage(MessageConstants.NotNull, params, enableI18n);

        }
        return null;
    }

    @Override
    public Map<String, Object> parseParams(Annotation annotation) {

        if (annotation instanceof NotNullCheck) {
            NotNullCheck notNullCheck = (NotNullCheck) annotation;
            Map<String, Object> params = new HashMap<>(2);
            params.put("message", notNullCheck.message());
            return params;
        }
        return Collections.emptyMap();
    }

    @Override
    public String getDefaultMessage() {
        // 不能为 null
        return "must not be null";
    }

}
