package io.github.vennarshulytz.validation.annotation.constraints;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.builtin.PatternValidator;

import java.lang.annotation.*;

/**
 * 正则表达式校验注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidateWith(validator = PatternValidator.class)
public @interface PatternCheck {

    String message() default MessageConstants.Pattern;

    /**
     * 正则表达式
     */
    String regexp();
}
