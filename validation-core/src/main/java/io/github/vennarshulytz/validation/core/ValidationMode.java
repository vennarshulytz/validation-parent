package io.github.vennarshulytz.validation.core;

/**
 * 校验模式
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public enum ValidationMode {
    /**
     * 快速失败：遇到第一个错误立即返回
     */
    FAIL_FAST,
    
    /**
     * 全部校验：收集所有错误后返回
     */
    FAIL_ALL
}