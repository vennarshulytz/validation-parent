package io.github.vennarshulytz.validation.annotation.constraints;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.builtin.PastOrPresentValidator;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 必须是当前或过去时间校验注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidateWith(validator = PastOrPresentValidator.class)
public @interface PastOrPresentCheck {

    @AliasFor("message")
    String value() default MessageConstants.PastOrPresent;

    @AliasFor("value")
    String message() default MessageConstants.PastOrPresent;
}
