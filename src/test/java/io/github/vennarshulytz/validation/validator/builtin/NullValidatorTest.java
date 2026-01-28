package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.Before;
import org.junit.Test;

/**
 * NullValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class NullValidatorTest extends ValidatorTestBase {

    private NullValidator validator;

    @Before
    public void setUp() {
        validator = new NullValidator();
    }

    /**
     * null值应该通过校验
     */
    @Test
    public void shouldPassWhenValueIsNull() {
        assertValid(validator, null);
    }

    /**
     * 非null值应该校验失败
     */
    @Test
    public void shouldFailWhenValueIsNotNull() {
        assertInvalid(validator, "not null");
        assertInvalid(validator, 123);
        assertInvalid(validator, new Object());
    }

    /**
     * 自定义消息应该生效
     */
    @Test
    public void shouldUseCustomMessage() {
        assertInvalidWithMessage(validator, "value",
                params("message", "必须为空"), "必须为空");
    }

}
