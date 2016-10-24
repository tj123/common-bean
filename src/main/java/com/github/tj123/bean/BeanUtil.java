package com.github.tj123.bean;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TJ on 2016/9/3.
 */
public class BeanUtil {

    private static Log log = LogFactory.getLog(BeanUtil.class);

    /**
     * map 转为 bean
     *
     * @param map
     * @param clazz
     * @param allFiled 转换所有字段
     *                 true: 当有错误时字段为空
     *                 false： 当有错误即抛出异常
     * @param <B>
     * @param <K>
     * @param <V>
     * @return
     * @throws BeanConvertException
     */
    public static <B, K, V> B toBean(Map<K, V> map, Class<B> clazz, boolean allFiled) throws BeanConvertException {
        try {
            B target = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field targetField : fields) {
                String targetFieldName = targetField.getName();
                if ("serialVersionUID".equals(targetFieldName)) continue;
                filedMap(map.get(targetFieldName), targetField, target);
            }
            return target;
        } catch (Exception e) {
            throw new BeanConvertException(e);
        }
    }


    /**
     * 对象转为 map 默认包含空字段
     *
     * @param bean 对象
     * @param <B>
     * @return
     * @throws BeanConvertException
     */
    public static <B> Map toMap(B bean) throws BeanConvertException {
        return toMap(bean, true);
    }

    /**
     * 对象转为 map
     *
     * @param bean        对象
     * @param includeNull 是否包含空字段
     * @param <B>
     * @return
     * @throws BeanConvertException
     */
    public static <B> Map toMap(B bean, boolean includeNull) throws BeanConvertException {
        Map<String, Object> map = new HashMap();
        Class<?> clazz = bean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals("serialVersionUID"))
                continue;
            field.setAccessible(true);
            try {
                Object value = field.get(bean);
                if (includeNull && value == null) {
                    continue;
                }
                map.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                throw new BeanConvertException(e);
            }
        }
        return map;
    }

    /**
     * bean 字段为 null 时 填充为 “”
     *
     * @param bean
     * @param <B>
     * @return
     */
    public static <B> B fillNull(B bean) {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (String.class.equals(field.getType()) && field.get(bean) == null) {
                    field.set(bean, "");
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                if (log.isDebugEnabled()) {
                    log.debug(e);
                }
            }
        }
        return bean;
    }

    /**
     * bean 之间转换 根据方法名转换 默认转换所有字段
     *
     * @param origin      原始
     * @param targetClass 目标
     * @param <O>
     * @param <T>
     * @return
     * @throws BeanConvertException
     */
    public static <O, T> T convert(O origin, Class<T> targetClass) throws BeanConvertException {
        return convert(origin, targetClass, true);
    }

    /**
     * bean 之间转换 根据方法名转换
     *
     * @param origin      原始
     * @param targetClass 目标
     * @param allFiled    转换所有字段
     *                    true: 当有错误时字段为空
     *                    false： 当有错误即抛出异常
     * @param <O>
     * @param <T>
     * @return
     * @throws BeanConvertException
     */
    public static <O, T> T convert(O origin, Class<T> targetClass, boolean allFiled) throws BeanConvertException {
        try {
            T target = targetClass.newInstance();
            Class<?> originClass = origin.getClass();
            Field[] fields = targetClass.getDeclaredFields();
            for (Field targetField : fields) {
                if ("serialVersionUID".equals(targetField.getName())) continue;
                Field originField = originClass.getDeclaredField(targetField.getName());
                filedMap(originField, origin, targetField, target);
            }
            return target;
        } catch (Exception e) {
            throw new BeanConvertException(e);
        }
    }


    /**
     * 判断是否为父类
     *
     * @param supperClass
     * @param subClass
     * @return
     */
    public static boolean isSuperClass(Class<?> supperClass, Class<?> subClass) {
        return supperClass.isAssignableFrom(subClass);
    }

    /**
     * 判断是否完成了接口
     *
     * @param originClass
     * @param interfaceClass 接口
     * @return
     */
    public static boolean isInterfaceOf(Class<?> interfaceClass, Class<?> originClass) {
        return isSuperClass(interfaceClass, originClass);
    }

    /**
     * 判断是否为子类
     *
     * @param subClass    子类
     * @param supperClass 父类
     * @return
     */
    public static boolean isSubClass(Class<?> subClass, Class<?> supperClass) {
        return !isSuperClass(subClass, supperClass);
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
    public static <E extends Enum<E>, V> E toEnum(Class<E> enumClass, V value) throws BeanConvertException {
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
     * 默认在枚举出错时不报错
     *
     * @param originValue
     * @param targetField
     * @param target
     * @param <O>
     * @param <T>
     * @throws Exception
     */
    public static <O, T> void filedMap(O originValue, Field targetField, T target) throws Exception {
        filedMap(originValue, targetField, target, false);
    }

    /**
     * map 转 bean 时用到
     *
     * @param originValue 原始值
     * @param targetField 目标对象的 字段
     * @param target      目标对象
     * @param validEnum   是否在枚举出错时报异常
     * @param <O>
     * @param <T>
     * @throws Exception
     */
    public static <O, T> void filedMap(O originValue, Field targetField, T target, boolean validEnum) throws Exception {
        targetField.setAccessible(true);
        Class<?> originFieldClass = originValue.getClass();
        Class<?> targetFieldClass = targetField.getType();
        if (originValue == null) return;
        //为 String
        if (String.class.equals(targetFieldClass)) {
            if (!isSuperClass(Date.class, originFieldClass)) {
                targetField.set(target, Util.stringValue(originValue));
            } else {
                DatePattern datePatternAnnotation = targetField.getAnnotation(DatePattern.class);
                String datePattern = BeanConfig.DEFAULT_DATE_PATTEN;
                if (datePatternAnnotation != null) {
                    datePattern = datePatternAnnotation.value();
                }
                targetField.set(target, Util.dateToString((Date) originValue, datePattern));
            }
        } else if (isSuperClass(Date.class, targetFieldClass)) {
            // 为 Date 类型或者 Date的子类
            String datePattern = BeanConfig.DEFAULT_DATE_PATTEN;
            if (Date.class.equals(targetFieldClass)) {
                targetField.set(target, Util.stringToDate(Util.stringValue(originValue), datePattern));
            } else if (isInterfaceOf(DateConvert.class, targetFieldClass)) {
                DateConvert targetValue = (DateConvert) targetFieldClass.newInstance();
                targetValue.setDate(Util.stringToDate(Util.stringValue(originValue), datePattern));
                targetField.set(target, targetValue);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("必须实现 DateConvert 接口,以完成转换：", new BeanConvertException());
                }
            }
        } else if (targetFieldClass.isEnum()) {
            //为 枚举
            try {
                Enum value = toEnum((Class<Enum>) targetFieldClass, originValue);
                targetField.set(target, value);
            } catch (Exception e) {
                if (validEnum)
                    throw e;
            }
        } else if (Double.class.equals(targetFieldClass)) {
            //为 Double
            targetField.set(target, Double.valueOf(Util.stringValue(originValue)));
        } else if (Integer.class.equals(targetFieldClass)) {
            //为 Integer
            targetField.set(target, Integer.valueOf(Util.stringValue(originValue)));
        } else if (Float.class.equals(targetFieldClass)) {
            //为 Float
            targetField.set(target, Float.valueOf(Util.stringValue(originValue)));
        } else if (Short.class.equals(targetFieldClass)) {
            //为 Short
            targetField.set(target, Short.valueOf(Util.stringValue(originValue)));
        } else if (Long.class.equals(targetFieldClass)) {
            //为 Long
            targetField.set(target, Long.valueOf(Util.stringValue(originValue)));
        }
    }

    public static <O, T> void filedMap(Field originField, O origin, Field targetField, T target) throws Exception {
        filedMap(originField, origin, targetField, target, false, false);
    }

    /**
     * field 之间的映射转换
     *
     * @param originField
     * @param origin
     * @param targetField
     * @param target
     * @param validEnum   当枚举值不正确时是否报异常
     * @param validDate   日期不正确时是否报异常
     * @param <O>
     * @param <T>
     */
    public static <O, T> void filedMap(Field originField, O origin, Field targetField, T target, boolean validEnum, boolean validDate) throws Exception {
        originField.setAccessible(true);
        targetField.setAccessible(true);
        Object originValue = originField.get(origin);
        Class<?> originFieldClass = originField.getType();
        Class<?> targetFieldClass = targetField.getType();
        if (originValue == null) return;
        //为 String
        if (String.class.equals(targetFieldClass)) {
            if (originFieldClass.isEnum()) {
                String enumValue = null;
                try {
                    enumValue = Util.stringValue(originFieldClass.getMethod("getKey").invoke(originValue));
                } catch (Exception e) {
                }
                if (enumValue == null || enumValue.trim().equals(""))
                    enumValue = Util.stringValue(originValue);
                targetField.set(target, enumValue);
            } else if (!isSuperClass(Date.class, originFieldClass)) {
                targetField.set(target, Util.stringValue(originValue));
            } else {
                DatePattern datePatternAnnotation = targetField.getAnnotation(DatePattern.class);
                String datePattern = BeanConfig.DEFAULT_DATE_PATTEN;
                if (datePatternAnnotation != null) {
                    datePattern = datePatternAnnotation.value();
                }
                targetField.set(target, Util.dateToString((Date) originValue, datePattern));
            }
        } else if (isSuperClass(Date.class, targetFieldClass)) {
            // 为 Date 类型或者 Date的子类
            DatePattern datePatternAnnotation = originField.getAnnotation(DatePattern.class);
            String datePattern = BeanConfig.DEFAULT_DATE_PATTEN;
            if (datePatternAnnotation != null) {
                datePattern = datePatternAnnotation.value();
            }
            if (Date.class.equals(targetFieldClass)) {
                String date = null;
                try {
                    date = Util.stringValue(originValue);
                    targetField.set(target, Util.stringToDate(date, datePattern));
                } catch (Exception e) {
                    if (validDate) {
                        throw e;
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("日期：" + date + " 不符合格式：" + datePattern);
                        }
                    }

                }
            } else if (isInterfaceOf(DateConvert.class, targetFieldClass)) {
                DateConvert targetValue = (DateConvert) targetFieldClass.newInstance();
                targetValue.setDate(Util.stringToDate(Util.stringValue(originValue), datePattern));
                targetField.set(target, targetValue);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("必须实现 DateConvert 接口,以完成转换：", new BeanConvertException());
                }
            }
        } else if (targetFieldClass.isEnum()) {
            //为 枚举
            try {
                Enum value = toEnum((Class<Enum>) targetFieldClass, originValue);
                targetField.set(target, value);
            } catch (Exception e) {
                if (validEnum) {
                    throw e;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("枚举值：" + originValue + " 不符合要求");
                    }
                }
            }
        } else if (Double.class.equals(targetFieldClass)) {
            //为 Double
            targetField.set(target, Double.valueOf(Util.stringValue(originValue)));
        } else if (Integer.class.equals(targetFieldClass)) {
            //为 Integer
            targetField.set(target, Integer.valueOf(Util.stringValue(originValue)));
        } else if (Float.class.equals(targetFieldClass)) {
            //为 Float
            targetField.set(target, Float.valueOf(Util.stringValue(originValue)));
        } else if (Short.class.equals(targetFieldClass)) {
            //为 Short
            targetField.set(target, Short.valueOf(Util.stringValue(originValue)));
        } else if (Long.class.equals(targetFieldClass)) {
            //为 Long
            targetField.set(target, Long.valueOf(Util.stringValue(originValue)));
        }
    }


}
