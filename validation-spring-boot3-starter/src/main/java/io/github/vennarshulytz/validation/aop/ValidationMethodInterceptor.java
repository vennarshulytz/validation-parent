package io.github.vennarshulytz.validation.aop;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.annotation.ValidationRules;
import io.github.vennarshulytz.validation.config.ValidationProperties;
import io.github.vennarshulytz.validation.core.ValidationContext;
import io.github.vennarshulytz.validation.core.ValidationEngine;
import io.github.vennarshulytz.validation.core.ValidationMode;
import io.github.vennarshulytz.validation.exception.ValidationException;
import io.github.vennarshulytz.validation.validator.ValidationResult;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 校验方法拦截器 - 核心 AOP 逻辑
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class ValidationMethodInterceptor implements MethodInterceptor {

    private final ObjectProvider<ValidationEngine> validationEngineProvider;
    private final ObjectProvider<ValidationProperties> validationPropertiesProvider;

    private volatile ValidationEngine validationEngine;
    private volatile ValidationMode defaultMode;
    private volatile Boolean enableI18n;

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();


    public ValidationMethodInterceptor(
            ObjectProvider<ValidationEngine> validationEngineProvider,
            ObjectProvider<ValidationProperties> validationPropertiesProvider) {
        this.validationEngineProvider = validationEngineProvider;
        this.validationPropertiesProvider = validationPropertiesProvider;
    }

    private ValidationEngine getValidationEngine() {
        ValidationEngine engine = this.validationEngine;
        if (engine == null) {
            synchronized (this) {
                engine = this.validationEngine;
                if (engine == null) {
                    engine = validationEngineProvider.getObject();
                    this.validationEngine = engine;
                }
            }
        }
        return engine;
    }

    private ValidationMode getDefaultMode() {
        ValidationMode mode = this.defaultMode;
        if (mode == null) {
            synchronized (this) {
                mode = this.defaultMode;
                if (mode == null) {
                    ValidationProperties props = validationPropertiesProvider.getIfAvailable();
                    mode = props != null ? props.getMode() : ValidationMode.FAIL_FAST;
                    this.defaultMode = mode;
                }
            }
        }
        return mode;
    }

    private boolean isEnableI18n() {
        Boolean i18n = this.enableI18n;
        if (i18n == null) {
            synchronized (this) {
                i18n = this.enableI18n;
                if (i18n == null) {
                    ValidationProperties props = validationPropertiesProvider.getIfAvailable();
                    i18n = props != null && props.isEnableI18n();
                    this.enableI18n = i18n;
                }
            }
        }
        return i18n;
    }


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Object[] arguments = invocation.getArguments();
        Parameter[] parameters = method.getParameters();
        
        // 获取参数名
        String[] paramNames = parameterNameDiscoverer.getParameterNames(method);
        
        // 创建校验上下文
        ValidationContext context = new ValidationContext(getDefaultMode(), isEnableI18n());
        ValidationEngine engine = getValidationEngine();

        int validationResultIndex = -1;

        // 遍历所有参数进行校验
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (validationResultIndex == -1 && ValidationResult.class.isAssignableFrom(parameter.getType())) {
                validationResultIndex = i;
                continue;
            }
            Object argument = arguments[i];
            String paramName = (paramNames != null && paramNames.length > i) 
                    ? paramNames[i] 
                    : "arg" + i;
            
            // 处理 @ValidationRules 注解
            ValidationRules validationRules = parameter.getAnnotation(ValidationRules.class);
            if (validationRules != null && argument != null) {
                context.initRules(validationRules);
                engine.validate(argument, context);
            }

            // 快速失败模式下，有错误立即停止
            if (context.getResult().shouldStop()) {
                break;
            }
            
            // 处理带有 @ValidateWith 的注解
            processValidateWithAnnotations(parameter, argument, paramName, context, engine);
            
            // 快速失败模式下，有错误立即停止
            if (context.getResult().shouldStop()) {
                break;
            }
        }
        
        // 检查校验结果
        ValidationResult result = context.getResult();
        if (validationResultIndex >= 0) {
            // 方法声明了 ValidationResult 参数：注入结果，由业务方自行处理
            arguments[validationResultIndex] = result;
        } else {
            // 未声明 ValidationResult 参数：校验失败则抛出异常
            if (result.hasErrors()) {
                throw new ValidationException(result);
            }
        }
        
        // 校验通过，执行原方法
        return invocation.proceed();
    }

    private void processValidateWithAnnotations(Parameter parameter, Object argument,
                                                String paramName, ValidationContext context, ValidationEngine engine) {
        for (Annotation annotation : parameter.getAnnotations()) {
            ValidateWith validateWith = annotation.annotationType().getAnnotation(ValidateWith.class);
            if (validateWith != null) {
                engine.validateSimpleParam(argument, paramName, validateWith, annotation, context);
                
                if (context.getResult().shouldStop()) {
                    break;
                }
            }
        }
    }
}