package io.github.vennarshulytz.validation.autoconfigure;

import io.github.vennarshulytz.validation.core.ValidatorRegistry;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
