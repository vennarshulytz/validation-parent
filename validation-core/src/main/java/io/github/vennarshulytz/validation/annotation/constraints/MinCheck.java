package io.github.vennarshulytz.validation.annotation.constraints;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.builtin.MinValidator;

import java.lang.annotation.*;

/**
 * 最小值校验注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidateWith(validator = MinValidator.class)
public @interface MinCheck {

    String message() default MessageConstants.Min;

    /**
     * 最小值
     */
    long value();
}
