package io.github.vennarshulytz.validation.annotation.constraints;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.builtin.NotNullValidator;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 非空校验注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidateWith(validator = NotNullValidator.class)
public @interface NotNullCheck {

    @AliasFor("message")
    String value() default MessageConstants.NotNull;

    @AliasFor("value")
    String message() default MessageConstants.NotNull;
}