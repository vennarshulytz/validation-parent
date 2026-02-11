package io.github.vennarshulytz.validation.annotation.constraints;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.builtin.NotEmptyValidator;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 非空校验注解（字符串、集合、Map、数组）
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidateWith(validator = NotEmptyValidator.class)
public @interface NotEmptyCheck {

    @AliasFor("message")
    String value() default MessageConstants.NotEmpty;

    @AliasFor("value")
    String message() default MessageConstants.NotEmpty;
}
