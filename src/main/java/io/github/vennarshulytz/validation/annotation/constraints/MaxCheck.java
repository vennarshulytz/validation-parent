package io.github.vennarshulytz.validation.annotation.constraints;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.builtin.MaxValidator;

import java.lang.annotation.*;

/**
 * 最大值校验注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidateWith(validator = MaxValidator.class)
public @interface MaxCheck {

    String message() default MessageConstants.Max;

    /**
     * 最大值
     */
    long value();
}
