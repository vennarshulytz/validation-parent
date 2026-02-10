package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * EmailValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("EmailValidator 测试")
class EmailValidatorTest extends ValidatorTestBase {

    private EmailValidator validator;

    @BeforeEach
    void setUp() {
        validator = new EmailValidator();
    }

    @Test
    @DisplayName("null应该通过校验")
    void shouldPassWhenNull() {
        assertValid(validator, null);
    }

    @Test
    @DisplayName("空字符串应该通过校验（由NotBlank处理）")
    void shouldPassWhenEmpty() {
        assertValid(validator, "");
    }

    @ParameterizedTest
    @DisplayName("有效邮箱应该通过校验")
    @ValueSource(strings = {
        "test@example.com",
        "user.name@example.com",
        "user+tag@example.com",
        "user@sub.example.com",
        "user@example.co.uk",
        "a@b.cn",
        "test123@example123.com",
        "user_name@example.com",
        "user-name@example.com",
        "USER@EXAMPLE.COM"
    })
    void shouldPassForValidEmails(String email) {
        assertValid(validator, email);
    }

    @ParameterizedTest
    @DisplayName("无效邮箱应该校验失败")
    @ValueSource(strings = {
        "plaintext",
        "@example.com",
        "user@",
        "user@@example.com",
        "user@.com",
        "user@example.",
        "user@-example.com",
        "user@example-.com",
        ".user@example.com",
        "user.@example.com",
        "user..name@example.com",
        "user@example..com",
        "a@b",  // 没有域名层级
        "user name@example.com"  // 包含空格
    })
    void shouldFailForInvalidEmails(String email) {
        assertInvalid(validator, email);
    }

    @Test
    @DisplayName("支持特殊字符")
    void shouldSupportSpecialCharacters() {
        assertValid(validator, "user!#$%&'*+-/=?^_`{|}~@example.com");
    }

    @Test
    @DisplayName("自定义消息应该生效")
    void shouldUseCustomMessage() {
        assertInvalidWithMessage(validator, "invalid-email", 
            params("message", "请输入正确的邮箱"), "请输入正确的邮箱");
    }

    @Test
    @DisplayName("非字符串类型应该校验失败")
    void shouldFailWhenNotString() {
        assertInvalid(validator, 12345);
    }
}