package io.github.vennarshulytz.validation.core;

import io.github.vennarshulytz.validation.annotation.FieldConfig;
import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.annotation.ValidationRule;
import io.github.vennarshulytz.validation.annotation.ValidationRules;
import io.github.vennarshulytz.validation.i18n.MessageResolver;
import io.github.vennarshulytz.validation.template.ValidationRuleTemplate;
import io.github.vennarshulytz.validation.validator.CustomValidator;
import io.github.vennarshulytz.validation.validator.FieldValidator;
import io.github.vennarshulytz.validation.validator.ValidationResult;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 校验引擎
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class ValidationEngine {

    private final ValidatorRegistry validatorRegistry;
    private final MessageResolver messageResolver;

    public ValidationEngine(ValidatorRegistry validatorRegistry, MessageResolver messageResolver) {
        this.validatorRegistry = validatorRegistry;
        this.messageResolver = messageResolver;
    }

    /**
     * 执行校验
     */
    public void validate(Object target, ValidationContext context) {
        ValidationResult result = context.getResult();

        for (ValidationRule rule : context.getRules()) {
            if (result.shouldStop()) {
                break;
            }
            processRule(target, rule, context);
        }
    }

    /**
     * 处理单个规则
     */
    private void processRule(Object target, ValidationRule rule, ValidationContext context) {
        ValidationResult result = context.getResult();

        // 处理自定义校验器
        if (rule.custom() != CustomValidator.class) {
            processCustomValidator(target, rule, context);
            if (result.shouldStop()) {
                return;
            }
        }

        // 处理字段校验器
        if (rule.validators().length > 0) {
            processFieldValidators(target, rule, context);
        }
    }

    /**
     * 处理自定义校验器
     */
    @SuppressWarnings("unchecked")
    private void processCustomValidator(Object target, ValidationRule rule, ValidationContext context) {
        Class<? extends CustomValidator<?>> customClass = (Class<? extends CustomValidator<?>>) rule.custom();
        CustomValidator<Object> customValidator = (CustomValidator<Object>) validatorRegistry.getCustomValidator(customClass);
        customValidator.validate(target, context.getResult());
    }

    /**
     * 处理字段校验器
     */
    private void processFieldValidators(Object target, ValidationRule rule, ValidationContext context) {
        ValidationResult result = context.getResult();
        Class<?> targetType = rule.type();
        String path = rule.path();

        // 获取需要排除的路径（仅当path为空时使用）
        Set<String> excludedPaths = (path == null || path.isEmpty())
                ? context.getExcludedPaths(targetType)
                : Collections.emptySet();

        // 查找匹配的实例

        for (FieldAccessor.TypedInstance typedInstance : FieldAccessor.findInstancesByTypeLazy(
                target, targetType, path, excludedPaths)) {
            if (result.shouldStop()) {
                break;
            }

            Object instance = typedInstance.getInstance();
            String instancePath = typedInstance.getPath();

            for (ValidateWith validateWith : rule.validators()) {
                if (result.shouldStop()) {
                    break;
                }

                processValidateWith(instance, instancePath, validateWith, context);
            }
        }
    }

    /**
     * 处理单个ValidateWith配置
     */
    private void processValidateWith(Object instance, String instancePath, ValidateWith validateWith, ValidationContext context) {
        ValidationResult result = context.getResult();
        FieldValidator validator = validatorRegistry.getFieldValidator(validateWith.validator());

        FieldConfig[] fieldConfigs = validateWith.fields();
        String defaultMessage = validateWith.message();
        Map<String, Object> defaultParams = parseParams(validateWith.params());

        if (fieldConfigs.length == 0) {
            // 没有字段配置，校验整个对象
            validateField(instancePath, "", instance, validator, defaultMessage, defaultParams, context);
        } else {
            // 按字段配置校验
            for (FieldConfig fieldConfig : fieldConfigs) {
                if (result.shouldStop()) {
                    break;
                }

                String fieldMessage = fieldConfig.message();
                String message = fieldMessage.isEmpty() ? defaultMessage : fieldMessage;
                Map<String, Object> params = fieldConfig.params().length > 0 ? parseParams(fieldConfig.params()) : new HashMap<>(defaultParams);

                for (String fieldName : fieldConfig.names()) {
                    if (result.shouldStop()) {
                        break;
                    }

                    Object fieldValue = FieldAccessor.getFieldValue(instance, fieldName);
                    validateField(instancePath, fieldName, fieldValue, validator, message, params, context);
                }
            }
        }
    }

    /**
     * 校验字段
     */
    private void validateField(String instancePath, String fieldName, Object fieldValue,
                               FieldValidator validator, String message, Map<String, Object> params,
                               ValidationContext context) {
        ValidationResult result = context.getResult();

        // 合并参数
        Map<String, Object> mergedParams = params;
        if (message != null && !message.isEmpty()) {
            mergedParams = new HashMap<>(params);
            mergedParams.put("message", message);
        }

        // 执行校验
        boolean enableI18n = context.isEnableI18n();
        String errorMessage = validator.validate(fieldName, fieldValue, mergedParams, enableI18n);

        if (errorMessage != null) {
            // 处理国际化
            errorMessage = messageResolver.resolve(errorMessage, mergedParams);

            String fullPath = buildFullPath(instancePath, fieldName);
            result.addError(fullPath, errorMessage, fieldValue);
        }
    }

    /**
     * 构建完整路径
     */
    private String buildFullPath(String instancePath, String fieldName) {
        if (instancePath == null || instancePath.isEmpty()) {
            return fieldName.isEmpty() ? "root" : fieldName;
        }
        if (fieldName == null || fieldName.isEmpty()) {
            return instancePath;
        }
        return instancePath + "." + fieldName;
    }

    /**
     * 解析参数
     */
    private Map<String, Object> parseParams(String[] params) {
        Map<String, Object> result = new HashMap<>();
        for (String param : params) {
            int idx = param.indexOf('=');
            if (idx > 0) {
                result.put(param.substring(0, idx).trim(), param.substring(idx + 1).trim());
            }
        }
        return result;
    }


    /**
     * 校验简单参数（如 @NotNullCheck, @MaxCheck 等）
     */
    public void validateSimpleParam(Object value, String paramName, ValidateWith validateWith,
                                    java.lang.annotation.Annotation annotation, ValidationContext context) {
        FieldValidator validator = validatorRegistry.getFieldValidator(validateWith.validator());
        // Map<String, Object> params = parseParams(validateWith.params());
        //
        // 从注解中提取参数
        Map<String, Object> params = validator.parseParams(annotation);


        boolean enableI18n = context.isEnableI18n();
        String errorMessage = validator.validate(paramName, value, params, enableI18n);
        if (errorMessage != null) {
            errorMessage = messageResolver.resolve(errorMessage, params);
            context.getResult().addError(paramName, errorMessage, value);
        }
    }


    /**
     * 获取 @ValidationRules 注解
     */
    public static ValidationRules findValidationRulesAnnotation(AnnotatedElement parameter) {

        ValidationRules validationRules = parameter.getAnnotation(ValidationRules.class);
        if (validationRules != null) {
            return validationRules;
        }

        ValidationRule validationRule = parameter.getAnnotation(ValidationRule.class);
        if (validationRule != null) {
            return createValidationRules(validationRule);
        }

        for (Annotation annotation : parameter.getAnnotations()) {
            validationRules = annotation.annotationType().getAnnotation(ValidationRules.class);
            if (validationRules != null) {
                return validationRules;
            }
            validationRule = annotation.annotationType().getAnnotation(ValidationRule.class);
            if (validationRule != null) {
                return createValidationRules(validationRule);
            }
        }

        return null;
    }

    public static @NonNull ValidationRules createValidationRules(ValidationRule validationRule) {
        return new ValidationRules() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return ValidationRules.class;
            }

            @Override
            public Class<? extends ValidationRuleTemplate> template() {
                return ValidationRuleTemplate.class;
            }

            @Override
            public ValidationRule[] value() {
                return new ValidationRule[]{validationRule};
            }
        };
    }
}
