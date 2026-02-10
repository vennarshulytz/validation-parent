package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.annotation.constraints.PatternCheck;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 正则校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class PatternValidator implements FieldValidator {
    
    private static final ConcurrentHashMap<String, Pattern> PATTERN_CACHE = new ConcurrentHashMap<>();
    
    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, boolean enableI18n) {
        if (value == null) {
            return null;
        }
        
       // String regex = params.get("regexp");
       // if (regex == null || regex.isEmpty()) {
       //     return null;
       // }
        Object regexObject = params.get("regexp");
        if (regexObject == null) {
            return null;
        }

        String regex = regexObject.toString();
        if (regex.isEmpty()) {
            return null;
        }

        String strValue = value.toString();
        Pattern pattern = PATTERN_CACHE.computeIfAbsent(regex, Pattern::compile);
        
        if (!pattern.matcher(strValue).matches()) {
            return getErrorMessage(MessageConstants.Pattern, params, enableI18n);

        }
        
        return null;
    }

    @Override
    public Map<String, Object> parseParams(Annotation annotation) {

        if (annotation instanceof PatternCheck) {
            PatternCheck patternCheck = (PatternCheck) annotation;
            Map<String, Object> params = new HashMap<>();
            params.put("message", patternCheck.message());
            params.put("regexp", patternCheck.regexp());
            return params;
        }
        return Collections.emptyMap();
    }

    @Override
    public String getDefaultMessage() {
        // 格式不正确
        return "must match \"{regexp}\"";
    }
}