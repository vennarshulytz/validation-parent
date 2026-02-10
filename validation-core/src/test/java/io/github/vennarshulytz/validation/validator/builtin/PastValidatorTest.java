package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Calendar;
import java.util.Date;

/**
 * PastValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("PastValidator 测试")
class PastValidatorTest extends ValidatorTestBase {

    private PastValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PastValidator();
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
        assertValid(validator, Instant.now().minusSeconds(3600));
        assertValid(validator, ZonedDateTime.now().minusDays(1));
        assertValid(validator, OffsetDateTime.now().minusDays(1));
    }

    @Test
    @DisplayName("当前时间应该校验失败")
    void shouldFailWhenPresent() {
        assertInvalid(validator, LocalDateTime.now().plusSeconds(1));
    }

    @Test
    @DisplayName("将来的时间应该校验失败")
    void shouldFailWhenFuture() {
        assertInvalid(validator, LocalDateTime.now().plusDays(1));
        assertInvalid(validator, LocalDate.now().plusDays(1));
        assertInvalid(validator, Instant.now().plusSeconds(3600));
    }

    @Test
    @DisplayName("支持Date类型")
    void shouldSupportDate() {
        Date pastDate = new Date(System.currentTimeMillis() - 86400000);
        assertValid(validator, pastDate);
        
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);
        assertInvalid(validator, futureDate);
    }

    @Test
    @DisplayName("支持Calendar类型")
    void shouldSupportCalendar() {
        Calendar past = Calendar.getInstance();
        past.add(Calendar.DAY_OF_MONTH, -1);
        assertValid(validator, past);

        Calendar future = Calendar.getInstance();
        future.add(Calendar.DAY_OF_MONTH, 1);
        assertInvalid(validator, future);
    }
}