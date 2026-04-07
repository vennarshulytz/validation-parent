package io.github.vennarshulytz.validation.annotation;

import io.github.vennarshulytz.validation.config.ValidationRegistrar;
import io.github.vennarshulytz.validation.core.ValidationMode;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用校验功能
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ValidationRegistrar.class})
public @interface EnableValidation {
    /**
     * 校验模式，默认快速失败
     */
    ValidationMode mode() default ValidationMode.FAIL_FAST;

    /**
     * 是否启用国际化
     */
    boolean enableI18n() default false;

    /**
     * 规则缓存最大容量
     */
    long cacheMaximumSize() default 1024;
}