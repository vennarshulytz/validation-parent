package io.github.vennarshulytz.validation.core;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字段访问器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class FieldAccessor {

    private static final Map<Class<?>, Map<String, Field>> FIELD_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取字段值
     */
    public static Object getFieldValue(Object target, String fieldName) {
        if (target == null || fieldName == null || fieldName.isEmpty()) {
            return null;
        }

        try {
            Field field = getField(target.getClass(), fieldName);
            if (field == null) {
                return null;
            }
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access field: " + fieldName, e);
        }
    }

    /**
     * 获取字段
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        Map<String, Field> fields = FIELD_CACHE.computeIfAbsent(clazz, FieldAccessor::cacheFields);
        return fields.get(fieldName);
    }

    /**
     * 获取所有字段
     */
    public static Map<String, Field> getAllFields(Class<?> clazz) {
        return FIELD_CACHE.computeIfAbsent(clazz, FieldAccessor::cacheFields);
    }

    /**
     * 缓存类的所有字段
     */
    private static Map<String, Field> cacheFields(Class<?> clazz) {
        Map<String, Field> fieldMap = new HashMap<>();
        Class<?> current = clazz;

        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                field.setAccessible(true);
                fieldMap.putIfAbsent(field.getName(), field);
            }
            current = current.getSuperclass();
        }

        return fieldMap;
    }

    /**
     * 查找目标类型的所有实例
     *
     * @param root 根对象
     * @param targetType 目标类型
     * @param targetPath 目标路径（空字符串表示匹配所有）
     * @param excludedPaths 需要排除的路径集合（仅当targetPath为空时使用）
     * @return 匹配的实例列表
     */
    public static List<TypedInstance> findInstancesByType(Object root, Class<?> targetType,
                                                          String targetPath, Set<String> excludedPaths) {
        List<TypedInstance> result = new ArrayList<>();
        findInstancesRecursive(root, targetType, targetPath, excludedPaths, "", result, new IdentityHashMap<>());
        return result;
    }

    private static void findInstancesRecursive(Object obj, Class<?> targetType, String targetPath,
                                               Set<String> excludedPaths, String currentPath,
                                               List<TypedInstance> result, Map<Object, Boolean> visited) {
        if (obj == null || visited.containsKey(obj)) {
            return;
        }

        // 避免基本类型和常用类型的深度遍历
        if (isSimpleType(obj.getClass())) {
            return;
        }

        visited.put(obj, Boolean.TRUE);

        // 处理集合
        if (obj instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) obj;

            int index = 0;
            for (Object item : collection) {
                // 集合元素的路径保持与集合属性相同（用于匹配）
                String itemPath = currentPath.isEmpty() ? "[" + index + "]" : currentPath + "[" + index + "]";
                findInstancesRecursive(item, targetType, targetPath, excludedPaths, itemPath, result, visited);
                index++;
            }
            return;
        }

        // 处理Map
        if (obj instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) obj;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String keyPath = currentPath.isEmpty() ? "[" + entry.getKey() + "]" : currentPath + "[" + entry.getKey() + "]";
                findInstancesRecursive(entry.getValue(), targetType, targetPath, excludedPaths, keyPath, result, visited);
            }
            return;
        }

        // 检查当前对象是否匹配
        if (targetType.isInstance(obj)) {
            boolean shouldInclude = shouldIncludeInstance(currentPath, targetPath, excludedPaths);
            if (shouldInclude) {
                result.add(new TypedInstance(obj, currentPath));
            }
        }

        // 递归处理字段
        Map<String, Field> fields = getAllFields(obj.getClass());
        for (Map.Entry<String, Field> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            Field field = entry.getValue();

            try {
                Object fieldValue = field.get(obj);
                if (fieldValue != null) {
                    String fieldPath = currentPath.isEmpty() ? fieldName : currentPath + "." + fieldName;
                    findInstancesRecursive(fieldValue, targetType, targetPath, excludedPaths, fieldPath, result, visited);
                }
            } catch (IllegalAccessException e) {
                // ignore
            }
        }
    }

    /**
     * 判断实例是否应该被包含
     */
    private static boolean shouldIncludeInstance(String currentPath, String targetPath, Set<String> excludedPaths) {
        // 如果指定了具体路径，则精确匹配
        if (targetPath != null && !targetPath.isEmpty()) {
            return pathMatches(currentPath, targetPath);
        }

        // 空路径：匹配所有，但排除已明确指定的路径
        if (excludedPaths != null && !excludedPaths.isEmpty()) {
            for (String excludedPath : excludedPaths) {
                if (pathMatches(currentPath, excludedPath)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 路径匹配
     */
    private static boolean pathMatches(String currentPath, String targetPath) {
        if (targetPath == null || targetPath.isEmpty()) {
            return true;
        }

        if (currentPath == null || currentPath.isEmpty()) {
            return false;
        }

        // 精确匹配
        if (currentPath.equals(targetPath)) {
            return true;
        }

        // 以目标路径结尾匹配（支持嵌套）
        if (currentPath.endsWith("." + targetPath)) {
            return true;
        }

       // // 支持路径前缀匹配（如 managerList1 匹配 managerList1.xxx）
       // if (currentPath.startsWith(targetPath + ".") || currentPath.startsWith(targetPath + "[")) {
       //     return false; // 只匹配精确路径，不匹配子路径
       // }

        // 移除数组索引进行匹配
        String normalizedCurrent = currentPath.replaceAll("\\[\\d+\\]", "");
        String normalizedTarget = targetPath.replaceAll("\\[\\d+\\]", "");

        // 精确匹配或以目标路径结尾
        return normalizedCurrent.equals(normalizedTarget) || normalizedCurrent.endsWith("." + normalizedTarget);

        // return false;
    }

    private static boolean isSimpleType(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == String.class
                || Number.class.isAssignableFrom(clazz)
                || clazz == Boolean.class
                || clazz == Character.class
                || clazz.isEnum()
                || Date.class.isAssignableFrom(clazz)
                || clazz.getName().startsWith("java.time");
    }

    /**
     * 类型化实例
     */
    public static class TypedInstance {
        private final Object instance;
        private final String path;

        public TypedInstance(Object instance, String path) {
            this.instance = instance;
            this.path = path;
        }

        public Object getInstance() {
            return instance;
        }

        public String getPath() {
            return path;
        }

        @Override
        public String toString() {
            return "TypedInstance{path='" + path + "', instance=" + instance.getClass().getSimpleName() + "}";
        }
    }
}