package com.github.tj123.bean;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
     * @param allFiled    转换所有字段
     *                    true: 当有错误时字段为空
     *                    false： 当有错误即抛出异常
     * @param <B>
     * @param <K>
     * @param <V>
     * @return
     * @throws BeanConvertException
     */
    public static <B, K, V> B toBean(Map<K, V> map, Class<B> clazz,boolean allFiled) throws BeanConvertException {
        try {
            B target = clazz.newInstance();
            for (K key : map.keySet()) {
                ((String)key).toUpperCase();
            }
            for (Method setMethod : clazz.getMethods()) {
                try {
                    String setMethodName = setMethod.getName();
                    if (!setMethodName.startsWith("set")) continue;
                    String fieldName = getField(setMethodName);
                    Field field = clazz.getDeclaredField(fieldName);
                    Class<?> fieldClass = field.getType();
                }catch (Exception e){
                    if (!allFiled) {
                        throw e;
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug(setMethod + "错误：", e);
                        }
                    }
                }

            }
            return target;
        } catch (Exception e) {
            throw new BeanConvertException(e);
        }
    }


    /**
     * 对象转为 map 默认包含空字段
     *
     * @param bean        对象
     * @param <B>
     * @return
     * @throws BeanConvertException
     */
    public static <B> Map toMap(B bean) throws BeanConvertException {
        return  toMap(bean,true);
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
        Map<String,Object> map = new HashMap();
        Class<?> clazz = bean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if(field.getName().equals("serialVersionUID"))
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
//        for (Method method : clazz.getMethods()) {
//            String methodName = method.getName();
//            if (methodName.startsWith("get")) {
//                String key = methodName.substring(3);
//                key = key.substring(0, 1).toLowerCase() + key.substring(1);
//                if (Util.isBlank(key) || "class".equals(key))
//                    continue;
//                try {
//                    Object value = method.invoke(bean);
//                    if (includeNull) {
//                        map.put(key, value);
//                    } else {
//                        if (value != null) {
//                            map.put(key, value);
//                        }
//                    }
//                } catch (IllegalAccessException | InvocationTargetException e) {
//                    throw new BeanConvertException(e);
//                }
//            }
//        }
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
                if (field.get(bean) == null && String.class.equals(field.getType())) {
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
    @SuppressWarnings("unchecked")
    public static <O, T> T convert(O origin, Class<T> targetClass, boolean allFiled) throws BeanConvertException {
        try {
            T target = targetClass.newInstance();
            Class<?> originClass = origin.getClass();
            for (Method targetSetMethod : targetClass.getMethods()) {
                try {
                    String targetMethodName = targetSetMethod.getName();
                    if (!targetMethodName.startsWith("set")) continue;
                    String fieldName = getField(targetMethodName);
                    //                Class<?> targetFieldClass = targetSetMethod.getParameterTypes()[0];
                    Field targetField = targetClass.getDeclaredField(fieldName);
                    Class<?> targetFieldClass = targetField.getType();
                    Method originGetMethod = originClass.getMethod(getGetterName(fieldName));
                    Class<?> originFieldClass = originGetMethod.getReturnType();
                    Field originField = originClass.getDeclaredField(fieldName);
                    Object originValue = originGetMethod.invoke(origin);
                    if (originValue == null) continue;
                    //为 String
                    if (String.class.equals(targetFieldClass)) {
                        if (!isSuperClass(Date.class, originFieldClass)) {
                            targetSetMethod.invoke(target, Util.stringValue(originValue));
                        } else {
                            DatePattern datePatternAnnotation = targetField.getAnnotation(DatePattern.class);
                            String datePattern = BeanConfig.DEFAULT_DATE_PATTEN;
                            if (datePatternAnnotation != null) {
                                datePattern = datePatternAnnotation.value();
                            }
                            targetSetMethod.invoke(target, Util.dateToString((Date) originValue, datePattern));
                        }
                    } else if (isSuperClass(Date.class, targetFieldClass)) {
                        // 为 Date 类型或者 Date的子类
                        DatePattern datePatternAnnotation = originField.getAnnotation(DatePattern.class);
                        String datePattern = BeanConfig.DEFAULT_DATE_PATTEN;
                        if (datePatternAnnotation != null) {
                            datePattern = datePatternAnnotation.value();
                        }
                        if (Date.class.equals(targetFieldClass)) {
                            targetSetMethod.invoke(target, Util.stringToDate(Util.stringValue(originValue), datePattern));
                        } else if (isInterfaceOf(DateConvert.class, targetFieldClass)) {
                            DateConvert targetValue = (DateConvert) targetFieldClass.newInstance();
                            targetValue.setDate(Util.stringToDate(Util.stringValue(originValue), datePattern));
                            targetSetMethod.invoke(target, targetValue);
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("必须实现 DateConvert 接口,以完成转换：", new BeanConvertException());
                            }
                        }
                    } else if (targetFieldClass.isEnum()) {
                        //为 枚举
                        //targetSetMethod.invoke(target, toEnum(targetFieldClass, originValue));
                    } else if (Double.class.equals(targetFieldClass)) {
                        //为 Double
                        targetSetMethod.invoke(target, Double.valueOf(Util.stringValue(originValue)));
                    } else if (Integer.class.equals(targetFieldClass)) {
                        //为 Integer
                        targetSetMethod.invoke(target, Integer.valueOf(Util.stringValue(originValue)));
                    } else if (Float.class.equals(targetFieldClass)) {
                        //为 Float
                        targetSetMethod.invoke(target, Float.valueOf(Util.stringValue(originValue)));
                    } else if (Short.class.equals(targetFieldClass)) {
                        //为 Short
                        targetSetMethod.invoke(target, Short.valueOf(Util.stringValue(originValue)));
                    } else if (Long.class.equals(targetFieldClass)) {
                        //为 Long
                        targetSetMethod.invoke(target, Long.valueOf(Util.stringValue(originValue)));
                    }
                } catch (Exception e) {
                    if (!allFiled) {
                        throw e;
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug(targetSetMethod + "错误：", e);
                        }
                    }
                }
            }
            return target;
        } catch (Exception e) {
            throw new BeanConvertException(e);
        }
    }

    /**
     * 根据 方法名 得出 字段名称 :
     * 方法名必须为 getter 或者 setter
     *
     * @param methodName
     * @return
     */
    public static String getField(String methodName) {
        String field = methodName.substring(3);
        return Character.isUpperCase(field.charAt(1)) ? field :
                field.substring(0, 1).toLowerCase() + field.substring(1);
    }

    /**
     * 根据 字段 获取 set 方法
     *
     * @param fieldName
     * @return
     */
    public static String getSetterName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }


    /**
     * 根据 字段 获取 get 方法
     *
     * @param fieldName
     * @return
     */
    public static String getGetterName(String fieldName) {
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
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


}
