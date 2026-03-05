package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.annotation.constraints.PastCheck;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.lang.annotation.Annotation;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 过去时间校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class PastValidator extends AbstractTemporalValidator implements FieldValidator {

    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, boolean enableI18n) {
        if (value == null) {
            return null;
        }

        if (!isSupportedType(value)) {
            return null;
        }

        Instant instant = toInstant(value);
        if (instant == null) {
            return null;
        }

        Instant now = Instant.now();
        if (!instant.isBefore(now)) {
            return getErrorMessage(MessageConstants.Past, params, enableI18n);
        }

        return null;
    }

    @Override
    public Map<String, Object> parseParams(Annotation annotation) {

        if (annotation instanceof PastCheck) {
            PastCheck pastCheck = (PastCheck) annotation;
            Map<String, Object> params = new HashMap<>(2);
            params.put("message", pastCheck.message());
            return params;
        }
        return Collections.emptyMap();
    }

    @Override
    public String getDefaultMessage() {
        // 必须是过去的时间
        return "must be a past date";

    }
}
