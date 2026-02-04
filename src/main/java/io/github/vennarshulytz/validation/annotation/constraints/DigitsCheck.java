package io.github.vennarshulytz.validation.annotation.constraints;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.builtin.DigitsValidator;

import java.lang.annotation.*;

/**
 * 数字位数校验注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidateWith(validator = DigitsValidator.class)
public @interface DigitsCheck {

    String message() default MessageConstants.Digits;

    /**
     * 整数部分最大位数
     */
    int integer();

    /**
     * 小数部分最大位数
     */
    int fraction();
}
