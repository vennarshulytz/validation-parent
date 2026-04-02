package io.github.vennarshulytz.validation.validator;

import io.github.vennarshulytz.validation.exception.ValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * 校验结果
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class ValidationResult {

    private final List<FieldError> errors = new ArrayList<>();
    private final boolean failFast;
    private volatile boolean shouldStop = false;

    public ValidationResult(boolean failFast) {
        this.failFast = failFast;
    }

    public void addError(String field, String message, Object rejectedValue) {
        errors.add(new FieldError(field, message, rejectedValue));
        if (failFast) {
            shouldStop = true;
        }
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean shouldStop() {
        return shouldStop;
    }

    public List<FieldError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public String getErrorMessage() {
        return getErrorMessage("; ");
    }

    public String getErrorMessage(CharSequence delimiter) {
        if (errors.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner(delimiter);
        for (FieldError error : errors) {
            joiner.add(error.getField() + ": " + error.getMessage());
        }
        return joiner.toString();
    }

    public void throwIfInvalid() {
        if (hasErrors()) {
            throw new ValidationException(this);
        }
    }

    public <X extends Throwable> void throwIfInvalid(java.util.function.Function<ValidationResult, X> exceptionSupplier) throws X {
        if (hasErrors()) {
            throw exceptionSupplier.apply(this);
        }
    }

    /**
     * 字段错误
     */
    public static class FieldError {
        private final String field;
        private final String message;
        private final Object rejectedValue;

        public FieldError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }

        @Override
        public String toString() {
            return "FieldError{field='" + field + "', message='" + message + "'}";
        }
    }
}