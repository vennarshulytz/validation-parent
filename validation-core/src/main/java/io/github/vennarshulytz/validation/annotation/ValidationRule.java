package io.github.vennarshulytz.validation.annotation;

import io.github.vennarshulytz.validation.validator.CustomValidator;

import java.lang.annotation.*;

/**
 * 校验规则定义
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ValidationRules.class)
@Documented
public @interface ValidationRule {
    /**
     * 目标类型
     */
    Class<?> type() default Object.class;

    /**
     * 字段路径，支持嵌套路径如 "manager.address"
     */
    String path() default "";

    /**
     * 校验器配置列表
     */
    ValidateWith[] validators() default {};

    /**
     * 自定义校验器
     */
    Class<? extends CustomValidator> custom() default CustomValidator.class;
}