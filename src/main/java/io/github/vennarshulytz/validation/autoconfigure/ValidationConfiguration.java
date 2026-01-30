package io.github.vennarshulytz.validation.autoconfigure;

import io.github.vennarshulytz.validation.config.ValidationProperties;
import io.github.vennarshulytz.validation.core.ValidatorRegistry;
import io.github.vennarshulytz.validation.i18n.MessageResolver;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * 校验配置
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Configuration
public class ValidationConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ValidatorRegistry validatorRegistry(BeanFactory beanFactory) {
        return new ValidatorRegistry(beanFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageResolver messageResolver(ResourceBundleMessageSource messageSource, ValidationProperties validationProperties) {
        boolean enableI18n = validationProperties.isEnableI18n();
        if (enableI18n) {
            messageSource.addBasenames("io/github/vennarshulytz/validation");
            messageSource.setDefaultEncoding("UTF-8");
        }

        return new MessageResolver(messageSource, enableI18n);
    }
}
