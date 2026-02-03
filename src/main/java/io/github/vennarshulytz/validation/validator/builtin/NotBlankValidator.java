package io.github.vennarshulytz.validation.validator.builtin;

import io.github.vennarshulytz.validation.annotation.constraints.NotBlankCheck;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 非空白字符串校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class NotBlankValidator implements FieldValidator {

    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, boolean enableI18n) {
        if (value == null) {
            return getErrorMessage(MessageConstants.NotBlank, params, enableI18n);

        }
        if (value instanceof String) {
            String str = (String) value;
            if (str.trim().isEmpty()) {
                return getErrorMessage(MessageConstants.NotBlank, params, enableI18n);
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> parseParams(Annotation annotation) {

        if (annotation instanceof NotBlankCheck) {
            NotBlankCheck notBlankCheck = (NotBlankCheck) annotation;
            Map<String, Object> params = new HashMap<>();
            params.put("message", notBlankCheck.message());
            return params;
        }
        return Collections.emptyMap();
    }

    @Override
    public String getDefaultMessage() {
        // 不能为空白
        return "must not be blank";
    }
}
