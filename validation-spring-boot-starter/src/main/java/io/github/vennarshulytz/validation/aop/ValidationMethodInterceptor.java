package io.github.vennarshulytz.validation.aop;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.annotation.ValidationRules;
import io.github.vennarshulytz.validation.core.ValidationContext;
import io.github.vennarshulytz.validation.core.ValidationEngine;
import io.github.vennarshulytz.validation.core.ValidationMode;
import io.github.vennarshulytz.validation.exception.ValidationException;
import io.github.vennarshulytz.validation.validator.ValidationResult;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 校验方法拦截器 - 核心 AOP 逻辑
 */
public class ValidationMethodInterceptor implements MethodInterceptor {

    private final ValidationEngine validationEngine;
    private final ValidationMode defaultMode;
    private final boolean enableI18n;
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public ValidationMethodInterceptor(ValidationEngine validationEngine,
                                       ValidationMode defaultMode,
                                       boolean enableI18n) {
        this.validationEngine = validationEngine;
        this.defaultMode = defaultMode;
        this.enableI18n = enableI18n;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Object[] arguments = invocation.getArguments();
        Parameter[] parameters = method.getParameters();
        
        // 获取参数名
        String[] paramNames = parameterNameDiscoverer.getParameterNames(method);
        
        // 创建校验上下文
        ValidationContext context = new ValidationContext(defaultMode, enableI18n);
        
        // 遍历所有参数进行校验
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object argument = arguments[i];
            String paramName = (paramNames != null && paramNames.length > i) 
                    ? paramNames[i] 
                    : "arg" + i;
            
            // 处理 @ValidationRules 注解
            ValidationRules validationRules = parameter.getAnnotation(ValidationRules.class);
            if (validationRules != null && argument != null) {
                context.initRules(validationRules);
                validationEngine.validate(argument, context);
            }

            // // 快速失败模式下，有错误立即停止
            // if (context.getResult().shouldStop()) {
            //     break;
            // }
            
            // 处理带有 @ValidateWith 的注解
            processValidateWithAnnotations(parameter, argument, paramName, context);
            
            // 快速失败模式下，有错误立即停止
            if (context.getResult().shouldStop()) {
                break;
            }
        }
        
        // 检查校验结果
        ValidationResult result = context.getResult();
        if (result.hasErrors()) {
            throw new ValidationException(result);
        }
        
        // 校验通过，执行原方法
        return invocation.proceed();
    }

    private void processValidateWithAnnotations(Parameter parameter, Object argument,
                                                String paramName, ValidationContext context) {
        for (Annotation annotation : parameter.getAnnotations()) {
            ValidateWith validateWith = annotation.annotationType().getAnnotation(ValidateWith.class);
            if (validateWith != null) {
                validationEngine.validateSimpleParam(argument, paramName, validateWith, annotation, context);
                
                if (context.getResult().shouldStop()) {
                    break;
                }
            }
        }
    }
}