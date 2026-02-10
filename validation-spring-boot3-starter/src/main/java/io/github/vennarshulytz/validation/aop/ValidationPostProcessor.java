package io.github.vennarshulytz.validation.aop;

import io.github.vennarshulytz.validation.annotation.ValidatedExt;
import io.github.vennarshulytz.validation.core.ValidationEngine;
import io.github.vennarshulytz.validation.core.ValidationMode;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.InitializingBean;
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

    private ValidationEngine validationEngine;
    private ValidationMode defaultMode = ValidationMode.FAIL_FAST;
    private boolean enableI18n = false;

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
        return new ValidationMethodInterceptor(validationEngine, defaultMode, enableI18n);
    }

    // ========== Setter 方法，支持灵活配置 ==========

    /**
     * 设置自定义的校验注解类型
     */
    public void setValidatedAnnotationType(Class<? extends Annotation> validatedAnnotationType) {
        this.validatedAnnotationType = validatedAnnotationType;
    }

    public void setValidationEngine(ValidationEngine validationEngine) {
        this.validationEngine = validationEngine;
    }

    public void setDefaultMode(ValidationMode defaultMode) {
        this.defaultMode = defaultMode;
    }

    public void setEnableI18n(boolean enableI18n) {
        this.enableI18n = enableI18n;
    }
}