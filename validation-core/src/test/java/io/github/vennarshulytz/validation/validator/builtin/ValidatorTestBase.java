package io.github.vennarshulytz.validation.validator.builtin;

import io.github.vennarshulytz.validation.validator.FieldValidator;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 校验器测试基类
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public abstract class ValidatorTestBase {

    /**
     * 断言校验通过
     */
    protected void assertValid(FieldValidator validator, Object value) {
        assertValid(validator, value, new HashMap<>());
    }

    protected void assertValid(FieldValidator validator, Object value, Map<String, Object> params) {
        String result = validator.validate("testField", value, params, false);
        assertNull(result, "Expected validation to pass, but got error: " + result);
    }

    /**
     * 断言校验失败
     */
    protected void assertInvalid(FieldValidator validator, Object value) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("message", validator.getDefaultMessage());
        assertInvalid(validator, value, params);
    }

    protected void assertInvalid(FieldValidator validator, Object value, Map<String, Object> params) {
        if (!params.containsKey("message")) {
            params.put("message", validator.getDefaultMessage());
        }

        String result = validator.validate("testField", value, params, false);
        assertNotNull(result, "Expected validation to fail, but it passed");
    }

    /**
     * 断言校验失败并包含指定消息
     */
    protected void assertInvalidWithMessage(FieldValidator validator, Object value,
                                            Map<String, Object> params, String expectedMessage) {
        String result = validator.validate("testField", value, params, false);
        assertNotNull(result, "Expected validation to fail");
        assertEquals(expectedMessage, result);
    }

    /**
     * 创建参数Map的便捷方法
     */
    protected Map<String, Object> params(String... keyValues) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < keyValues.length - 1; i += 2) {
            map.put(keyValues[i], keyValues[i + 1]);
        }
        return map;
    }
}
