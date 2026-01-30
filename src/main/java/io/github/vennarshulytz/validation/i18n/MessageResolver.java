package io.github.vennarshulytz.validation.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 消息解析器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class MessageResolver {

    private final MessageSource messageSource;

    public MessageResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * 解析消息
     */
    public String resolve(String message) {

        // 尝试从MessageSource获取国际化消息
        try {
            return messageSource.getMessage(message, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return message;
        }
    }
}
