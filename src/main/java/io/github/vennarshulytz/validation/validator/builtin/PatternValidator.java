package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.validator.FieldValidator;

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
    public String validate(String fieldName, Object value, Map<String, String> params) {
        if (value == null) {
            return null;
        }
        
        String regex = params.get("regexp");
        if (regex == null || regex.isEmpty()) {
            return null;
        }
        
        String strValue = value.toString();
        Pattern pattern = PATTERN_CACHE.computeIfAbsent(regex, Pattern::compile);
        
        if (!pattern.matcher(strValue).matches()) {
            return params.getOrDefault("message", getDefaultMessage());
        }
        
        return null;
    }

    @Override
    public String getDefaultMessage() {
        return "格式不正确";
    }
}