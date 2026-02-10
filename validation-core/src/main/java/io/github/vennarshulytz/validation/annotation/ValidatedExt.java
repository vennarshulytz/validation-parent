package io.github.vennarshulytz.validation.annotation;

import java.lang.annotation.*;

/**
 * 校验类注解 - 标记需要进行方法参数校验的类
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidatedExt {
}