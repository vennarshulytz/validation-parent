package io.github.vennarshulytz.validation.validator;

/**
 * 自定义校验器接口
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public interface CustomValidator<T> {

    /**
     * 执行校验
     *
     * @param target 校验目标对象
     * @param result 校验结果收集器
     */
    void validate(T target, ValidationResult result);
}