package io.github.vennarshulytz.validation.validator.builtin;

import java.time.*;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间校验器基类（提供通用的时间转换方法）
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public abstract class AbstractTemporalValidator {

    /**
     * 将值转换为Instant进行比较
     */
    protected Instant toInstant(Object value) {
        if (value instanceof Instant) {
            return (Instant) value;
        }
        if (value instanceof LocalDateTime) {
            LocalDateTime ldt = (LocalDateTime) value;
            return ldt.atZone(ZoneId.systemDefault()).toInstant();
        }
        if (value instanceof LocalDate) {
            LocalDate ld = (LocalDate) value;
            return ld.atStartOfDay(ZoneId.systemDefault()).toInstant();
        }
        if (value instanceof LocalTime) {
            LocalTime lt = (LocalTime) value;
            return lt.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant();
        }
        if (value instanceof ZonedDateTime) {
            ZonedDateTime zdt = (ZonedDateTime) value;
            return zdt.toInstant();
        }
        if (value instanceof OffsetDateTime) {
            OffsetDateTime odt = (OffsetDateTime) value;
            return odt.toInstant();
        }
        if (value instanceof OffsetTime) {
            OffsetTime ot = (OffsetTime) value;
            return ot.atDate(LocalDate.now()).toInstant();
        }
        if (value instanceof Year) {
            Year y = (Year) value;
            return y.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        }
        if (value instanceof YearMonth) {
            YearMonth ym = (YearMonth) value;
            return ym.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        }
        if (value instanceof MonthDay) {
            MonthDay md = (MonthDay) value;
            return md.atYear(Year.now().getValue()).atStartOfDay(ZoneId.systemDefault()).toInstant();
        }
        if (value instanceof Date) {
            Date date = (Date) value;
            return date.toInstant();
        }
        if (value instanceof Calendar) {
            Calendar cal = (Calendar) value;
            return cal.toInstant();
        }
        // 处理Chrono类型
        if (value instanceof ChronoZonedDateTime<?>) {
            ChronoZonedDateTime<?> czdt = (ChronoZonedDateTime<?>) value;
            return czdt.toInstant();
        }
        if (value instanceof ChronoLocalDateTime<?>) {
            ChronoLocalDateTime<?> cldt = (ChronoLocalDateTime<?>) value;
            return cldt.atZone(ZoneId.systemDefault()).toInstant();
        }
        if (value instanceof ChronoLocalDate) {
            ChronoLocalDate cld = (ChronoLocalDate) value;
            return cld.atTime(LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant();
        }
        return null;
    }

    /**
     * 判断是否是支持的时间类型
     */
    protected boolean isSupportedType(Object value) {
        return value instanceof Temporal
                || value instanceof Date
                || value instanceof Calendar;
    }
}
