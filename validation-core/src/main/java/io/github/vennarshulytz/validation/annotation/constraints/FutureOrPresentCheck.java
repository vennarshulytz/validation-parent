package io.github.vennarshulytz.validation.annotation.constraints;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.builtin.FutureOrPresentValidator;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 必须是当前或将来时间校验注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidateWith(validator = FutureOrPresentValidator.class)
public @interface FutureOrPresentCheck {

    @AliasFor("message")
    String value() default MessageConstants.FutureOrPresent;

    @AliasFor("value")
    String message() default MessageConstants.FutureOrPresent;
}
