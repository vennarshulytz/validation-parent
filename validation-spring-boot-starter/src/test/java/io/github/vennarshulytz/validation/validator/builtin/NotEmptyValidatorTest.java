package io.github.vennarshulytz.validation.validator.builtin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * NotEmptyValidator 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
@DisplayName("NotEmptyValidator 测试")
class NotEmptyValidatorTest extends ValidatorTestBase {

    private NotEmptyValidator validator;

    @BeforeEach
    void setUp() {
        validator = new NotEmptyValidator();
    }

    @Test
    @DisplayName("非空字符串应该通过校验")
    void shouldPassWhenStringIsNotEmpty() {
        assertValid(validator, "hello");
        assertValid(validator, " ");
    }

    @Test
    @DisplayName("非空集合应该通过校验")
    void shouldPassWhenCollectionIsNotEmpty() {
        assertValid(validator, ImmutableList.of("a"));
        assertValid(validator, ImmutableSet.of(1, 2, 3));
        assertValid(validator, new ArrayList<>(ImmutableList.of("item")));
    }

    @Test
    @DisplayName("非空Map应该通过校验")
    void shouldPassWhenMapIsNotEmpty() {
        assertValid(validator, ImmutableMap.of("key", "value"));
    }

    @Test
    @DisplayName("非空数组应该通过校验")
    void shouldPassWhenArrayIsNotEmpty() {
        assertValid(validator, new String[]{"a"});
        assertValid(validator, new int[]{1, 2, 3});
    }

    @Test
    @DisplayName("null应该校验失败")
    void shouldFailWhenNull() {
        assertInvalid(validator, null);
    }

    @Test
    @DisplayName("空字符串应该校验失败")
    void shouldFailWhenEmptyString() {
        assertInvalid(validator, "");
    }

    @Test
    @DisplayName("空集合应该校验失败")
    void shouldFailWhenEmptyCollection() {
        assertInvalid(validator, Collections.emptyList());
        assertInvalid(validator, Collections.emptySet());
        assertInvalid(validator, new ArrayList<>());
    }

    @Test
    @DisplayName("空Map应该校验失败")
    void shouldFailWhenEmptyMap() {
        assertInvalid(validator, Collections.emptyMap());
        assertInvalid(validator, new HashMap<>());
    }

    @Test
    @DisplayName("空数组应该校验失败")
    void shouldFailWhenEmptyArray() {
        assertInvalid(validator, new String[]{});
        assertInvalid(validator, new int[]{});
    }

}
