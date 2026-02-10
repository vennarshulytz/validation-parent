package io.github.vennarshulytz.validation.utils;

import java.util.Map;

/**
 * 命名占位符解析器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public final class NamedPlaceholderResolver {

    private NamedPlaceholderResolver() {
    }

    public static String resolve(String template, Map<String, Object> params) {
        if (template == null || template.isEmpty() || params == null || params.isEmpty()) {
            return template;
        }

        int length = template.length();
        StringBuilder out = new StringBuilder(length + 16);

        for (int i = 0; i < length; i++) {
            char c = template.charAt(i);

            // 条件表达式 ${ ... }
            if (c == '$' && i + 1 < length && template.charAt(i + 1) == '{') {
                int end = findClosingBrace(template, i + 2);
                if (end == -1) {
                    out.append(c);
                    continue;
                }

                String expr = template.substring(i + 2, end);
                String resolved = resolveConditional(expr, params);

                if (resolved == null) {
                    out.append(template, i, end + 1);
                } else {
                    out.append(resolved);
                }
                i = end;
                continue;
            }

            // 普通占位符 {key}
            if (c == '{') {
                int end = findClosingBrace(template, i + 1);
                if (end == -1) {
                    out.append(c);
                    continue;
                }

                String key = template.substring(i + 1, end);
                Object value = params.get(key);

                if (value == null && !params.containsKey(key)) {
                    out.append('{').append(key).append('}');
                } else {
                    out.append(value);
                }
                i = end;
                continue;
            }

            out.append(c);
        }

        return out.toString();
    }

    private static int findClosingBrace(String text, int start) {
        int length = text.length();
        for (int i = start; i < length; i++) {
            if (text.charAt(i) == '}') {
                return i;
            }
        }
        return -1;
    }

    /**
     * 解析形如：
     * key == true ? 'xxx' : ''
     * key == false ? 'xxx' : ''
     */
    private static String resolveConditional(String expr, Map<String, Object> params) {
        int q = expr.indexOf('?');
        int colon = expr.lastIndexOf(':');

        if (q < 0 || colon < q) {
            return null;
        }

        String condition = expr.substring(0, q).trim();
        String truePart = expr.substring(q + 1, colon).trim();
        String falsePart = expr.substring(colon + 1).trim();

        boolean expectTrue;
        String key;

        if (condition.endsWith("== true")) {
            expectTrue = true;
            key = condition.substring(0, condition.length() - 7).trim();
        } else if (condition.endsWith("== false")) {
            expectTrue = false;
            key = condition.substring(0, condition.length() - 8).trim();
        } else {
            return null;
        }

        Object value = params.get(key);
        if (!(value instanceof Boolean)) {
            return null;
        }

        boolean matched = ((Boolean) value) == expectTrue;
        String result = matched ? truePart : falsePart;

        return stripQuotes(result);
    }

    private static String stripQuotes(String text) {
        int len = text.length();
        if (len >= 2 && text.charAt(0) == '\'' && text.charAt(len - 1) == '\'') {
            return text.substring(1, len - 1);
        }
        return text;
    }
}
