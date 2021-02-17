package com.github.ulwx.aka.dbutils.tool.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class ObjectUtils {
    private static final Logger log = LoggerFactory.getLogger(ObjectUtils.class);

    public static String toString(Object obj){
        return toJsonString(obj);
    }
    public static boolean isPrimitiveWapper(Class t) {
        if (t == Integer.class || t == Boolean.class || t == Long.class

                || t == Short.class || t == Float.class || t == Double.class || t == Byte.class
                || t == Character.class) {
            return true;
        }
        return false;

    }
    public static Map<String, Object> fromJavaBeanToMap(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(moduleForMapper);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        @SuppressWarnings("unchecked")
        Map<String, Object> fieldMap = mapper.convertValue(obj, Map.class);
        return fieldMap;
    }
    public static Map<String, Object> getMapFromResultSet(ResultSet rs) {

        Map<String, Object> map = new HashMap<String, Object>();
        try {

            ResultSetMetaData rsMeta = rs.getMetaData();

            for (int i = 0; i < rsMeta.getColumnCount(); i++) {
                String columnName = rsMeta.getColumnLabel(i + 1);
               // log.debug("columnName=" + columnName + ",val=" + rs.getObject(columnName));

                map.put(columnName, rs.getObject(columnName));
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("", e);
        }
        return map;

    }
    public static Object[] toObjectArray(Object source) {
        if (source instanceof Object[]) {
            return (Object[]) source;
        }
        if (source == null) {
            return new Object[0];
        }
        if (!source.getClass().isArray()) {
            throw new IllegalArgumentException("Source is not an array: " + source);
        }

        int length = Array.getLength(source);
        if (length == 0) {
            return new Object[0];
        }
        Class wrapperType = Array.get(source, 0).getClass();
        Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
        for (int i = 0; i < length; i++) {
            newArray[i] = Array.get(source, i);
        }
        return newArray;
    }
    public static String toJavascriptString(Object obj) throws Exception {

        String resultStr = "";
        if (obj == null)
            return "{}";
        if (obj.getClass().isPrimitive() || isPrimitiveWapper(obj.getClass())) {
            return resultStr = resultStr + obj;
        } else if (obj.getClass() == String.class) {
            if (((String) obj).startsWith("[javascript]")) {
                return resultStr = StringUtils.trimLeadingString(obj + "", "[javascript]");
            } else {
                return resultStr = resultStr + "\"" + obj + "\"";
            }
        } else if (obj.getClass().isArray()) {
            return ArrayUtils.toJavascriptString(obj);
        } else if (obj instanceof Map) {
            return MapUtils.toJavascriptString((Map) obj);
        } else if (obj instanceof Collection) {
            return CollectionUtils.toJavascriptString((Collection) obj);
        }

        Class c = obj.getClass();

        Field[] fields = c.getDeclaredFields();
        Map<String, Object> fieldsNameValues = new HashMap<String, Object>();
        Map<String, Field> fieldsNameField = new HashMap<String, Field>();
        String[] values = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod))
                continue;

            values[i] = field.getName();
            fieldsNameValues.put(field.getName(), field.get(obj));
            fieldsNameField.put(field.getName(), field);
        }
        try {

            for (int i = 0; i < values.length; i++) {
                String fieldName = values[i];

                Field field = fieldsNameField.get(fieldName);

                Object fvalue = field.get(obj);
                if (fvalue == null) {
                    continue;
                }

                Class<?> fieldType = field.getType();

                // 基本类型
                if (fieldType.isPrimitive() || isPrimitiveWapper(fieldType) || fieldType == String.class) {

                    resultStr = resultStr + ",\"" + fieldName + "\":" + ObjectUtils.toJavascriptString(field.get(obj));

                } else if (fieldType.isArray()) {// 数组

                    resultStr = resultStr + "," + ArrayUtils.toJavascriptString(field.get(obj));

                } else if (fvalue instanceof Map) {
                    resultStr = resultStr + "," + MapUtils.toJavascriptString((Map) field.get(obj));

                } else if (fvalue instanceof Collection) {

                    resultStr = resultStr + "," + CollectionUtils.toJavascriptString((Collection) field.get(obj));
                } else if (fieldType instanceof Object) {
                    resultStr = resultStr + "," + ObjectUtils.toJavascriptString(obj);
                } else {
                    throw new Exception("java bean里的" + fieldName + "属性的类型" + fieldType + "不支持！");
                }

            }
        } catch (Exception e) {
            throw new Exception("seq属性配置不正确或者配置的javabean与二进制流格式不匹配导致解析出错");
        }

        return "{" + StringUtils.trimLeadingString(resultStr, ",") + "}";
    }

    public static String toJsonString(Object obj){
       return  toJsonString(obj,true);
    }
    public static boolean isEmpty(Object[] array) {
        return (array == null || array.length == 0);
    }
    /**
     * 搜索本类或所有父类里指定的属性 ，从本类开始找，再一级级搜索父类
     *
     * @param srcClass         : javabean对象
     * @param fieldName      : 属性名
     * @param fromParentClass 从哪个基类开始查找
     * @return 父类中的属性对象
     */
    public static Field getDeclaredField(Class srcClass, String fieldName, Class fromParentClass) {
        Field field = null;
        Class<?> clazz = srcClass;
        for (; clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
            if (fromParentClass != null && !clazz.isAssignableFrom(fromParentClass)) {
                continue;
            }
            try {
                field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (Exception e) {
            }

        }
        return null;
    }
    public static String toJsonString(Object obj, boolean includeNull) {
        return  toJsonString(obj,includeNull,false,false);
    }
    public static String toPrettyJsonString(Object obj) {

        return  toJsonString(obj,false,false,true);
    }
    public static String toJsonString(Object obj, boolean includeNull, boolean ifNullToDefault,boolean prettyOutPut) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.registerModule(moduleForMapper);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            if(prettyOutPut) {
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
            }
            if (!includeNull) {
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            } else {
                if (ifNullToDefault) {

                    mapper.setSerializerFactory(mapper.getSerializerFactory()
                            .withSerializerModifier(new Converter.MyBeanSerializerModifier()));
                }
            }
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("", e);
        }
        return "";

    }
    public static SimpleModule moduleForMapper = new SimpleModule();
    static {

        moduleForMapper.addSerializer(LocalDate.class, Converter.LocalDateSerializer.instance);
        moduleForMapper.addDeserializer(LocalDate.class, Converter.LocalDateDeSerializer.instance);

        moduleForMapper.addSerializer(LocalDateTime.class, Converter.LocalDateTimeSerializer.instance);
        moduleForMapper.addDeserializer(LocalDateTime.class, Converter.LocalDateTimeDeSerializer.instance);

        moduleForMapper.addSerializer(LocalTime.class, Converter.LocalTimeSerializer.instance);
        moduleForMapper.addDeserializer(LocalTime.class, Converter.LocalTimeDeSerializer.instance);

    }
}


class Converter{


    public static class MyNullArrayJsonSerializer extends JsonSerializer<Object> {

        @Override
        public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {

            if (value == null) {
                jgen.writeStartArray();
                jgen.writeEndArray();
            } else {
                jgen.writeObject(value);
            }
        }
    }

    public static class MyNullStringJsonSerializer extends JsonSerializer<Object> {

        @Override
        public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            if (value == null) {
                jgen.writeString("");
            } else {
                jgen.writeObject(value);
            }
        }
    }

    public static class MyNullObjectJsonSerializer extends JsonSerializer<Object> {

        @Override
        public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            if (value == null) {
                jgen.writeStartObject();
                jgen.writeEndObject();
            } else {
                jgen.writeObject(value);
            }
        }
    }

    public static class MyNullNumberJsonSerializer extends JsonSerializer<Object> {

        @Override
        public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            if (value == null) {
                jgen.writeNumber(0);
            } else {
                jgen.writeObject(value);
            }
        }
    }

    public static class MyNullBooleanJsonSerializer extends JsonSerializer<Object> {

        @Override
        public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            if (value == null) {
                jgen.writeBoolean(false);
            } else {
                jgen.writeObject(value);
            }
        }
    }

    public static class MyNullLocalDateJsonSerializer extends JsonSerializer<LocalDate> {

        @Override
        public void serialize(LocalDate value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            if (value == null) {
                jgen.writeString("0001-01-01");

            } else {
                jgen.writeObject(value);
            }
        }
    }

    public static class MyNullLocalDateTimeJsonSerializer
            extends JsonSerializer<LocalDateTime> {

        @Override
        public void serialize(LocalDateTime value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            if (value == null) {
                jgen.writeString("0001-01-01 00:00:00");

            } else {
                jgen.writeObject(value);
            }
        }
    }

    public static class MyNullLocalTImeJsonSerializer extends JsonSerializer<LocalTime> {

        @Override
        public void serialize(LocalTime value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {
            if (value == null) {
                jgen.writeString("00:00:00");

            } else {
                jgen.writeObject(value);
            }
        }
    }

    public static class MyBeanSerializerModifier extends BeanSerializerModifier {

        public static JsonSerializer<Object> nullArrayJsonSerializer = new MyNullArrayJsonSerializer();
        public static JsonSerializer<Object> nullStringJsonSerializer = new MyNullStringJsonSerializer();
        public static JsonSerializer<Object> nullObjectJsonSerializer = new MyNullObjectJsonSerializer();
        public static JsonSerializer<Object> nullBooleanJsonSerializer = new MyNullBooleanJsonSerializer();
        public static JsonSerializer<Object> nullNumberJsonSerializer = new MyNullNumberJsonSerializer();

        public static JsonSerializer nullLocalDateJsonSerializer = new MyNullLocalDateJsonSerializer();
        public static JsonSerializer nullLocalDateTimeJsonSerializer = new MyNullLocalDateTimeJsonSerializer();
        public static JsonSerializer nullLocalTImeJsonSerializer = new MyNullLocalTImeJsonSerializer();

        @Override
        public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
                                                         List<BeanPropertyWriter> beanProperties) {
            // 循环所有的beanPropertyWriter
            for (int i = 0; i < beanProperties.size(); i++) {
                BeanPropertyWriter writer = beanProperties.get(i);
                // 判断字段的类型，如果是array，list，set则注册nullSerializer
                if (isArrayType(writer)) {

                    writer.assignNullSerializer(nullArrayJsonSerializer);
                } else if (isStringType(writer)) {
                    writer.assignNullSerializer(nullStringJsonSerializer);
                } else if (isNumberType(writer)) {
                    writer.assignNullSerializer(nullNumberJsonSerializer);
                } else if (isBooleanType(writer)) {
                    writer.assignNullSerializer(nullBooleanJsonSerializer);
                } else if (writer.getType().getRawClass() == LocalDate.class) {
                    writer.assignNullSerializer(nullLocalDateJsonSerializer);
                } else if (writer.getType().getRawClass() == LocalDateTime.class) {
                    writer.assignNullSerializer(nullLocalDateTimeJsonSerializer);
                } else if (writer.getType().getRawClass() == LocalTime.class) {
                    writer.assignNullSerializer(nullLocalTImeJsonSerializer);
                } else {
                    writer.assignNullSerializer(nullObjectJsonSerializer);
                }
            }
            return beanProperties;
        }

        // 判断是什么类型
        protected boolean isArrayType(BeanPropertyWriter writer) {
            Class<?> clazz = writer.getType().getRawClass();
            return clazz.isArray() || clazz.equals(List.class) || clazz.equals(Set.class);

        }

        protected boolean isStringType(BeanPropertyWriter writer) {
            Class<?> clazz = writer.getType().getRawClass();
            return clazz == String.class || clazz == Character.class || clazz == char.class;

        }

        protected boolean isNumberType(BeanPropertyWriter writer) {
            Class<?> t = writer.getType().getRawClass();

            return (t == Integer.class || t == int.class || t == Long.class || t == long.class || t == Short.class
                    || t == short.class || t == Float.class || t == float.class || t == Double.class
                    || t == double.class || t == java.math.BigDecimal.class || t == java.math.BigInteger.class);
        }

        protected boolean isObjectType(BeanPropertyWriter writer) {
            Class<?> clazz = writer.getType().getRawClass();
            return Object.class.isAssignableFrom(clazz);

        }

        protected boolean isBooleanType(BeanPropertyWriter writer) {
            Class<?> clazz = writer.getType().getRawClass();
            return clazz == Boolean.class || clazz == boolean.class;

        }

    }

    public static class LocalDateSerializer extends JsonSerializer<LocalDate> {

        public static LocalDateSerializer instance = new LocalDateSerializer();

        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {
            if (value != null) {
                gen.writeString(value.toString());
            }

        }

    }

    public static class LocalDateDeSerializer extends JsonDeserializer<LocalDate> {
        public static LocalDateDeSerializer instance = new LocalDateDeSerializer();

        @Override
        public LocalDate deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {

            LocalDate date;
            try {
                if (StringUtils.hasText(p.getText())) {
                    date = LocalDate.parse(p.getText());
                    return date;
                } else {
                    return null;
                }
            } catch (Exception e) {
                throw e;
            }

        }
    }

    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

        public static LocalDateTimeSerializer instance = new LocalDateTimeSerializer();

        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {
            if (value != null) {
                gen.writeString(value.format(CTime.DTF_YMD_HH_MM_SS));
            }

        }

    }

    public static class LocalDateTimeDeSerializer
            extends JsonDeserializer<LocalDateTime> {
        public static LocalDateTimeDeSerializer instance = new LocalDateTimeDeSerializer();

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {

            LocalDateTime date;
            try {
                if (StringUtils.hasText(p.getText())) {
                    date = LocalDateTime.parse(p.getText(), CTime.DTF_YMD_HH_MM_SS);
                    return date;
                } else {
                    return null;
                }
            } catch (Exception e) {
                throw e;
            }

        }

    }
    public static class LocalTimeSerializer extends JsonSerializer<LocalTime> {

        public static LocalTimeSerializer instance = new LocalTimeSerializer();

        @Override
        public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {
            if (value != null) {
                gen.writeString(value.toString());
            } else {
                gen.writeString("00:00:00");
            }

        }

    }

    public static class LocalTimeDeSerializer extends JsonDeserializer<LocalTime> {
        public static LocalTimeDeSerializer instance = new LocalTimeDeSerializer();

        @Override
        public LocalTime deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {

            LocalTime date;
            try {
                if (StringUtils.hasText(p.getText())) {
                    date = LocalTime.parse(p.getText());
                    return date;
                } else {
                    return null;
                }
            } catch (Exception e) {
                throw e;
            }

        }

    }


}