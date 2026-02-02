package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.time.Instant;
import java.util.Map;

/**
 * 当前或过去时间校验器（高性能）
 */
/**
 * 字段校验器接口
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class PastOrPresentValidator extends AbstractTemporalValidator implements FieldValidator {

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
        if (instant.isAfter(now)) {
            return params.getOrDefault("message", getDefaultMessage());
        }

        return null;
    }

    @Override
    public String getDefaultMessage() {
        return "必须是当前或过去的时间";
    }
}
