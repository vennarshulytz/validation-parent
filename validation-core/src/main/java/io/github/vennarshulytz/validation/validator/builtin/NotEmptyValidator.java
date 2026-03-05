package io.github.vennarshulytz.validation.validator.builtin;

import io.github.vennarshulytz.validation.annotation.constraints.NotEmptyCheck;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 非空集合校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class NotEmptyValidator implements FieldValidator {

    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, boolean enableI18n) {
        if (value == null) {
            return getErrorMessage(MessageConstants.NotEmpty, params, enableI18n);
        }

        if (value instanceof String) {
            String str = (String) value;
            if (str.isEmpty()) {
                return getErrorMessage(MessageConstants.NotEmpty, params, enableI18n);

            }
        }

        if (value instanceof Collection<?>) {
            Collection<?> col = (Collection<?>) value;
            if (col.isEmpty()) {
                return getErrorMessage(MessageConstants.NotEmpty, params, enableI18n);
            }
        }

        if (value instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) value;
            if (map.isEmpty()) {
                return getErrorMessage(MessageConstants.NotEmpty, params, enableI18n);

            }

        }

        if (value.getClass().isArray()) {
            if (java.lang.reflect.Array.getLength(value) == 0) {
                return getErrorMessage(MessageConstants.NotEmpty, params, enableI18n);
            }
        }

        return null;
    }

    @Override
    public Map<String, Object> parseParams(Annotation annotation) {

        if (annotation instanceof NotEmptyCheck) {
            NotEmptyCheck notEmptyCheck = (NotEmptyCheck) annotation;
            Map<String, Object> params = new HashMap<>(2);
            params.put("message", notEmptyCheck.message());
            return params;
        }
        return Collections.emptyMap();
    }

    @Override
    public String getDefaultMessage() {
        // 不能为空
        return "must not be empty";
    }
}
