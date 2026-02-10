package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;

/**
 * PastOrPresentValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("PastOrPresentValidator 测试")
class PastOrPresentValidatorTest extends ValidatorTestBase {

    private PastOrPresentValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PastOrPresentValidator();
    }

    @Test
    @DisplayName("null应该通过校验")
    void shouldPassWhenNull() {
        assertValid(validator, null);
    }

    @Test
    @DisplayName("过去的时间应该通过校验")
    void shouldPassWhenPast() {
        assertValid(validator, LocalDateTime.now().minusDays(1));
        assertValid(validator, LocalDate.now().minusDays(1));
    }

    @Test
    @DisplayName("当前时间应该通过校验")
    void shouldPassWhenPresent() {
        // 使用稍早一点的时间确保测试稳定
        assertValid(validator, LocalDateTime.now().minusSeconds(1));
        assertValid(validator, Instant.now().minusMillis(100));
    }

    @Test
    @DisplayName("将来的时间应该校验失败")
    void shouldFailWhenFuture() {
        assertInvalid(validator, LocalDateTime.now().plusDays(1));
        assertInvalid(validator, LocalDate.now().plusDays(1));
        assertInvalid(validator, Instant.now().plusSeconds(3600));
    }
}