package io.github.vennarshulytz.validation.annotation.constraints;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.builtin.DecimalMinValidator;

import java.lang.annotation.*;

/**
 * 小数最小值校验注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidateWith(validator = DecimalMinValidator.class)
public @interface DecimalMinCheck {

    String message() default MessageConstants.DecimalMin;

    /**
     * 最小值
     */
    String value();

    /**
     * 是否包含边界值
     */
    boolean inclusive() default true;
}
