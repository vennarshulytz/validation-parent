package io.github.vennarshulytz.validation.core;

import io.github.vennarshulytz.validation.utils.PathMatchUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字段访问器
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class FieldAccessor {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    /**
     * 字段元数据缓存：Class -> 字段名 -> FieldMeta（包含 Field 和 MethodHandle）
     */
    private static final Map<Class<?>, Map<String, FieldMeta>> FIELD_CACHE = new ConcurrentHashMap<>();

    /**
     * 简单类型判断结果缓存
     */
    private static final Map<Class<?>, Boolean> SIMPLE_TYPE_CACHE = new ConcurrentHashMap<>();

    // ========================= 公共 API =========================

    /**
     * 获取字段值（基于 MethodHandle，高性能）
     */
    public static Object getFieldValue(Object target, String fieldName) {
        if (target == null || fieldName == null || fieldName.isEmpty()) {
            return null;
        }
        FieldMeta meta = getFieldMeta(target.getClass(), fieldName);
        if (meta == null) {
            return null;
        }
        return meta.getValue(target);
    }

    /**
     * 获取字段
     */
    @Deprecated
    public static Field getField(Class<?> clazz, String fieldName) {
        FieldMeta meta = getFieldMeta(clazz, fieldName);
        return meta != null ? meta.field : null;
    }

    /**
     * 获取所有字段（返回不可变视图）
     */
    @Deprecated
    public static Map<String, Field> getAllFields(Class<?> clazz) {
        Map<String, FieldMeta> metaMap = getFieldMetaMap(clazz);
        // 构建 Field 视图（仅在需要兼容旧 API 时使用）
        Map<String, Field> result = new HashMap<>(metaMap.size());
        for (Map.Entry<String, FieldMeta> entry : metaMap.entrySet()) {
            result.put(entry.getKey(), entry.getValue().field);
        }
        return Collections.unmodifiableMap(result);
    }

    /**
     * 惰性查找目标类型的实例
     * <p>
     * 返回一个 {@link Iterable}，内部使用显式栈驱动的 DFS 迭代器。
     * 每次 {@code next()} 仅推进到下一个匹配项，调用方可随时停止，
     * 无需全量收集。
     *
     * @param root          根对象
     * @param targetType    目标类型
     * @param targetPath    目标路径（空字符串表示匹配所有）
     * @param excludedPaths 需要排除的路径集合（仅当 targetPath 为空时使用）
     * @return 可迭代的匹配实例（惰性求值）
     */
    public static Iterable<TypedInstance> findInstancesByTypeLazy(Object root, Class<?> targetType,
                                                              String targetPath, Set<String> excludedPaths) {
        return () -> new TypedInstanceIterator(root, targetType, targetPath, excludedPaths);
    }

    @Deprecated
    public static List<TypedInstance> findInstancesByType(Object root, Class<?> targetType,
                                                          String targetPath, Set<String> excludedPaths) {
        List<TypedInstance> result = new ArrayList<>();
        for (TypedInstance typedInstance : findInstancesByTypeLazy(root, targetType, targetPath, excludedPaths)) {
            result.add(typedInstance);
        }
        return result;
    }

    // ========================= 内部实现 =========================

    private static FieldMeta getFieldMeta(Class<?> clazz, String fieldName) {
        return getFieldMetaMap(clazz).get(fieldName);
    }

    private static Map<String, FieldMeta> getFieldMetaMap(Class<?> clazz) {
        return FIELD_CACHE.computeIfAbsent(clazz, FieldAccessor::buildFieldMeta);
    }

    /**
     * 构建类的字段元数据并缓存（含 MethodHandle）
     */
    private static Map<String, FieldMeta> buildFieldMeta(Class<?> clazz) {
        Map<String, FieldMeta> metaMap = new HashMap<>();
        Class<?> current = clazz;

        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                String fieldName = field.getName();
                if (metaMap.containsKey(fieldName)) {
                    continue; // 子类字段优先
                }
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || field.isSynthetic()) {
                    continue;
                }
                field.setAccessible(true);
                MethodHandle handle;
                try {
                    handle = LOOKUP.unreflectGetter(field);
                } catch (IllegalAccessException e) {
                    // fallback: 无法创建 MethodHandle 时仍保留 Field
                    handle = null;
                }
                metaMap.put(fieldName, new FieldMeta(field, handle));
            }
            current = current.getSuperclass();
        }

        return Collections.unmodifiableMap(metaMap);
    }

    /**
     * 判断实例是否应该被包含
     */
    private static boolean shouldIncludeInstance(String currentPath, String targetPath, Set<String> excludedPaths) {
        if (targetPath != null && !targetPath.isEmpty()) {
            return pathMatches(currentPath, targetPath);
        }

        if (excludedPaths != null && !excludedPaths.isEmpty()) {
            for (String excludedPath : excludedPaths) {
                if (pathMatches(currentPath, excludedPath)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean pathMatches(String currentPath, String targetPath) {
        if (targetPath == null || targetPath.isEmpty()) {
            return true;
        }
        if (currentPath == null || currentPath.isEmpty()) {
            return false;
        }
        return PathMatchUtil.match(currentPath, targetPath);
    }

    /**
     * 简单类型判断（带缓存）
     */
    private static boolean isSimpleType(Class<?> clazz) {
        return SIMPLE_TYPE_CACHE.computeIfAbsent(clazz, FieldAccessor::computeIsSimpleType);
    }

    private static boolean computeIsSimpleType(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == String.class
                || Number.class.isAssignableFrom(clazz)
                || clazz == Boolean.class
                || clazz == Character.class
                || clazz.isEnum()
                || Date.class.isAssignableFrom(clazz)
                || clazz.getName().startsWith("java.time");
    }

    // ========================= 内部类 =========================

    /**
     * 字段元数据：封装 Field + MethodHandle
     */
    private static final class FieldMeta {
        final Field field;
        final MethodHandle getter;

        FieldMeta(Field field, MethodHandle getter) {
            this.field = field;
            this.getter = getter;
        }

        Object getValue(Object target) {
            try {
                if (getter != null) {
                    return getter.invoke(target);
                }
                return field.get(target);
            } catch (Throwable e) {
                throw new RuntimeException("Failed to access field: " + field.getName(), e);
            }
        }
    }

    /**
     * DFS 迭代器遍历帧：表示一个待处理的遍历任务
     */
    private static final class TraversalFrame {
        final Object obj;
        final String path;

        TraversalFrame(Object obj, String path) {
            this.obj = obj;
            this.path = path;
        }
    }

    /**
     * 惰性 DFS 迭代器
     * <p>
     * 使用显式栈替代递归，每次调用 {@code next()} 从栈中推进到下一个匹配项。
     * 调用方不调用 {@code next()} 时不会继续遍历，实现真正的惰性求值。
     */
    private static final class TypedInstanceIterator implements Iterator<TypedInstance> {
        private final Class<?> targetType;
        private final String targetPath;
        private final Set<String> excludedPaths;
        private final IdentityHashMap<Object, Boolean> visited;
        private final Deque<TraversalFrame> stack;

        /**
         * 预取的下一个匹配项
         */
        private TypedInstance next;

        TypedInstanceIterator(Object root, Class<?> targetType,
                              String targetPath, Set<String> excludedPaths) {
            this.targetType = targetType;
            this.targetPath = targetPath;
            this.excludedPaths = excludedPaths;
            this.visited = new IdentityHashMap<>();
            this.stack = new ArrayDeque<>();

            if (root != null) {
                stack.push(new TraversalFrame(root, ""));
            }
        }

        @Override
        public boolean hasNext() {
            if (next != null) {
                return true;
            }
            next = advance();
            return next != null;
        }

        @Override
        public TypedInstance next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            TypedInstance result = next;
            next = null;
            return result;
        }

        /**
         * 从栈中推进，找到下一个匹配的 TypedInstance，找不到则返回 null
         */
        private TypedInstance advance() {
            while (!stack.isEmpty()) {
                TraversalFrame frame = stack.pop();
                Object obj = frame.obj;
                String currentPath = frame.path;

                if (obj == null || visited.containsKey(obj)) {
                    continue;
                }

                if (isSimpleType(obj.getClass())) {
                    continue;
                }

                visited.put(obj, Boolean.TRUE);

                // 处理集合
                if (obj instanceof Collection<?>) {
                    pushCollectionElements((Collection<?>) obj, currentPath);
                    continue;
                }

                // 处理 Map
                if (obj instanceof Map<?, ?>) {
                    pushMapEntries((Map<?, ?>) obj, currentPath);
                    continue;
                }

                // 先将子字段压栈（倒序，保证遍历顺序一致）
                pushObjectFields(obj, currentPath);

                // 再检查当前对象是否匹配（DFS 先序：先返回父节点，再遍历子节点）
                if (targetType.isInstance(obj)) {
                    if (shouldIncludeInstance(currentPath, targetPath, excludedPaths)) {
                        return new TypedInstance(obj, currentPath);
                    }
                }
            }
            return null;
        }

        private void pushCollectionElements(Collection<?> collection, String currentPath) {
            // 将集合元素逆序压栈，使得正序弹出
            Object[] items = collection.toArray();
            for (int i = items.length - 1; i >= 0; i--) {
                if (items[i] != null) {
                    String itemPath = currentPath.isEmpty()
                            ? "[" + i + "]"
                            : currentPath + "[" + i + "]";
                    stack.push(new TraversalFrame(items[i], itemPath));
                }
            }
        }

        private void pushMapEntries(Map<?, ?> map, String currentPath) {
            // Map 无序，直接压入
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    String keyPath = currentPath.isEmpty()
                            ? "[" + entry.getKey() + "]"
                            : currentPath + "[" + entry.getKey() + "]";
                    stack.push(new TraversalFrame(value, keyPath));
                }
            }
        }

        private void pushObjectFields(Object obj, String currentPath) {
            Map<String, FieldMeta> metaMap = getFieldMetaMap(obj.getClass());
            for (Map.Entry<String, FieldMeta> entry : metaMap.entrySet()) {
                String fieldName = entry.getKey();
                FieldMeta meta = entry.getValue();
                Object fieldValue = meta.getValue(obj);
                if (fieldValue != null) {
                    String fieldPath = currentPath.isEmpty()
                            ? fieldName
                            : currentPath + "." + fieldName;
                    stack.push(new TraversalFrame(fieldValue, fieldPath));
                }
            }
        }
    }

    /**
     * 类型化实例
     */
    public static final class TypedInstance {
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