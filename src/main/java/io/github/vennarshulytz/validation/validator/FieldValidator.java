package io.github.vennarshulytz.validation.validator;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * 字段校验器接口
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public interface FieldValidator {

    /**
     * 校验字段值
     *
     * @param fieldName  字段名
     * @param value      字段值
     * @param params     校验参数
     * @param enableI18n
     * @return 校验失败返回错误信息，成功返回null
     */
    String validate(String fieldName, Object value, Map<String, Object> params, boolean enableI18n);

    default String getErrorMessage(String defaultMessage, Map<String, Object> params, boolean enableI18n) {
        String errorMessage = ((String) params.get("message"));
        if (enableI18n) {
            return errorMessage;
        }
        return defaultMessage.equals(errorMessage) ? getDefaultMessage() : errorMessage;
    }

    /**
     * 解析标记了 @ValidateWith 的注解（如 @NotNullCheck, @MaxCheck 等）
     * @param annotation
     * @return
     */
    Map<String, Object> parseParams(Annotation annotation);

    /**
     * 获取默认错误消息
     */
    default String getDefaultMessage() {
        return "校验失败";
    }
}
