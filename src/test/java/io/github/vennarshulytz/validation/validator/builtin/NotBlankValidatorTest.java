package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.Before;
import org.junit.Test;

/**
 * NotBlankValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class NotBlankValidatorTest extends ValidatorTestBase {

    private NotBlankValidator validator;

    @Before
    public void setUp() {
        validator = new NotBlankValidator();
    }

    /**
     * 非空白字符串应该通过校验
     */
    @Test
    public void shouldPassWhenStringIsNotBlank() {
        assertValid(validator, "hello");
        assertValid(validator, " hello ");
        assertValid(validator, "  a  ");
    }

    /**
     * null应该校验失败
     */
    @Test
    public void shouldFailWhenNull() {
        assertInvalid(validator, null);
    }

    /**
     * 空字符串应该校验失败
     */
    @Test
    public void shouldFailWhenEmpty() {
        assertInvalid(validator, "");
    }


    /**
     * 纯空白字符串应该校验失败
     */
    @Test
    public void shouldFailWhenBlank() {
        assertInvalid(validator, "   ");
        assertInvalid(validator, "\t");
        assertInvalid(validator, "\n");
        assertInvalid(validator, " \t\n ");
    }

    /**
     * 非字符串类型应该通过校验
     */
    @Test
    public void shouldPassWhenNotString() {
        assertValid(validator, 123);
        assertValid(validator, new Object());
    }
}
