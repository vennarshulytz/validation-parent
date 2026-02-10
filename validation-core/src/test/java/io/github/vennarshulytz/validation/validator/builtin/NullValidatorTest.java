package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * NullValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("NullValidator 测试")
class NullValidatorTest extends ValidatorTestBase {

    private NullValidator validator;

    @BeforeEach
    void setUp() {
        validator = new NullValidator();
    }

    @Test
    @DisplayName("null值应该通过校验")
    void shouldPassWhenValueIsNull() {
        assertValid(validator, null);
    }

    @Test
    @DisplayName("非null值应该校验失败")
    void shouldFailWhenValueIsNotNull() {
        assertInvalid(validator, "not null");
        assertInvalid(validator, 123);
        assertInvalid(validator, new Object());
    }

    @Test
    @DisplayName("自定义消息应该生效")
    void shouldUseCustomMessage() {
        assertInvalidWithMessage(validator, "value",
                params("message", "必须为空"), "必须为空");
    }
}
