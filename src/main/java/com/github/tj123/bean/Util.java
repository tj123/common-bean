package com.github.tj123.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by TJ on 2016/9/20.
 */
class Util {

    private static Log log = LogFactory.getLog(Util.class);

    /**
     * 获取值
     */
    public static <V> String stringValue(V value) {
        String valueOf = String.valueOf(value);
        return "null".equals(valueOf) ? "" : valueOf;
    }

    /**
     * 判断为空
     */
    public static boolean isBlank(String value) {
        return value != null && !value.trim().equals("");
    }

    /**
     * 枚举转换
     */
    public static <E extends Enum<E>, K> E toEnum(Class<E> clazz, K key) throws CannotConvertException {
        return toEnum(clazz, key, "getKey");
    }

    /**
     * 枚举转换
     */
    public static <E extends Enum<E>, K> E toEnum(Class<E> enumClass, K key, String methodName) throws CannotConvertException {
        E[] enums = enumClass.getEnumConstants();
        for (E enm : enums) {
            try {
                Method method = enm.getClass().getMethod(methodName);
                if (method.invoke(enm).equals(key))
                    return enm;
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new CannotConvertException(e);
            }
        }
        throw new CannotConvertException();
    }

    /**
     * 根据名称来转 枚举
     */
    public static <E extends Enum<E>> E enumValue(Class<E> clazz, String name) throws CannotConvertException {
        if (isBlank(name))
            throw new CannotConvertException();
        try {
            return Enum.valueOf(clazz, name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CannotConvertException(e);
        }

    }

    /**
     * 生产 UUID
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /**
     *  日期转换
     */
    public static String dateToString(Date date,String pattern) throws Exception{
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     *  日期转换
     */
    public static Date stringToDate(String date,String pattern) throws Exception{
        return new SimpleDateFormat(pattern).parse(date);
    }

}
