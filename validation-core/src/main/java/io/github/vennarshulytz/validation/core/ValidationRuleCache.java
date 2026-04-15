package io.github.vennarshulytz.validation.core;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.vennarshulytz.validation.annotation.ValidationRule;
import io.github.vennarshulytz.validation.annotation.ValidationRules;
import io.github.vennarshulytz.validation.utils.ValidationRulesUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * 校验规则缓存管理器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class ValidationRuleCache {

    private final Cache<CacheKey, CachedRuleInfo> cache;

    public ValidationRuleCache(long maxSize) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .build();
    }

    /**
     * 获取缓存的规则信息
     */
    public CachedRuleInfo get(Method method, int parameterIndex, Parameter parameter) {
        CacheKey key = new CacheKey(method, parameterIndex);
        return cache.get(key, cacheKey -> {
            ValidationRules validationRules = ValidationEngine.findValidationRulesAnnotation(parameter);
            return parseRules(validationRules);
        });
    }

    /**
     * 解析并缓存规则（如果不存在）
     */
    public CachedRuleInfo parseRules(ValidationRules validationRules) {

        // ValidationRule[] value = validationRules.value();
        ValidationRule[] value = ValidationRulesUtils.resolve(validationRules);
        int length = value.length;
        if (length == 0) {
            return CachedRuleInfo.EMPTY;
        }
        List<ValidationRule> rules = new ArrayList<>(length);
        Map<Class<?>, Set<String>> typeExplicitPaths = new HashMap<>();
        // 第一遍：收集每个类型的所有明确指定路径
        for (ValidationRule rule : value) {
            Class<?> type = rule.type();
            String path = rule.path();

            if (path != null && !path.isEmpty()) {
                typeExplicitPaths.computeIfAbsent(type, k -> new HashSet<>()).add(path);
            }

            rules.add(rule);
        }
        return new CachedRuleInfo(rules, typeExplicitPaths);
    }

    /**
     * 缓存键
     */
    private static class CacheKey {
        private final Method method;
        private final int parameterIndex;

        CacheKey(Method method, int parameterIndex) {
            this.method = method;
            this.parameterIndex = parameterIndex;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return parameterIndex == cacheKey.parameterIndex && Objects.equals(method, cacheKey.method);
        }

        @Override
        public int hashCode() {
            return Objects.hash(method, parameterIndex);
        }
    }

    /**
     * 缓存的规则信息
     */
    public static class CachedRuleInfo {

        public static final CachedRuleInfo EMPTY = new CachedRuleInfo(Collections.emptyList(), Collections.emptyMap());

        private final List<ValidationRule> rules;

        // 存储每个类型对应的明确指定路径集合（用于排除）
        private final Map<Class<?>, Set<String>> typeExplicitPaths;

        public CachedRuleInfo(List<ValidationRule> rules, Map<Class<?>, Set<String>> typeExplicitPaths) {
            this.rules = Collections.unmodifiableList(rules);
            this.typeExplicitPaths = Collections.unmodifiableMap(typeExplicitPaths);
        }

        public List<ValidationRule> getRules() {
            return rules;
        }

        public Map<Class<?>, Set<String>> getTypeExplicitPaths() {
            return typeExplicitPaths;
        }

        public Set<String> getExcludedPaths(Class<?> type) {
            return typeExplicitPaths.getOrDefault(type, Collections.emptySet());
        }

        public boolean isEmpty() {
            return rules.isEmpty();
        }
    }
}