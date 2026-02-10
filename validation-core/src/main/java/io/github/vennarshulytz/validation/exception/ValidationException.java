package io.github.vennarshulytz.validation.exception;

import io.github.vennarshulytz.validation.validator.ValidationResult;

import java.util.List;

/**
 * 校验异常
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class ValidationException extends RuntimeException {

    private final List<ValidationResult.FieldError> errors;

    public ValidationException(String message, List<ValidationResult.FieldError> errors) {
        super(message);
        this.errors = errors;
    }

    public ValidationException(ValidationResult result) {
        super(result.getErrorMessage());
        this.errors = result.getErrors();
    }

    public List<ValidationResult.FieldError> getErrors() {
        return errors;
    }

}
