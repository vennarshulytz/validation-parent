package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * AssertTrueValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("AssertTrueValidator 测试")
class AssertTrueValidatorTest extends ValidatorTestBase {

    private AssertTrueValidator validator;

    @BeforeEach
    void setUp() {
        validator = new AssertTrueValidator();
    }

    @Test
    @DisplayName("null应该通过校验")
    void shouldPassWhenNull() {
        assertValid(validator, null);
    }

    @Test
    @DisplayName("true应该通过校验")
    void shouldPassWhenTrue() {
        assertValid(validator, true);
        assertValid(validator, Boolean.TRUE);
    }

    @Test
    @DisplayName("false应该校验失败")
    void shouldFailWhenFalse() {
        assertInvalid(validator, false);
        assertInvalid(validator, Boolean.FALSE);
    }

    @Test
    @DisplayName("非Boolean类型应该通过校验")
    void shouldPassWhenNotBoolean() {
        assertValid(validator, "true");
        assertValid(validator, 1);
    }

    @Test
    @DisplayName("自定义消息应该生效")
    void shouldUseCustomMessage() {
        assertInvalidWithMessage(validator, false, 
            params("message", "请同意协议"), "请同意协议");
    }
}