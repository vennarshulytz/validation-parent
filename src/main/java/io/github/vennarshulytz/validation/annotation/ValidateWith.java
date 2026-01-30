package io.github.vennarshulytz.validation.annotation;

import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.lang.annotation.*;

/**
 * 校验器配置
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidateWith {
    /**
     * 字段校验器类
     */
    Class<? extends FieldValidator> validator();

    /**
     * 字段配置列表
     */
    FieldConfig[] fields() default {};

    /**
     * 默认错误消息
     */
    String message() default "";

    /**
     * 默认校验参数
     */
    String[] params() default {};
}