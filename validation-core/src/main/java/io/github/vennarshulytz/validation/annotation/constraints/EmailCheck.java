package io.github.vennarshulytz.validation.annotation.constraints;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.builtin.EmailValidator;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 邮箱格式校验注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidateWith(validator = EmailValidator.class)
public @interface EmailCheck {

    @AliasFor("message")
    String value() default MessageConstants.Email;

    @AliasFor("value")
    String message() default MessageConstants.Email;
}
