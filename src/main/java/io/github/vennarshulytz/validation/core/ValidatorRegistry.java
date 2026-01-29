package io.github.vennarshulytz.validation.core;

import io.github.vennarshulytz.validation.validator.FieldValidator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

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
            } catch (NoSuchBeanDefinitionException e) {
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to instantiate validator: " + clazz.getName(), ex);
                }
            }
        });
    }

}
