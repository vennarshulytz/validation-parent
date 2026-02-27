package io.github.vennarshulytz.validation.validator.builtin;

import io.github.vennarshulytz.validation.core.model.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 校验器测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
class ValidatorTest {

    @Test
    @DisplayName("测试NotNull校验器 - 字段为null时应该失败")
    void testNotNullValidator_WhenFieldIsNull_ShouldFail() {
        Employee employee = new Employee();
        employee.setId(null);
        employee.setName("张三");

        NotNullValidator validator = new NotNullValidator();
        Map<String, Object> params = new HashMap<>();
        params.put("message", "id不能为null");

        String result = validator.validate("id", employee.getId(), params, false);

        assertNotNull(result);
        assertEquals("id不能为null", result);
    }

    @Test
    @DisplayName("测试NotNull校验器 - 字段不为null时应该成功")
    void testNotNullValidator_WhenFieldIsNotNull_ShouldPass() {
        NotNullValidator validator = new NotNullValidator();
        Map<String, Object> params = new HashMap<>();

        String result = validator.validate("id", "123", params, false);

        assertNull(result);
    }

    @Test
    @DisplayName("测试NotBlank校验器 - 空白字符串应该失败")
    void testNotBlankValidator_WhenFieldIsBlank_ShouldFail() {
        NotBlankValidator validator = new NotBlankValidator();
        Map<String, Object> params = new HashMap<>();
        params.put("message", "不能为空白");

        String result = validator.validate("name", "   ", params, false);

        assertNotNull(result);
        assertEquals("不能为空白", result);
    }

    @Test
    @DisplayName("测试NotEmpty校验器 - 空集合应该失败")
    void testNotEmptyValidator_WhenCollectionIsEmpty_ShouldFail() {
        NotEmptyValidator validator = new NotEmptyValidator();
        Map<String, Object> params = new HashMap<>();
        params.put("message", "集合不能为空");

        String result = validator.validate("list", new ArrayList<>(), params, false);

        assertNotNull(result);
        assertEquals("集合不能为空", result);
    }

    @Test
    @DisplayName("测试Size校验器 - 超出范围应该失败")
    void testSizeValidator_WhenOutOfRange_ShouldFail() {
        SizeValidator validator = new SizeValidator();
        Map<String, Object> params = new HashMap<>();
        params.put("min", "2");
        params.put("max", "5");
        params.put("message", "大小必须在{min}和{max}之间");

        String result = validator.validate("name", "a", params, false);

        assertNotNull(result);
    }

    @Test
    @DisplayName("测试Pattern校验器 - 不匹配正则应该失败")
    void testPatternValidator_WhenNotMatch_ShouldFail() {
        PatternValidator validator = new PatternValidator();
        Map<String, Object> params = new HashMap<>();
        params.put("regexp", "^\\d+$");
        params.put("message", "必须是数字");

        String result = validator.validate("code", "abc", params, false);

        assertNotNull(result);
        assertEquals("必须是数字", result);
    }

    @Test
    @DisplayName("测试Pattern校验器 - 匹配正则应该成功")
    void testPatternValidator_WhenMatch_ShouldPass() {
        PatternValidator validator = new PatternValidator();
        Map<String, Object> params = new HashMap<>();
        params.put("regexp", "^\\d+$");

        String result = validator.validate("code", "12345", params, false);

        assertNull(result);
    }
}