package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * PatternValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("PatternValidator 测试")
class PatternValidatorTest extends ValidatorTestBase {

    private PatternValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PatternValidator();
    }

    @Test
    @DisplayName("null应该通过校验")
    void shouldPassWhenNull() {
        assertValid(validator, null, params("regexp", ".*"));
    }

    @Test
    @DisplayName("匹配正则应该通过校验")
    void shouldPassWhenMatches() {
        assertValid(validator, "hello123", params("regexp", "^[a-z0-9]+$"));
        assertValid(validator, "12345", params("regexp", "^\\d+$"));
        assertValid(validator, "test@example.com", params("regexp", "^.+@.+\\..+$"));
    }

    @Test
    @DisplayName("不匹配正则应该校验失败")
    void shouldFailWhenNotMatches() {
        assertInvalid(validator, "Hello123", params("regexp", "^[a-z0-9]+$"));
        assertInvalid(validator, "abc", params("regexp", "^\\d+$"));
    }

    @Test
    @DisplayName("没有提供正则应该通过校验")
    void shouldPassWhenNoRegexp() {
        assertValid(validator, "anything");
        assertValid(validator, "test", params("regexp", ""));
    }

    @Test
    @DisplayName("手机号校验")
    void shouldValidatePhoneNumber() {
        assertValid(validator, "13800138000", params("regexp", "^1[3-9]\\d{9}$"));
        assertInvalid(validator, "12800138000", params("regexp", "^1[3-9]\\d{9}$"));
        assertInvalid(validator, "1380013800", params("regexp", "^1[3-9]\\d{9}$"));
    }

    @Test
    @DisplayName("身份证号校验")
    void shouldValidateIdCard() {
        String regexp = "^\\d{17}[\\dXx]$";
        assertValid(validator, "110101199001011234", params("regexp", regexp));
        assertValid(validator, "11010119900101123X", params("regexp", regexp));
        assertInvalid(validator, "1101011990010112", params("regexp", regexp));
    }

    @Test
    @DisplayName("自定义消息应该生效")
    void shouldUseCustomMessage() {
        assertInvalidWithMessage(validator, "abc", 
            params("regexp", "^\\d+$", "message", "只能输入数字"), "只能输入数字");
    }
}