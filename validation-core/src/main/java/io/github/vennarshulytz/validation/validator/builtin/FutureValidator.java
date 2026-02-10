package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.annotation.constraints.FutureCheck;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.lang.annotation.Annotation;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 将来时间校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class FutureValidator extends AbstractTemporalValidator implements FieldValidator {

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
        if (!instant.isAfter(now)) {
            return getErrorMessage(MessageConstants.Future, params, enableI18n);
        }

        return null;
    }

    @Override
    public Map<String, Object> parseParams(Annotation annotation) {

        if (annotation instanceof FutureCheck) {
            FutureCheck futureCheck = (FutureCheck) annotation;
            Map<String, Object> params = new HashMap<>();
            params.put("message", futureCheck.message());
            return params;
        }
        return Collections.emptyMap();
    }

    @Override
    public String getDefaultMessage() {
        // 必须是将来的时间
        return "must be a future date";
    }
}
