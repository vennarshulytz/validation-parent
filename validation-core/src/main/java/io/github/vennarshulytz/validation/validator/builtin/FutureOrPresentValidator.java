package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.annotation.constraints.FutureOrPresentCheck;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.lang.annotation.Annotation;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 当前或将来时间校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class FutureOrPresentValidator extends AbstractTemporalValidator implements FieldValidator {

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
        if (instant.isBefore(now)) {
            return getErrorMessage(MessageConstants.FutureOrPresent, params, enableI18n);
        }

        return null;
    }

    @Override
    public Map<String, Object> parseParams(Annotation annotation) {

        if (annotation instanceof FutureOrPresentCheck) {
            FutureOrPresentCheck futureOrPresentCheck = (FutureOrPresentCheck) annotation;
            Map<String, Object> params = new HashMap<>();
            params.put("message", futureOrPresentCheck.message());
            return params;
        }
        return Collections.emptyMap();
    }

    @Override
    public String getDefaultMessage() {
        // 必须是当前或将来的时间
        return "must be a date in the present or in the future";
    }
}
