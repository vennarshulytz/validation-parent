package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.Before;
import org.junit.Test;

/**
 * NotBlankValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class NotNullValidatorTest extends ValidatorTestBase {

    private NotNullValidator validator;

    @Before
    public void setUp() {
        validator = new NotNullValidator();
    }

    /**
     * 非null值应该通过校验
     */
    @Test
    public void shouldPassWhenValueIsNotNull() {
        assertValid(validator, "string");
        assertValid(validator, 0);
        assertValid(validator, "");
        assertValid(validator, new Object());
    }

    /**
     * null值应该校验失败
     */
    @Test
    public void shouldFailWhenValueIsNull() {
        assertInvalid(validator, null);
    }
}
