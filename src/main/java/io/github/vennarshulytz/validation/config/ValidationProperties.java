package io.github.vennarshulytz.validation.config;


import io.github.vennarshulytz.validation.core.ValidationMode;

/**
 * 校验配置属性
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class ValidationProperties {

    private ValidationMode mode = ValidationMode.FAIL_FAST;
    private boolean enableI18n = false;

    public ValidationProperties() {
    }

    public ValidationProperties(ValidationMode mode, boolean enableI18n) {
        this.mode = mode;
        this.enableI18n = enableI18n;
    }

    public ValidationMode getMode() {
        return mode;
    }

    public void setMode(ValidationMode mode) {
        this.mode = mode;
    }

    public boolean isEnableI18n() {
        return enableI18n;
    }

    public void setEnableI18n(boolean enableI18n) {
        this.enableI18n = enableI18n;
    }
}