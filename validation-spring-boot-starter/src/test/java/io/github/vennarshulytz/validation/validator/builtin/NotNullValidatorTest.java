package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * NotBlankValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("NotNullValidator 测试")
class NotNullValidatorTest extends ValidatorTestBase {

    private NotNullValidator validator;

    @BeforeEach
    void setUp() {
        validator = new NotNullValidator();
    }

    @Test
    @DisplayName("非null值应该通过校验")
    void shouldPassWhenValueIsNotNull() {
        assertValid(validator, "string");
        assertValid(validator, 0);
        assertValid(validator, "");
        assertValid(validator, new Object());
    }

    @Test
    @DisplayName("null值应该校验失败")
    void shouldFailWhenValueIsNull() {
        assertInvalid(validator, null);
    }
}
