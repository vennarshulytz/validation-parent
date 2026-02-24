package io.github.vennarshulytz.validation.utils;

/**
 * 路径匹配工具类
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public final class PathMatchUtil {

    private PathMatchUtil() {
    }

    public static boolean match(String candidate, String pattern) {
        if (candidate == null || pattern == null) {
            return false;
        }

        int ci = 0, pi = 0;
        int clen = candidate.length();
        int plen = pattern.length();

        while (pi < plen) {
            // 1. 解析段名
            int pNameStart = pi;
            while (pi < plen && pattern.charAt(pi) != '.' && pattern.charAt(pi) != '[') {
                pi++;
            }
            int pNameEnd = pi;

            if (ci >= clen) {
                return false;
            }

            int cNameStart = ci;
            while (ci < clen && candidate.charAt(ci) != '.' && candidate.charAt(ci) != '[') {
                ci++;
            }
            int cNameEnd = ci;

            if (!regionEquals(candidate, cNameStart, cNameEnd,
                    pattern, pNameStart, pNameEnd)) {
                return false;
            }

            // 2. pattern 中的 [] 必须被满足
            while (pi < plen && pattern.charAt(pi) == '[') {
                if (ci >= clen || candidate.charAt(ci) != '[') {
                    return false;
                }

                int pClose = findCloseBracket(pattern, pi);
                int cClose = findCloseBracket(candidate, ci);

                if (!regionEquals(candidate, ci + 1, cClose,
                        pattern, pi + 1, pClose)) {
                    return false;
                }

                pi = pClose + 1;
                ci = cClose + 1;
            }

            // 3. candidate 可以有多余的 []
            while (ci < clen && candidate.charAt(ci) == '[') {
                ci = findCloseBracket(candidate, ci) + 1;
            }

            // 4. 段结束处理
            if (pi < plen && pattern.charAt(pi) == '.') {
                pi++;
                if (ci < clen && candidate.charAt(ci) == '.') {
                    ci++;
                } else {
                    return false;
                }
            }
        }

        // 5. candidate 不能再有新的段
        while (ci < clen && candidate.charAt(ci) == '[') {
            ci = findCloseBracket(candidate, ci) + 1;
        }

        return ci == clen;
    }

    private static int findCloseBracket(String s, int openIndex) {
        int i = openIndex + 1;
        while (i < s.length() && s.charAt(i) != ']') {
            i++;
        }
        return i;
    }

    private static boolean regionEquals(
            String s1, int s1Start, int s1End,
            String s2, int s2Start, int s2End) {

        int len = s1End - s1Start;
        if (len != s2End - s2Start) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (s1.charAt(s1Start + i) != s2.charAt(s2Start + i)) {
                return false;
            }
        }
        return true;
    }
}
