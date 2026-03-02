package com.github.ulwx.aka.dbutils.tool.support;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.*;

/**
 * 对象打印工具类
 * 能够打印任何对象的所有字段值
 */
public class ObjectPrinter {

    /**
     * 打印对象的所有字段
     */
    public static String print(Object obj) {
        return print(obj, false);
    }

    /**
     * 打印对象的所有字段
     * @param obj 要打印的对象
     * @param includeNulls 是否包含null值字段
     */
    public static String print(Object obj, boolean includeNulls) {
        if (obj == null) return "null";

        try {
            StringBuilder sb = new StringBuilder();
            Class<?> clazz = obj.getClass();
            sb.append(clazz.getSimpleName()).append("{");

            List<Field> fields = getAllFields(clazz);
            boolean first = true;

            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) continue;

                field.setAccessible(true);
                Object value = field.get(obj);

                if (!includeNulls && value == null) continue;

                if (!first) sb.append(", ");
                first = false;

                sb.append(field.getName()).append("=");

                if (value == null) {
                    sb.append("null");
                } else if (isSimpleType(value.getClass())) {
                    formatSimpleValue(sb, value);
                } else {
                    sb.append(value.toString());
                }
            }

            sb.append("}");
            return sb.toString();

        } catch (Exception e) {
            return obj.getClass().getSimpleName() + "@" + System.identityHashCode(obj) + "[打印异常]" + e.getMessage();
        }
    }

    /**
     * 打印所有字段（包括null值）
     */
    public static String printAll(Object obj) {
        return print(obj, true);
    }

    /**
     * 打印到控制台
     */
    public static void printToConsole(Object obj) {
        System.out.println(print(obj));
    }

    /**
     * 打印列表
     */
    public static String printList(List<?> list) {
        if (list == null) return "null";

        StringBuilder sb = new StringBuilder("List[");
        for (int i = 0; i < Math.min(list.size(), 10); i++) {
            if (i > 0) sb.append(", ");
            sb.append(print(list.get(i)));
        }
        if (list.size() > 10) sb.append(", ...(size=" + list.size() + ")");
        sb.append("]");
        return sb.toString();
    }

    /**
     * 打印Map
     */
    public static String printMap(Map<?, ?> map) {
        if (map == null) return "null";

        StringBuilder sb = new StringBuilder("Map{");
        int count = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (count >= 10) break;
            if (count > 0) sb.append(", ");
            sb.append(print(entry.getKey())).append("=").append(print(entry.getValue()));
            count++;
        }
        if (map.size() > 10) sb.append(", ...(size=" + map.size() + ")");
        sb.append("}");
        return sb.toString();
    }

    /**
     * 判断是否为简单类型
     */
    private static boolean isSimpleType(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == String.class ||
                clazz == Boolean.class ||
                clazz == Character.class ||
                Number.class.isAssignableFrom(clazz) ||
                clazz == java.util.Date.class ||
                clazz == java.sql.Date.class ||
                clazz == java.sql.Timestamp.class ||
                clazz == java.time.LocalDate.class ||
                clazz == java.time.LocalDateTime.class ||
                clazz == java.time.LocalTime.class;
    }

    /**
     * 格式化简单类型的值
     */
    private static void formatSimpleValue(StringBuilder sb, Object value) {
        if (value instanceof String) {
            sb.append("\"").append(value).append("\"");
        } else if (value instanceof Character) {
            sb.append("'").append(value).append("'");
        } else {
            sb.append(value);
        }
    }

    /**
     * 获取类的所有字段
     */
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            Collections.addAll(fields, current.getDeclaredFields());
            current = current.getSuperclass();
        }
        return fields;
    }

    public static void main(String[] args) {


    }


}