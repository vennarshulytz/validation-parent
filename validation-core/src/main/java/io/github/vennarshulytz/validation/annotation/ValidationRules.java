package io.github.vennarshulytz.validation.annotation;

import java.lang.annotation.*;

/**
 * 校验规则集注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidationRules {
    /**
     * 校验规则列表
     */
    ValidationRule[] value() default {};
}