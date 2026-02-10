package io.github.vennarshulytz.validation.utils;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * NamedPlaceholderResolverTest 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("NamedPlaceholderResolverTest 测试")
class NamedPlaceholderResolverTest {

    /* ---------- 普通占位符 ---------- */

    @Test
    @DisplayName("应正确替换普通命名占位符")
    void shouldReplaceNamedPlaceholders() {
        String template = "size must be between {min} and {max}";
        Map<String, Object> params = ImmutableMap.of(
                "min", 3,
                "max", 10
        );

        String result = NamedPlaceholderResolver.resolve(template, params);

        assertEquals("size must be between 3 and 10", result);
    }

    @Test
    @DisplayName("当占位符 key 不存在时应保留原样")
    void shouldKeepPlaceholderWhenKeyNotFound() {
        String template = "size must be >= {min}";
        Map<String, Object> params = Collections.emptyMap();

        String result = NamedPlaceholderResolver.resolve(template, params);

        assertEquals("size must be >= {min}", result);
    }

    @Test
    @DisplayName("当 key 存在但值为 null 时应输出字符串 null")
    void shouldRenderNullWhenKeyExistsButValueIsNull() {
        String template = "value is {v}";
        Map<String, Object> params = new HashMap<>();
        params.put("v", null);

        String result = NamedPlaceholderResolver.resolve(template, params);

        assertEquals("value is null", result);
    }

    /* ---------- 条件表达式 ---------- */

    @Test
    @DisplayName("当条件表达式为 true 时应渲染 true 分支")
    void shouldRenderTrueBranchWhenConditionIsTrue() {
        String template = "must be less than ${inclusive == true ? 'or equal to ' : ''}{value}";
        Map<String, Object> params = ImmutableMap.of(
                "inclusive", true,
                "value", 10
        );

        String result = NamedPlaceholderResolver.resolve(template, params);

        assertEquals("must be less than or equal to 10", result);
    }

    @Test
    @DisplayName("当条件表达式为 false 时应渲染 false 分支")
    void shouldRenderFalseBranchWhenConditionIsFalse() {
        String template = "must be less than ${inclusive == true ? 'or equal to ' : ''}{value}";
        Map<String, Object> params = ImmutableMap.of(
                "inclusive", false,
                "value", 10
        );

        String result = NamedPlaceholderResolver.resolve(template, params);

        assertEquals("must be less than 10", result);
    }

    @Test
    @DisplayName("应支持 == false 的条件表达式")
    void shouldSupportFalseConditionExpression() {
        String template = "flag is ${enabled == false ? 'off' : 'on'}";
        Map<String, Object> params = ImmutableMap.of(
                "enabled", false
        );

        String result = NamedPlaceholderResolver.resolve(template, params);

        assertEquals("flag is off", result);
    }

    /* ---------- 条件表达式异常/边界 ---------- */

    @Test
    @DisplayName("当条件表达式中的 key 不存在时应保留原表达式")
    void shouldKeepExpressionWhenConditionKeyNotExists() {
        String template = "value ${missing == true ? 'A' : 'B'}";
        Map<String, Object> params = Collections.emptyMap();

        String result = NamedPlaceholderResolver.resolve(template, params);

        assertEquals("value ${missing == true ? 'A' : 'B'}", result);
    }

    @Test
    @DisplayName("当条件表达式值不是 Boolean 时应保留原表达式")
    void shouldKeepExpressionWhenValueIsNotBoolean() {
        String template = "value ${flag == true ? 'A' : 'B'}";
        Map<String, Object> params = ImmutableMap.of(
                "flag", "yes"
        );

        String result = NamedPlaceholderResolver.resolve(template, params);

        assertEquals("value ${flag == true ? 'A' : 'B'}", result);
    }

    @Test
    @DisplayName("当条件表达式语法不合法时应保留原表达式")
    void shouldKeepExpressionWhenExpressionIsInvalid() {
        String template = "value ${flag ? 'A' : 'B'}";
        Map<String, Object> params = ImmutableMap.of(
                "flag", true
        );

        String result = NamedPlaceholderResolver.resolve(template, params);

        assertEquals("value ${flag ? 'A' : 'B'}", result);
    }

    /* ---------- 混合 & 顺序 ---------- */

    @Test
    @DisplayName("应支持同一模板中混合使用普通占位符与条件表达式")
    void shouldSupportMixedPlaceholdersInOneTemplate() {
        String template = "{field} must be ${required == true ? 'provided' : 'optional'}";
        Map<String, Object> params = ImmutableMap.of(
                "field", "name",
                "required", true
        );

        String result = NamedPlaceholderResolver.resolve(template, params);

        assertEquals("name must be provided", result);
    }

    /* ---------- 非法模板 ---------- */

    @Test
    @DisplayName("当普通占位符缺少右花括号时应忽略并原样输出")
    void shouldIgnoreUnclosedBrace() {
        String template = "value is {min";
        Map<String, Object> params = ImmutableMap.of("min", 1);

        String result = NamedPlaceholderResolver.resolve(template, params);

        assertEquals("value is {min", result);
    }

    @Test
    @DisplayName("当条件表达式缺少右花括号时应忽略并原样输出")
    void shouldIgnoreUnclosedExpression() {
        String template = "value is ${flag == true ? 'A' : 'B'";
        Map<String, Object> params = ImmutableMap.of("flag", true);

        String result = NamedPlaceholderResolver.resolve(template, params);

        assertEquals("value is ${flag == true ? 'A' : 'B'", result);
    }

    /* ---------- 空值 & 快速返回 ---------- */

    @Test
    @DisplayName("当参数 Map 为空时应直接返回模板原文")
    void shouldReturnTemplateWhenParamsIsEmpty() {
        String template = "hello {name}";

        String result = NamedPlaceholderResolver.resolve(template, Collections.emptyMap());

        assertEquals("hello {name}", result);
    }

    @Test
    @DisplayName("当模板字符串为空时应直接返回空字符串")
    void shouldReturnTemplateWhenTemplateIsEmpty() {
        String template = "";

        String result = NamedPlaceholderResolver.resolve(template, ImmutableMap.of("a", 1));

        assertEquals("", result);
    }
}