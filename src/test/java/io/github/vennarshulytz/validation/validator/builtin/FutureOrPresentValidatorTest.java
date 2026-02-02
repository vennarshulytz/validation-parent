package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;

/**
 * FutureOrPresentValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("FutureOrPresentValidator 测试")
class FutureOrPresentValidatorTest extends ValidatorTestBase {

    private FutureOrPresentValidator validator;

    @BeforeEach
    void setUp() {
        validator = new FutureOrPresentValidator();
    }

    @Test
    @DisplayName("null应该通过校验")
    void shouldPassWhenNull() {
        assertValid(validator, null);
    }

    @Test
    @DisplayName("将来的时间应该通过校验")
    void shouldPassWhenFuture() {
        assertValid(validator, LocalDateTime.now().plusDays(1));
        assertValid(validator, LocalDate.now().plusDays(1));
    }

    @Test
    @DisplayName("当前时间应该通过校验")
    void shouldPassWhenPresent() {
        // 使用稍后一点的时间确保测试稳定
        assertValid(validator, LocalDateTime.now().plusSeconds(1));
        assertValid(validator, Instant.now().plusMillis(100));
    }

    @Test
    @DisplayName("过去的时间应该校验失败")
    void shouldFailWhenPast() {
        assertInvalid(validator, LocalDateTime.now().minusDays(1));
        assertInvalid(validator, LocalDate.now().minusDays(1));
        assertInvalid(validator, Instant.now().minusSeconds(3600));
    }
}