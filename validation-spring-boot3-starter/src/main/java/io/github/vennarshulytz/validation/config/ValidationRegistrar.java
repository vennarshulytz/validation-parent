package io.github.vennarshulytz.validation.config;

import io.github.vennarshulytz.validation.annotation.EnableValidation;
import io.github.vennarshulytz.validation.autoconfigure.ValidationConfiguration;
import io.github.vennarshulytz.validation.core.ValidationMode;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 校验配置注册器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class ValidationRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(EnableValidation.class.getName()));

        if (attributes == null) {
            return;
        }

        ValidationMode mode = attributes.getEnum("mode");
        boolean enableI18n = attributes.getBoolean("enableI18n");
        long cacheMaximumSize = attributes.getNumber("cacheMaximumSize");

        // 注册 ValidationProperties
        BeanDefinitionBuilder propertiesBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(ValidationProperties.class);
        propertiesBuilder.addConstructorArgValue(mode);
        propertiesBuilder.addConstructorArgValue(enableI18n);
        propertiesBuilder.addConstructorArgValue(cacheMaximumSize);
        registry.registerBeanDefinition("validationProperties", propertiesBuilder.getBeanDefinition());

        // 注册 ValidationConfiguration
        BeanDefinitionBuilder configBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(ValidationConfiguration.class);
        registry.registerBeanDefinition("validationConfiguration", configBuilder.getBeanDefinition());
    }
}