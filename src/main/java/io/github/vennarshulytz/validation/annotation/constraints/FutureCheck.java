package io.github.vennarshulytz.validation.annotation.constraints;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.builtin.FutureValidator;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 必须是将来时间校验注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidateWith(validator = FutureValidator.class)
public @interface FutureCheck {

    @AliasFor("message")
    String value() default MessageConstants.Future;

    String message() default MessageConstants.Future;
}
