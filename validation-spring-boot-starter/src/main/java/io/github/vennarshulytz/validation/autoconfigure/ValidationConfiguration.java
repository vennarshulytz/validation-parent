package io.github.vennarshulytz.validation.autoconfigure;

import io.github.vennarshulytz.validation.aop.ValidationPostProcessor;
import io.github.vennarshulytz.validation.config.ValidationProperties;
import io.github.vennarshulytz.validation.core.ValidationEngine;
import io.github.vennarshulytz.validation.core.ValidatorRegistry;
import io.github.vennarshulytz.validation.i18n.MessageResolver;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;

/**
 * 校验配置
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class ValidationConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ValidatorRegistry validatorRegistry(BeanFactory beanFactory) {
        return new ValidatorRegistry(beanFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageResolver messageResolver(@Nullable ValidationProperties validationProperties,
                                           @Nullable ResourceBundleMessageSource messageSource) {
        boolean enableI18n = validationProperties != null && validationProperties.isEnableI18n();
        if (enableI18n) {
            if (messageSource == null) {
                messageSource = new ResourceBundleMessageSource();
            }

            messageSource.addBasenames("io/github/vennarshulytz/validation");
            messageSource.setDefaultEncoding("UTF-8");
        }

        return new MessageResolver(messageSource, enableI18n);
    }

    @Bean
    @ConditionalOnMissingBean
    public ValidationEngine validationEngine(ValidatorRegistry validatorRegistry, MessageResolver messageResolver) {
        return new ValidationEngine(validatorRegistry, messageResolver);
    }

    /**
     * 注册校验后置处理器
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean
    public static ValidationPostProcessor validationPostProcessor(
            ObjectProvider<ValidationEngine> validationEngineProvider,
            ObjectProvider<ValidationProperties> validationPropertiesProvider) {

        ValidationPostProcessor processor = new ValidationPostProcessor();
        processor.setValidationEngineProvider(validationEngineProvider);
        processor.setValidationPropertiesProvider(validationPropertiesProvider);
        return processor;
    }
}