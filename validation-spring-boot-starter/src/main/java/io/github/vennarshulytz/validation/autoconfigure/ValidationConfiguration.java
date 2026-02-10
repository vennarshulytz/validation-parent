package io.github.vennarshulytz.validation.autoconfigure;

import io.github.vennarshulytz.validation.config.ValidationProperties;
import io.github.vennarshulytz.validation.core.ValidationEngine;
import io.github.vennarshulytz.validation.core.ValidationMode;
import io.github.vennarshulytz.validation.core.ValidatorRegistry;
import io.github.vennarshulytz.validation.i18n.MessageResolver;
import io.github.vennarshulytz.validation.resolver.ValidationArgumentResolver;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 校验配置
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@Configuration
public class ValidationConfiguration {

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private ValidationProperties validationProperties;

    @Autowired(required = false)
    private ResourceBundleMessageSource messageSource;

    @Autowired
    private List<HttpMessageConverter<?>> messageConverters;

    @Bean
    @ConditionalOnMissingBean
    public ValidatorRegistry validatorRegistry() {
        return new ValidatorRegistry(beanFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageResolver messageResolver() {
        boolean enableI18n = validationProperties.isEnableI18n();
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

    @Bean
    public ValidationArgumentResolver validationArgumentResolver() {
        ValidationMode mode = validationProperties != null ? validationProperties.getMode() : ValidationMode.FAIL_FAST;
        boolean enableI18n = validationProperties != null && validationProperties.isEnableI18n();

        // List<HttpMessageConverter<?>> messageConverters = requestMappingHandlerAdapter.getMessageConverters();

        return new ValidationArgumentResolver(
                validationEngine(validatorRegistry(), messageResolver()),
                mode,
                enableI18n,
                messageConverters
        );
    }

    @Bean
    public SmartInitializingSingleton argumentResolverInitializer(RequestMappingHandlerAdapter adapter) {
        return () -> {
            List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
            // 将自定义解析器添加到最前面
            resolvers.add(validationArgumentResolver());
            // 再添加原有的解析器
            List<HandlerMethodArgumentResolver> argumentResolvers = adapter.getArgumentResolvers();
            if (argumentResolvers != null) {
                resolvers.addAll(argumentResolvers);
            }
            adapter.setArgumentResolvers(resolvers);
        };
    }
}
