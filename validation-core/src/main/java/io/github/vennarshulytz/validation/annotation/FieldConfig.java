package io.github.vennarshulytz.validation.annotation;

import java.lang.annotation.*;

/**
 * 字段配置
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldConfig {
    /**
     * 字段名列表
     */
    String[] names();

    /**
     * 错误消息
     */
    String message() default "";

    /**
     * 校验参数
     */
    String[] params() default {};
}