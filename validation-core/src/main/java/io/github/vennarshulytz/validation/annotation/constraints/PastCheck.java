package io.github.vennarshulytz.validation.annotation.constraints;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.builtin.PastValidator;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 必须是过去时间校验注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidateWith(validator = PastValidator.class)
public @interface PastCheck {

    @AliasFor("message")
    String value() default MessageConstants.Past;

    @AliasFor("value")
    String message() default MessageConstants.Past;
}
