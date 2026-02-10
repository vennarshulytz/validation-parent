package io.github.vennarshulytz.validation.core;

import io.github.vennarshulytz.validation.validator.CustomValidator;
import io.github.vennarshulytz.validation.validator.FieldValidator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 校验器注册中心
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class ValidatorRegistry {

    private final BeanFactory beanFactory;
    private final Map<Class<? extends FieldValidator>, FieldValidator> fieldValidatorCache = new ConcurrentHashMap<>();
    private final Map<Class<? extends CustomValidator<?>>, CustomValidator<?>> customValidatorCache = new ConcurrentHashMap<>();


    public ValidatorRegistry(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * 获取字段校验器（懒加载）
     */
    @SuppressWarnings("unchecked")
    public <T extends FieldValidator> T getFieldValidator(Class<T> validatorClass) {
        return (T) fieldValidatorCache.computeIfAbsent(validatorClass, clazz -> {
            // 优先从Spring容器获取
            try {
                return beanFactory.getBean(clazz);
            } catch (BeansException e) {
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to instantiate validator: " + clazz.getName(), ex);
                }
            }
        });
    }


    /**
     * 获取自定义校验器
     */
    @SuppressWarnings("unchecked")
    public <T> CustomValidator<T> getCustomValidator(Class<? extends CustomValidator<?>> validatorClass) {
        return (CustomValidator<T>) customValidatorCache.computeIfAbsent(validatorClass, clazz -> {
            try {
                return beanFactory.getBean(clazz);
            } catch (BeansException e) {
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to instantiate custom validator: " + clazz.getName(), ex);
                }
            }
        });
    }

}
