package io.github.vennarshulytz.validation.aop;

import io.github.vennarshulytz.validation.annotation.ValidatedExt;
import io.github.vennarshulytz.validation.config.ValidationProperties;
import io.github.vennarshulytz.validation.core.ValidationEngine;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.Ordered;

import java.lang.annotation.Annotation;

/**
 * 校验后置处理器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class ValidationPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor
        implements InitializingBean {

    /**
     * 触发校验的注解类型，默认为 @ValidatedExt
     */
    private Class<? extends Annotation> validatedAnnotationType = ValidatedExt.class;

    private ObjectProvider<ValidationEngine> validationEngineProvider;
    private ObjectProvider<ValidationProperties> validationPropertiesProvider;

    @Override
    public void afterPropertiesSet() {
        // 创建切点：匹配类上标注了 @ValidatedExt 的所有方法
        Pointcut pointcut = new AnnotationMatchingPointcut(this.validatedAnnotationType, true);

        // 创建 Advisor
        DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor(pointcut, createValidationAdvice());
        defaultPointcutAdvisor.setOrder(Ordered.HIGHEST_PRECEDENCE);
        this.advisor = defaultPointcutAdvisor;
    }

    /**
     * 创建校验 Advice
     */
    protected Advice createValidationAdvice() {
        return new ValidationMethodInterceptor(validationEngineProvider, validationPropertiesProvider);
    }

    // ========== Setter 方法，支持灵活配置 ==========

    /**
     * 设置自定义的校验注解类型
     */
    public void setValidatedAnnotationType(Class<? extends Annotation> validatedAnnotationType) {
        this.validatedAnnotationType = validatedAnnotationType;
    }

    public void setValidationEngineProvider(ObjectProvider<ValidationEngine> validationEngineProvider) {
        this.validationEngineProvider = validationEngineProvider;
    }

    public void setValidationPropertiesProvider(ObjectProvider<ValidationProperties> validationPropertiesProvider) {
        this.validationPropertiesProvider = validationPropertiesProvider;
    }

}