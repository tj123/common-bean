package com.shundian.common.v7.bean;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
     * @param <B>
     * @param <K>
     * @param <V>
     * @return
     * @throws BeanConvertException
     */
    public static <B, K, V> B toBean(Map<K, V> map, Class<B> clazz) throws BeanConvertException {
        try {
            B bean = clazz.newInstance();
            Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<K, V> element = iterator.next();
                String key = String.valueOf(element.getKey());
                V value = element.getValue();

            }
            for (Method method : clazz.getMethods()) {
                String methodName = method.getName();
                if (!methodName.startsWith("set")) continue;
            }
            return bean;
        } catch (Exception e) {
            throw new BeanConvertException(e);
        }
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
        Map map = new HashMap();
        Class<?> clazz = bean.getClass();
        for (Method method : clazz.getMethods()) {
            String methodName = method.getName();
            if (methodName.startsWith("get")) {
                String key = methodName.substring(3);
                key = key.substring(0, 1).toLowerCase() + key.substring(1);
                if (Util.isBlank(key) || "class".equals(key))
                    continue;
                try {
                    Object value = method.invoke(bean);
                    if (includeNull) {
                        map.put(key, value);
                    } else {
                        if (value != null) {
                            map.put(key, value);
                        }
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new BeanConvertException(e);
                }
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
     * bean 之间转换 根据方法名转换
     *
     * @param origin      原始
     * @param targetClass 目标
     * @param <O>
     * @param <T>
     * @return
     * @throws BeanConvertException
     */
    public static <O, T> T convert(O origin, Class<T> targetClass) throws BeanConvertException {
        try {
            T target = targetClass.newInstance();
            Class<?> originClass = origin.getClass();
            for (Method targetSetMethod : targetClass.getMethods()) {
                String targetMethodName = targetSetMethod.getName();
                if (!targetMethodName.startsWith("set")) continue;
                String fieldName = getField(targetMethodName);
//                Class<?> targetFieldClass = targetSetMethod.getParameterTypes()[0];
                Field targetField = targetClass.getDeclaredField(fieldName);
                Class<?> targetFieldClass = targetField.getType();
                Method originGetMethod = originClass.getMethod(getGetterName(fieldName));
                Class<?> originFieldClass = originGetMethod.getReturnType();
                Object originValue = originGetMethod.invoke(origin);

                if(originValue == null) continue;
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

                    }
                }
                if (isSuperClass(Date.class, targetClass)) {

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
     * @param interfaceClass
     * @return
     */
    public static boolean isInterfaceOf(Class<?> interfaceClass, Class<?> originClass) {
        return isSuperClass(interfaceClass, originClass);
    }

    /**
     * 判断是否为子类
     *
     * @param subClass
     * @param supperClass
     * @return
     */
    public static boolean isSubClass(Class<?> subClass, Class<?> supperClass) {
        return !isSuperClass(subClass, supperClass);
    }


}
