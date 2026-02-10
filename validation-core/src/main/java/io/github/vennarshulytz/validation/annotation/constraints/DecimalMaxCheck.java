package io.github.vennarshulytz.validation.annotation.constraints;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.builtin.DecimalMaxValidator;

import java.lang.annotation.*;

/**
 * 小数最大值校验注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidateWith(validator = DecimalMaxValidator.class)
public @interface DecimalMaxCheck {

    String message() default MessageConstants.DecimalMax;

    /**
     * 最大值
     */
    String value();

    /**
     * 是否包含边界值
     */
    boolean inclusive() default true;
}
