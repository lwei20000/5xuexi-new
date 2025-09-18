package com.struggle.common.core.utils;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * JSON解析工具类
 *
 * @author EleAdmin
 * @since 2017-06-10 10:10:39
 */
public class JSONUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();

    /**
     * 对象转json字符串
     *
     * @param value 对象
     * @return String
     */
    public static String toJSONString(Object value) {
        return toJSONString(value, false);
    }


    /**
     * 获取 ObjectMapper 对象
     *
     */
    public static ObjectMapper getObjectMapper() {

        return objectMapper;
    }

    /**
     * 对象转json字符串
     *
     * @param value  对象
     * @param pretty 是否格式化输出
     * @return String
     */
    public static String toJSONString(Object value, boolean pretty) {
        // 设置全局日期格式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        // 设置时区（例如上海时区）
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        if (value != null) {
            if (value instanceof String) {
                return (String) value;
            }
            try {
                if (pretty) {
                    return objectWriter.writeValueAsString(value);
                }
                return objectMapper.writeValueAsString(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * json字符串转对象
     *
     * @param json  String
     * @param clazz Class
     * @return T
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        if (StrUtil.isNotBlank(json) && clazz != null) {
            try {
                return objectMapper.readValue(json, clazz);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    /**
     * json字符串转List对象
     *
     * @param json  String
     * @param object Class
     * @return T
     */
    public static <T> List<T> stringToList(String json, Class<T> object){
        try {
            CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, object);
            List<T> list = objectMapper.readValue(json, listType);
            return list;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
