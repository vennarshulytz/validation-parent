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
@DisplayName("NotBlankValidator 测试")
class NotBlankValidatorTest extends ValidatorTestBase {

    private NotBlankValidator validator;

    @BeforeEach
    void setUp() {
        validator = new NotBlankValidator();
    }

    @Test
    @DisplayName("非空白字符串应该通过校验")
    void shouldPassWhenStringIsNotBlank() {
        assertValid(validator, "hello");
        assertValid(validator, " hello ");
        assertValid(validator, "  a  ");
    }

    @Test
    @DisplayName("null应该校验失败")
    void shouldFailWhenNull() {
        assertInvalid(validator, null);
    }

    @Test
    @DisplayName("空字符串应该校验失败")
    void shouldFailWhenEmpty() {
        assertInvalid(validator, "");
    }

    @Test
    @DisplayName("纯空白字符串应该校验失败")
    void shouldFailWhenBlank() {
        assertInvalid(validator, "   ");
        assertInvalid(validator, "\t");
        assertInvalid(validator, "\n");
        assertInvalid(validator, " \t\n ");
    }

    @Test
    @DisplayName("非字符串类型应该通过校验")
    void shouldPassWhenNotString() {
        assertValid(validator, 123);
        assertValid(validator, new Object());
    }
}
