package io.github.vennarshulytz.validation.annotation;

import io.github.vennarshulytz.validation.template.ValidationRuleTemplate;

import java.lang.annotation.*;

/**
 * 校验规则集注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidationRules {

    /**
     * 校验规则模板类
     */
    Class<? extends ValidationRuleTemplate> template() default ValidationRuleTemplate.class;

    /**
     * 校验规则列表
     */
    ValidationRule[] value() default {};
}