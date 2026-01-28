package io.github.vennarshulytz.validation.validator;

import java.util.Map;

/**
 * 字段校验器接口
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public interface FieldValidator {

    /**
     * 校验字段值
     *
     * @param fieldName 字段名
     * @param value     字段值
     * @param params    校验参数
     * @return 校验失败返回错误信息，成功返回null
     */
    String validate(String fieldName, Object value, Map<String, String> params);

}
