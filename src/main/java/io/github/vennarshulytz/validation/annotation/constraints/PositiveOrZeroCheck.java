package io.github.vennarshulytz.validation.annotation.constraints;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.builtin.PositiveOrZeroValidator;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 正数或零校验注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidateWith(validator = PositiveOrZeroValidator.class)
public @interface PositiveOrZeroCheck {

    @AliasFor("message")
    String value() default MessageConstants.PositiveOrZero;

    String message() default MessageConstants.PositiveOrZero;
}
