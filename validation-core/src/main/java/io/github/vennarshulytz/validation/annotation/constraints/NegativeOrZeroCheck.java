package io.github.vennarshulytz.validation.annotation.constraints;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.builtin.NegativeOrZeroValidator;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 负数或零校验注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidateWith(validator = NegativeOrZeroValidator.class)
public @interface NegativeOrZeroCheck {

    @AliasFor("message")
    String value() default MessageConstants.NegativeOrZero;

    @AliasFor("value")
    String message() default MessageConstants.NegativeOrZero;
}
