package io.github.vennarshulytz.validation.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PathMatchUtilTest 单元测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
class PathMatchUtilTest {

    @Test
    @DisplayName("结构一致时基础路径应匹配")
    void matchWithSameSegmentCount() {
        assertTrue(PathMatchUtil.match(
                "mangerMap.addressList",
                "mangerMap.addressList"));

        assertTrue(PathMatchUtil.match(
                "mangerMap[key1].addressList[0]",
                "mangerMap.addressList"));
    }

    @Test
    @DisplayName("candidate 段数多于 pattern 时不匹配")
    void candidateHasMoreSegmentsThanPattern() {
        assertFalse(PathMatchUtil.match(
                "mangerMap.addressList.detail",
                "mangerMap.addressList"));

        assertFalse(PathMatchUtil.match(
                "A.B.C",
                "A.B"));
    }

    @Test
    @DisplayName("candidate 段数少于 pattern 时不匹配")
    void candidateHasFewerSegmentsThanPattern() {
        assertFalse(PathMatchUtil.match(
                "mangerMap.addressList",
                "mangerMap.addressList[0].detail"));

        assertFalse(PathMatchUtil.match(
                "A.B",
                "A.B.C"));
    }

    @Test
    @DisplayName("pattern 指定 index 时仅匹配相同 index")
    void matchWithSpecificIndex() {
        assertTrue(PathMatchUtil.match(
                "mangerMap.addressList[0]",
                "mangerMap.addressList[0]"));

        assertFalse(PathMatchUtil.match(
                "mangerMap.addressList[1]",
                "mangerMap.addressList[0]"));
    }

    @Test
    @DisplayName("pattern 指定 key 时仅匹配相同 key")
    void matchWithSpecificKey() {
        assertTrue(PathMatchUtil.match(
                "mangerMap[key2].addressList[1]",
                "mangerMap[key2].addressList"));

        assertFalse(PathMatchUtil.match(
                "mangerMap[key1].addressList",
                "mangerMap[key2].addressList"));
    }

    @Test
    @DisplayName("pattern 同时指定 key 和 index 时必须完全匹配")
    void matchWithSpecificKeyAndIndex() {
        assertTrue(PathMatchUtil.match(
                "mangerMap[key2].addressList[1]",
                "mangerMap[key2].addressList[1]"));

        assertFalse(PathMatchUtil.match(
                "mangerMap[key2].addressList[0]",
                "mangerMap[key2].addressList[1]"));
    }

    @Test
    @DisplayName("null 输入应返回 false")
    void nullInputShouldReturnFalse() {
        assertFalse(PathMatchUtil.match(null, "A.B"));
        assertFalse(PathMatchUtil.match("A.B", null));
        assertFalse(PathMatchUtil.match(null, null));
    }
}
