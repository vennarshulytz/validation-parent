package io.github.vennarshulytz.validation.validator.builtin;


import io.github.vennarshulytz.validation.annotation.constraints.MaxCheck;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.FieldValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 最大值校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class MaxValidator extends AbstractRangeValidator implements FieldValidator {

    private static final Logger log = LoggerFactory.getLogger(MaxValidator.class);

    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, boolean enableI18n) {
        if (value == null) {
            return null;
        }





        // String maxStr = params.get("value");
        // if (maxStr == null || maxStr.isEmpty()) {
        //     return null;
        // }
        //
        // long maxValue;
        // try {
        //     maxValue = Long.parseLong(maxStr);
        // } catch (NumberFormatException e) {
        //     return "max参数格式错误";
        // }
        Object maxObject = params.get("value");
        if (maxObject == null) {
            return null;
        }

        long maxValue;
        if (maxObject instanceof Long) {
            maxValue = (long) maxObject;
        } else {
            // value 参数格式错误可能会有异常，直接抛出
            maxValue = Long.parseLong(maxObject.toString());
        }



        Long actualValue = toLong(value);
        if (actualValue == null) {
            // 尝试使用BigDecimal进行比较
            BigDecimal bd = toBigDecimal(value);
            if (bd != null && bd.compareTo(BigDecimal.valueOf(maxValue)) > 0) {
                return getErrorMessage(MessageConstants.Max, params, enableI18n);

            }
            return null;
        }

        if (actualValue > maxValue) {
            return getErrorMessage(MessageConstants.Max, params, enableI18n);

        }

        return null;
    }

    @Override
    public Map<String, Object> parseParams(Annotation annotation) {

        if (annotation instanceof MaxCheck) {
            MaxCheck maxCheck = (MaxCheck) annotation;
            Map<String, Object> params = new HashMap<>(3);
            params.put("message", maxCheck.message());
            params.put("value", maxCheck.value());
            return params;
        }
        return Collections.emptyMap();
    }


    @Override
    public String getDefaultMessage() {
        // 必须小于或等于{value}
        return "must be less than or equal to {value}";
    }
}
