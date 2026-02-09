package io.github.vennarshulytz.validation.resolver;

import io.github.vennarshulytz.validation.annotation.ValidateWith;
import io.github.vennarshulytz.validation.annotation.ValidationRules;
import io.github.vennarshulytz.validation.core.ValidationContext;
import io.github.vennarshulytz.validation.core.ValidationEngine;
import io.github.vennarshulytz.validation.core.ValidationMode;
import io.github.vennarshulytz.validation.exception.ValidationException;
import io.github.vennarshulytz.validation.utils.InputStreamUtils;
import io.github.vennarshulytz.validation.validator.ValidationResult;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 校验参数解析器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class ValidationArgumentResolver implements HandlerMethodArgumentResolver {

    private final ValidationEngine validationEngine;
    private final ValidationMode defaultMode;
    private final boolean enableI18n;
    private final List<HttpMessageConverter<?>> messageConverters;

    public ValidationArgumentResolver(ValidationEngine validationEngine,
                                      ValidationMode defaultMode,
                                      boolean enableI18n,
                                      List<HttpMessageConverter<?>> messageConverters) {
        this.validationEngine = validationEngine;
        this.defaultMode = defaultMode;
        this.enableI18n = enableI18n;
        this.messageConverters = messageConverters;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ValidationRules.class)
                || hasValidateWithAnnotation(parameter);
    }

    private boolean hasValidateWithAnnotation(MethodParameter parameter) {
        for (Annotation annotation : parameter.getParameterAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(ValidateWith.class)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);

        // 解析参数值
        Object argument = resolveArgumentValue(parameter, servletRequest, webRequest);

        // 创建校验上下文
        ValidationContext context = new ValidationContext(defaultMode, enableI18n);

        // 处理 @ValidationRules 注解
        ValidationRules validationRules = parameter.getParameterAnnotation(ValidationRules.class);
        if (validationRules != null && argument != null) {
            context.initRules(validationRules);
            validationEngine.validate(argument, context);
        }

        // 处理带有 @ValidateWith 的注解（如 @NotNullCheck）
        processValidateWithAnnotations(parameter, argument, context);

        // 检查校验结果
        ValidationResult result = context.getResult();
        if (result.hasErrors()) {
            throw new ValidationException(result);
        }

        return argument;
    }

    @SuppressWarnings("unchecked")
    private Object resolveArgumentValue(MethodParameter parameter, HttpServletRequest servletRequest,
                                        NativeWebRequest webRequest) throws Exception {
        Class<?> parameterType = parameter.getParameterType();

        // 处理简单类型参数
        if (isSimpleType(parameterType)) {
            String paramName = parameter.getParameterName();
            String value = webRequest.getParameter(paramName);
            return convertSimpleValue(value, parameterType);
        }

        // 处理 @RequestBody 参数
        if (parameter.hasParameterAnnotation(org.springframework.web.bind.annotation.RequestBody.class)) {
            // 包装请求以支持多次读取
            CachedBodyHttpServletRequest cachedRequest = wrapRequest(servletRequest);
            HttpInputMessage inputMessage = new ServletServerHttpRequest(cachedRequest);

            for (HttpMessageConverter<?> converter : messageConverters) {
                if (converter instanceof MappingJackson2HttpMessageConverter) {
                    if (converter.canRead(parameterType, inputMessage.getHeaders().getContentType())) {
                        return ((HttpMessageConverter<Object>) converter).read(parameterType, inputMessage);
                    }
                }
            }
        }

        return null;
    }

    private CachedBodyHttpServletRequest wrapRequest(HttpServletRequest request) throws IOException {
        if (request instanceof CachedBodyHttpServletRequest) {
            return (CachedBodyHttpServletRequest) request;
        }
        return new CachedBodyHttpServletRequest(request);
    }

    private void processValidateWithAnnotations(MethodParameter parameter, Object argument, ValidationContext context) {
        String paramName = parameter.getParameterName();
        if (paramName == null) {
            paramName = "arg" + parameter.getParameterIndex();
        }

        for (Annotation annotation : parameter.getParameterAnnotations()) {
            ValidateWith validateWith = annotation.annotationType().getAnnotation(ValidateWith.class);
            if (validateWith != null) {
//                String message = extractMessage(annotation);
                validationEngine.validateSimpleParam(argument, paramName, validateWith, annotation, context);

                if (context.getResult().shouldStop()) {
                    break;
                }
            }
        }
    }

    // private String extractMessage(Annotation annotation) {
    //     try {
    //         return (String) annotation.annotationType().getMethod("message").invoke(annotation);
    //     } catch (Exception e) {
    //         return "";
    //     }
    // }

    private boolean isSimpleType(Class<?> type) {
        return type.isPrimitive()
                || type == String.class
                || Number.class.isAssignableFrom(type)
                || type == Boolean.class
                || type == Character.class
                || type.isEnum();
    }

    private Object convertSimpleValue(String value, Class<?> targetType) {
        if (value == null) {
            if (targetType.isPrimitive()) {
                return getDefaultPrimitiveValue(targetType);
            }
            return null;
        }

        if (targetType == String.class) return value;
        if (targetType == Integer.class || targetType == int.class) return Integer.parseInt(value);
        if (targetType == Long.class || targetType == long.class) return Long.parseLong(value);
        if (targetType == Double.class || targetType == double.class) return Double.parseDouble(value);
        if (targetType == Float.class || targetType == float.class) return Float.parseFloat(value);
        if (targetType == Boolean.class || targetType == boolean.class) return Boolean.parseBoolean(value);
        if (targetType == Short.class || targetType == short.class) return Short.parseShort(value);
        if (targetType == Byte.class || targetType == byte.class) return Byte.parseByte(value);

        return value;
    }

    private Object getDefaultPrimitiveValue(Class<?> type) {
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == double.class) return 0.0d;
        if (type == float.class) return 0.0f;
        if (type == boolean.class) return false;
        if (type == short.class) return (short) 0;
        if (type == byte.class) return (byte) 0;
        if (type == char.class) return '\0';
        return null;
    }

    /**
     * 缓存请求体的HttpServletRequest包装类
     */
    private static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
        private final byte[] cachedBody;

        public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
            super(request);
            InputStream requestInputStream = request.getInputStream();
            this.cachedBody = InputStreamUtils.readAllBytes(requestInputStream);
        }

        @Override
        public ServletInputStream getInputStream() {
            return new CachedBodyServletInputStream(this.cachedBody);
        }

        @Override
        public BufferedReader getReader() {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
            return new BufferedReader(new InputStreamReader(byteArrayInputStream, StandardCharsets.UTF_8));
        }
    }

    private static class CachedBodyServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream inputStream;

        public CachedBodyServletInputStream(byte[] cachedBody) {
            this.inputStream = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() {
            return inputStream.read();
        }
    }
}
