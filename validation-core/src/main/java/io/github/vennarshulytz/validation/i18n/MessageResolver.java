package io.github.vennarshulytz.validation.i18n;

import io.github.vennarshulytz.validation.utils.NamedPlaceholderResolver;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Map;

/**
 * 消息解析器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class MessageResolver {

    private final MessageSource messageSource;
    private final boolean enabled;

    public MessageResolver(MessageSource messageSource, boolean enabled) {
        this.messageSource = messageSource;
        this.enabled = enabled;
    }

    /**
     * 解析消息
     */
    public String resolve(String message, Map<String, Object> params) {

        if (message == null) {
            return message;
        }

        if (!enabled) {
            return NamedPlaceholderResolver.resolve(message, params);
        }

        // 尝试从MessageSource获取国际化消息
        try {
            String resolved = messageSource.getMessage(message, null, LocaleContextHolder.getLocale());
            return NamedPlaceholderResolver.resolve(resolved, params);
        } catch (Exception e) {
            // 如果找不到，返回原消息并替换占位符
            return NamedPlaceholderResolver.resolve(message, params);
        }
    }

}
