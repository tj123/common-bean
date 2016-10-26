package com.github.tj123.bean;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by TJ on 2016/9/20.
 */
class Util {

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
        return value == null || value.trim().equals("");
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
     * 如果key为null则返回枚举值
     * @param envm
     * @param <E>
     * @return
     * @throws CannotConvertException
     */
    public static <E extends Enum<E>> String getEnumKeyOrValue(E envm) throws CannotConvertException {
        String enumValue = null;
        try {
            enumValue = Util.stringValue(envm.getClass().getMethod("getKey").invoke(envm));
        } catch (Exception e) {
        }
        if (enumValue == null || enumValue.trim().equals(""))
            enumValue = Util.stringValue(envm);
        if (enumValue == null || enumValue.trim().equals(""))
            throw new CannotConvertException(envm + "转换错误！");
        return enumValue;
    }

    /**
     * 调用  getKey 和 valueOf 两种方法来转换
     *
     * @param enumClass
     * @param value
     * @param <E>
     * @param <V>
     * @return
     */
    public static <E extends Enum<E>, V> E toEnumByKeyOrValue(Class<E> enumClass, V value) throws BeanConvertException {
        try {
            return Util.toEnum(enumClass, value);
        } catch (CannotConvertException e) {
            try {
                return Util.enumValue(enumClass, Util.stringValue(value));
            } catch (CannotConvertException e1) {
                throw new BeanConvertException(e1);
            }
        }
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
