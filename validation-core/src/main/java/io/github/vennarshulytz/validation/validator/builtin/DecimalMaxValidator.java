package io.github.vennarshulytz.validation.validator.builtin;

import io.github.vennarshulytz.validation.annotation.constraints.DecimalMaxCheck;
import io.github.vennarshulytz.validation.constant.MessageConstants;
import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 小数最大值校验器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class DecimalMaxValidator extends AbstractDecimalValidator implements FieldValidator {

    @Override
    public String validate(String fieldName, Object value, Map<String, Object> params, boolean enableI18n) {
        if (value == null) {
            return null;
        }

        // String maxStr = params.get("value");
        // if (maxStr == null || maxStr.isEmpty()) {
        //     return null;
        // }

        Object maxObject = params.get("value");
        if (maxObject == null) {
            return null;
        }
        String maxStr = maxObject.toString();
        if (maxStr.isEmpty()) {
            return null;
        }

        // boolean inclusive = !"false".equalsIgnoreCase(params.get("inclusive"));
        boolean inclusive;
        Object inclusiveObject = params.get("inclusive");

        if (inclusiveObject == null) {
            inclusive = true;
        } else if (inclusiveObject instanceof Boolean) {
            inclusive = (boolean) inclusiveObject;
        } else {
            inclusive = Boolean.parseBoolean((String) inclusiveObject);
        }
        params.put("inclusive", inclusive);


        // BigDecimal maxValue;
        // try {
        //     maxValue = new BigDecimal(maxStr);
        // } catch (NumberFormatException e) {
        //     return "max参数格式错误";
        // }

        // value 参数格式错误可能会有异常，直接抛出
        BigDecimal maxValue = new BigDecimal(maxStr);

        BigDecimal actualValue = toBigDecimal(value);
        if (actualValue == null) {
            return null; // 无法转换的类型跳过校验
        }

        int comparison = actualValue.compareTo(maxValue);
        boolean valid = inclusive ? comparison <= 0 : comparison < 0;

        if (!valid) {
            return getErrorMessage(MessageConstants.DecimalMax, params, enableI18n);
        }

        return null;
    }

    @Override
    public Map<String, Object> parseParams(Annotation annotation) {

        if (annotation instanceof DecimalMaxCheck) {
            DecimalMaxCheck decimalMaxCheck = (DecimalMaxCheck) annotation;
            Map<String, Object> params = new HashMap<>(5);
            params.put("message", decimalMaxCheck.message());
            params.put("value", decimalMaxCheck.value());
            params.put("inclusive", decimalMaxCheck.inclusive());
            return params;
        }
        return Collections.emptyMap();
    }



    @Override
    public String getDefaultMessage() {
        // 必须小于(或等于){value}
        return "must be less than ${inclusive == true ? 'or equal to ' : ''}{value}";
    }


}

