package io.github.vennarshulytz.validation.annotation.constraints;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.builtin.NotBlankValidator;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 非空白字符串校验注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidateWith(validator = NotBlankValidator.class)
public @interface NotBlankCheck {

    @AliasFor("message")
    String value() default MessageConstants.NotBlank;

    @AliasFor("value")
    String message() default MessageConstants.NotBlank;
}