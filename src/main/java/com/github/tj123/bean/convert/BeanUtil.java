package com.github.tj123.bean.convert;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by TJ on 2016/9/3.
 */
public class BeanUtil {

    private static Log log = LogFactory.getLog(BeanUtil.class);

    /**
     * 生产 UUID
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /**
     * map 转为 bean
     *
     * @param map
     * @param clazz
     * @param allFiled 转换所有字段
     *                 true: 当有错误时字段为空
     *                 false： 当有错误即抛出异常
     * @param <B>
     * @return
     * @throws BeanConvertException
     */
    public static <B> B toBean(Map<String, Object> map, Class<B> clazz, boolean allFiled) throws BeanConvertException {
        try {
            B target = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field targetField : fields) {
                String targetFieldName = targetField.getName();
                if ("serialVersionUID".equals(targetFieldName)) continue;
                try {
                    ConvertibleField ogn = new ConvertibleField(map, targetField.getName());
                    ConvertibleField tgt = new ConvertibleField(target, null, targetField);
                    tgt.copyValueFrom(ogn);
                } catch (Exception e) {
                    if (!allFiled) {
                        throw new BeanConvertException(e);
                    }
                    if (log.isDebugEnabled()) {
                        //log.debug(e.getMessage(), e);
                        log.debug(e.getMessage());
                    }
                }
            }
            return target;
        } catch (InstantiationException | IllegalAccessException e) {
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
    public static <B> Map<String, Object> toMap(B bean) throws BeanConvertException {
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
    public static <B> Map<String, Object> toMap(B bean, boolean includeNull) throws BeanConvertException {
        Map<String, Object> map = new HashMap<>();
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
                    log.debug(e.getMessage(), e);
                }
            }
        }
        return bean;
    }

    /**
     * 对所有字段 trim()
     *
     * @param bean
     * @param <B>
     * @return
     */
    public static <B> B trim(B bean) {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(bean);
                if (String.class.equals(field.getType()) && fieldValue != null) {
                    field.set(bean, String.valueOf(fieldValue).trim());
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                if (log.isDebugEnabled()) {
                    log.debug(e.getMessage(), e);
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
                try {
                    Field originField = originClass.getDeclaredField(targetField.getName());
                    originField.setAccessible(true);
                    Object originValue = originField.get(origin);
                    ConvertibleField ogn = new ConvertibleField(origin, originValue, originField);
                    ConvertibleField tgt = new ConvertibleField(target, null, targetField);
                    try {
                        tgt.copyValueFrom(ogn);
                    } catch (Exception e) {
                        if (!allFiled) {
                            throw new BeanConvertException(e);
                        }
                        if (log.isDebugEnabled()) {
                            log.debug(e.getMessage());
                        }
                    }
                } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
                    if (!allFiled) {
                        throw new BeanConvertException(e);
                    }
                    if (log.isDebugEnabled()) {
                        log.debug(e.getMessage());
                    }
                }
            }
            return target;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BeanConvertException(e);
        }
    }


}
