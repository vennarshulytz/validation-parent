package io.github.vennarshulytz.validation.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 消息解析器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class MessageResolver {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)}");

    private final MessageSource messageSource;
    private final boolean enabled;

    public MessageResolver(MessageSource messageSource, boolean enabled) {
        this.messageSource = messageSource;
        this.enabled = enabled;
    }

    /**
     * 解析消息
     */
    public String resolve(String message, Map<String, String> params) {
        if (!enabled || message == null) {
            return replacePlaceholders(message, params);
        }

        // 尝试从MessageSource获取国际化消息
        try {
            String resolved = messageSource.getMessage(message, null, LocaleContextHolder.getLocale());
            return replacePlaceholders(resolved, params);
        } catch (Exception e) {
            // 如果找不到，返回原消息并替换占位符
            return replacePlaceholders(message, params);
        }
    }

    /**
     * 替换占位符
     */
    private String replacePlaceholders(String message, Map<String, String> params) {
        if (message == null || params == null || params.isEmpty()) {
            return message;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(message);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = params.getOrDefault(key, matcher.group(0));
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }
}
