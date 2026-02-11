package io.github.vennarshulytz.validation.annotation.constraints;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.builtin.NegativeValidator;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 负数校验注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidateWith(validator = NegativeValidator.class)
public @interface NegativeCheck {

    @AliasFor("message")
    String value() default MessageConstants.Negative;

    @AliasFor("value")
    String message() default MessageConstants.Negative;
}
