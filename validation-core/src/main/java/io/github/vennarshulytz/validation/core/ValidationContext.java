package io.github.vennarshulytz.validation.core;

import io.github.vennarshulytz.validation.annotation.ValidationRule;
import io.github.vennarshulytz.validation.annotation.ValidationRules;
import io.github.vennarshulytz.validation.validator.ValidationResult;

import java.util.*;

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
    private List<ValidationRule> rules;

    // 存储每个类型对应的明确指定路径集合（用于排除）
    private final Map<Class<?>, Set<String>> typeExplicitPaths = new HashMap<>();

    public ValidationContext(ValidationMode mode, boolean enableI18n) {
        this.mode = mode;
        this.enableI18n = enableI18n;
        this.result = new ValidationResult(mode == ValidationMode.FAIL_FAST);
    }

    /**
     * 初始化规则
     */
    public void initRules(ValidationRules validationRules) {

        ValidationRule[] value = validationRules.value();
        int length = value.length;
        if (length == 0) {
            return;
        }
        rules = new ArrayList<>(length);
        // 第一遍：收集每个类型的所有明确指定路径
        for (ValidationRule rule : value) {
            Class<?> type = rule.type();
            String path = rule.path();

            if (path != null && !path.isEmpty()) {
                typeExplicitPaths.computeIfAbsent(type, k -> new HashSet<>()).add(path);
            }

            rules.add(rule);
        }
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
        return rules;
    }

    /**
     * 获取指定类型需要排除的路径集合
     */
    public Set<String> getExcludedPaths(Class<?> type) {
        return typeExplicitPaths.getOrDefault(type, Collections.emptySet());
    }
}
