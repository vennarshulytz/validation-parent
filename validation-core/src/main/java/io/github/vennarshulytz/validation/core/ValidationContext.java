package io.github.vennarshulytz.validation.core;

import io.github.vennarshulytz.validation.annotation.ValidationRule;
import io.github.vennarshulytz.validation.validator.ValidationResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 校验上下文
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class ValidationContext {

    private final ValidationMode mode;
    private final boolean enableI18n;
    private final ValidationResult result;
    private ValidationRuleCache.CachedRuleInfo cachedRuleInfo;


    private final Map<Class<?>, Set<String>> typeExplicitPaths = new HashMap<>();

    public ValidationContext(ValidationMode mode, boolean enableI18n) {
        this.mode = mode;
        this.enableI18n = enableI18n;
        this.result = new ValidationResult(mode == ValidationMode.FAIL_FAST);
    }

    public ValidationMode getMode() {
        return mode;
    }

    public boolean isEnableI18n() {
        return enableI18n;
    }

    public ValidationResult getResult() {
        return result;
    }

    public List<ValidationRule> getRules() {
        return cachedRuleInfo.getRules();
    }

    /**
     * 获取指定类型需要排除的路径集合
     */
    public Set<String> getExcludedPaths(Class<?> type) {
        return cachedRuleInfo.getExcludedPaths(type);
    }

    public void setCachedRuleInfo(ValidationRuleCache.CachedRuleInfo cachedRuleInfo) {
        this.cachedRuleInfo = cachedRuleInfo;
    }
}
