package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.time.Instant;
import java.util.Map;

/**
 * 过去时间校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class PastValidator extends AbstractTemporalValidator implements FieldValidator {

    @Override
    public String validate(String fieldName, Object value, Map<String, String> params) {
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
            return params.getOrDefault("message", getDefaultMessage());
        }

        return null;
    }

    @Override
    public String getDefaultMessage() {
        return "必须是过去的时间";
    }
}
