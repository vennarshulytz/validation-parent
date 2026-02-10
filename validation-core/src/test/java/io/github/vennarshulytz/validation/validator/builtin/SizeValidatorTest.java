package io.github.vennarshulytz.validation.validator.builtin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * SizeValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("SizeValidator 测试")
class SizeValidatorTest extends ValidatorTestBase {

    private SizeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SizeValidator();
    }

    @Test
    @DisplayName("null应该通过校验")
    void shouldPassWhenNull() {
        assertValid(validator, null, params("min", "1", "max", "10"));
    }

    @Test
    @DisplayName("字符串长度在范围内应该通过校验")
    void shouldPassWhenStringSizeInRange() {
        assertValid(validator, "hello", params("min", "1", "max", "10"));
        assertValid(validator, "a", params("min", "1", "max", "10"));
        assertValid(validator, "1234567890", params("min", "1", "max", "10"));
    }

    @Test
    @DisplayName("字符串长度超出范围应该校验失败")
    void shouldFailWhenStringSizeOutOfRange() {
        assertInvalid(validator, "", params("min", "1", "max", "10"));
        assertInvalid(validator, "12345678901", params("min", "1", "max", "10"));
    }

    @Test
    @DisplayName("集合大小在范围内应该通过校验")
    void shouldPassWhenCollectionSizeInRange() {
        assertValid(validator, ImmutableList.of("a", "b", "c"), params("min", "1", "max", "5"));
        assertValid(validator, ImmutableSet.of(1, 2, 3), params("min", "1", "max", "5"));
    }

    @Test
    @DisplayName("集合大小超出范围应该校验失败")
    void shouldFailWhenCollectionSizeOutOfRange() {
        assertInvalid(validator, Collections.emptyList(), params("min", "1", "max", "5"));
        assertInvalid(validator, ImmutableList.of(1, 2, 3, 4, 5, 6), params("min", "1", "max", "5"));
    }

    @Test
    @DisplayName("Map大小在范围内应该通过校验")
    void shouldPassWhenMapSizeInRange() {
        assertValid(validator, ImmutableMap.of("a", 1, "b", 2), params("min", "1", "max", "5"));
    }

    @Test
    @DisplayName("数组大小在范围内应该通过校验")
    void shouldPassWhenArraySizeInRange() {
        assertValid(validator, new String[]{"a", "b", "c"}, params("min", "1", "max", "5"));
        assertValid(validator, new int[]{1, 2, 3}, params("min", "1", "max", "5"));
    }

    @Test
    @DisplayName("数组大小超出范围应该校验失败")
    void shouldFailWhenArraySizeOutOfRange() {
        assertInvalid(validator, new String[]{}, params("min", "1", "max", "5"));
        assertInvalid(validator, new int[]{1, 2, 3, 4, 5, 6}, params("min", "1", "max", "5"));
    }

    @Test
    @DisplayName("只设置最小值")
    void shouldValidateWithOnlyMin() {
        assertValid(validator, "hello", params("min", "3"));
        assertInvalid(validator, "hi", params("min", "3"));
    }

    @Test
    @DisplayName("只设置最大值")
    void shouldValidateWithOnlyMax() {
        assertValid(validator, "hi", params("max", "5"));
        assertInvalid(validator, "hello world", params("max", "5"));
    }
}