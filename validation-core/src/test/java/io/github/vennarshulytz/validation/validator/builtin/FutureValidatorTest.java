package io.github.vennarshulytz.validation.validator.builtin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Calendar;
import java.util.Date;

/**
 * FutureValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("FutureValidator 测试")
class FutureValidatorTest extends ValidatorTestBase {

    private FutureValidator validator;

    @BeforeEach
    void setUp() {
        validator = new FutureValidator();
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
        assertValid(validator, Instant.now().plusSeconds(3600));
        assertValid(validator, ZonedDateTime.now().plusDays(1));
        assertValid(validator, OffsetDateTime.now().plusDays(1));
    }

    @Test
    @DisplayName("当前时间应该校验失败")
    void shouldFailWhenPresent() {
        // 由于时间精度问题，使用稍早的时间
        assertInvalid(validator, LocalDateTime.now().minusSeconds(1));
    }

    @Test
    @DisplayName("过去的时间应该校验失败")
    void shouldFailWhenPast() {
        assertInvalid(validator, LocalDateTime.now().minusDays(1));
        assertInvalid(validator, LocalDate.now().minusDays(1));
        assertInvalid(validator, Instant.now().minusSeconds(3600));
    }

    @Test
    @DisplayName("支持Date类型")
    void shouldSupportDate() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);
        assertValid(validator, futureDate);
        
        Date pastDate = new Date(System.currentTimeMillis() - 86400000);
        assertInvalid(validator, pastDate);
    }

    @Test
    @DisplayName("支持Calendar类型")
    void shouldSupportCalendar() {
        Calendar future = Calendar.getInstance();
        future.add(Calendar.DAY_OF_MONTH, 1);
        assertValid(validator, future);

        Calendar past = Calendar.getInstance();
        past.add(Calendar.DAY_OF_MONTH, -1);
        assertInvalid(validator, past);
    }

    @Test
    @DisplayName("支持Year类型")
    void shouldSupportYear() {
        assertValid(validator, Year.now().plusYears(1));
        assertInvalid(validator, Year.now().minusYears(1));
    }

    @Test
    @DisplayName("支持YearMonth类型")
    void shouldSupportYearMonth() {
        assertValid(validator, YearMonth.now().plusMonths(1));
        assertInvalid(validator, YearMonth.now().minusMonths(1));
    }
}