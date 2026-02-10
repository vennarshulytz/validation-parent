package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * AssertFalseValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("AssertFalseValidator 测试")
class AssertFalseValidatorTest extends ValidatorTestBase {

    private AssertFalseValidator validator;

    @BeforeEach
    void setUp() {
        validator = new AssertFalseValidator();
    }

    @Test
    @DisplayName("null应该通过校验")
    void shouldPassWhenNull() {
        assertValid(validator, null);
    }

    @Test
    @DisplayName("false应该通过校验")
    void shouldPassWhenFalse() {
        assertValid(validator, false);
        assertValid(validator, Boolean.FALSE);
    }

    @Test
    @DisplayName("true应该校验失败")
    void shouldFailWhenTrue() {
        assertInvalid(validator, true);
        assertInvalid(validator, Boolean.TRUE);
    }

    @Test
    @DisplayName("非Boolean类型应该通过校验")
    void shouldPassWhenNotBoolean() {
        assertValid(validator, "false");
        assertValid(validator, 0);
    }
}