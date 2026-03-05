package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.annotation.constraints.EmailCheck;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 邮箱校验器
 * 基于RFC 5321规范的简化实现
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class EmailValidator implements FieldValidator {

    private static final int MAX_LOCAL_PART_LENGTH = 64;
    private static final int MAX_DOMAIN_PART_LENGTH = 255;

    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, boolean enableI18n) {
        if (value == null) {
            return null;
        }

        if (!(value instanceof String)) {
            return getErrorMessage(MessageConstants.Email, params, enableI18n);
        }

        String email = (String) value;

        if (email.isEmpty()) {
            return null; // 空字符串由NotBlank/NotEmpty处理
        }

        if (!isValidEmail(email)) {
            return getErrorMessage(MessageConstants.Email, params, enableI18n);
        }

        return null;
    }

    /**
     * 高性能邮箱验证（不使用正则）
     */
    private boolean isValidEmail(String email) {
        int length = email.length();
        if (length < 3) { // 最小 a@b
            return false;
        }

        // 查找@符号位置
        int atIndex = -1;
        int atCount = 0;
        for (int i = 0; i < length; i++) {
            if (email.charAt(i) == '@') {
                atIndex = i;
                atCount++;
            }
        }

        // 必须有且只有一个@
        if (atCount != 1 || atIndex == 0 || atIndex == length - 1) {
            return false;
        }

        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex + 1);

        // 长度校验
        if (localPart.length() > MAX_LOCAL_PART_LENGTH || domainPart.length() > MAX_DOMAIN_PART_LENGTH) {
            return false;
        }

        return isValidLocalPart(localPart) && isValidDomainPart(domainPart);
    }

    /**
     * 验证本地部分
     */
    private boolean isValidLocalPart(String localPart) {
        if (localPart.isEmpty()) {
            return false;
        }

        // 不能以点开头或结尾
        if (localPart.charAt(0) == '.' || localPart.charAt(localPart.length() - 1) == '.') {
            return false;
        }

        boolean previousDot = false;
        for (int i = 0; i < localPart.length(); i++) {
            char c = localPart.charAt(i);

            if (c == '.') {
                // 不能有连续的点
                if (previousDot) {
                    return false;
                }
                previousDot = true;
            } else {
                previousDot = false;
                // 允许的字符：字母、数字、以及特殊字符
                if (!isValidLocalChar(c)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 验证本地部分的字符
     */
    private boolean isValidLocalChar(char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || (c >= '0' && c <= '9')
                || c == '!' || c == '#' || c == '$' || c == '%' || c == '&'
                || c == '\'' || c == '*' || c == '+' || c == '-' || c == '/'
                || c == '=' || c == '?' || c == '^' || c == '_' || c == '`'
                || c == '{' || c == '|' || c == '}' || c == '~';
    }

    /**
     * 验证域名部分
     */
    private boolean isValidDomainPart(String domainPart) {
        if (domainPart.isEmpty()) {
            return false;
        }

        // 不能以点或连字符开头或结尾
        char first = domainPart.charAt(0);
        char last = domainPart.charAt(domainPart.length() - 1);
        if (first == '.' || first == '-' || last == '.' || last == '-') {
            return false;
        }

        // 必须包含至少一个点（表示有域名层级）
        boolean hasDot = false;
        boolean previousDot = false;
        boolean previousHyphen = false;

        for (int i = 0; i < domainPart.length(); i++) {
            char c = domainPart.charAt(i);

            if (c == '.') {
                hasDot = true;
                if (previousDot || previousHyphen) {
                    return false; // 不能有连续的点，或点前面是连字符
                }
                previousDot = true;
                previousHyphen = false;
            } else if (c == '-') {
                if (previousDot) {
                    return false; // 连字符不能紧跟在点后面
                }
                previousHyphen = true;
                previousDot = false;
            } else {
                previousDot = false;
                previousHyphen = false;
                // 域名只允许字母、数字
                if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'))) {
                    return false;
                }
            }
        }

        return hasDot;
    }

    @Override
    public Map<String, Object> parseParams(Annotation annotation) {

        if (annotation instanceof EmailCheck) {
            EmailCheck emailCheck = (EmailCheck) annotation;
            Map<String, Object> params = new HashMap<>(2);
            params.put("message", emailCheck.message());
            return params;
        }
        return Collections.emptyMap();
    }


    @Override
    public String getDefaultMessage() {
        // 邮箱格式不正确
        return "must be a well-formed email address";
    }
}
