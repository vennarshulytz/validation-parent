package io.github.vennarshulytz.validation.annotation.constraints;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.builtin.SizeValidator;

import java.lang.annotation.*;

/**
 * 大小校验注解
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidateWith(validator = SizeValidator.class)
public @interface SizeCheck {

    String message() default MessageConstants.Size;

    /**
     * 最小值
     */
    int min() default 0;

    /**
     * 最大值
     */
    int max() default Integer.MAX_VALUE;
}
