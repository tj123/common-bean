package com.github.tj123.bean.validate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 提示字段包裹对象
 */
class Msg {

    /**
     * 字段名称
     */
    private String field;

    /**
     * 字段的值
     */
    private String value;

    /**
     * 注解上 value() 的值
     */
    private String annoValue;

    public String getField() {
        return field;
    }

    public Msg setField(String field) {
        this.field = field;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Msg setValue(String value) {
        this.value = value;
        return this;
    }

    public String getAnnoValue() {
        return annoValue;
    }

    /**
     * 暴力获取 注解 value() 字段值
     * @param annotation
     * @return
     */
    public Msg setAnnoValue(Annotation annotation){
        if (annotation == null) {
            return this;
        }
        Class<? extends Annotation> clazz = annotation.getClass();
        try {
            Method method = clazz.getMethod("value");
            setAnnoValue(String.valueOf(method.invoke(annotation)));
        } catch (Exception e) {
        }
        return this;
    }

    public Msg setAnnoValue(String annoValue) {
        this.annoValue = annoValue;
        return this;
    }

    /**
     * 获取字段的值
     * @param key
     * @return
     */
    public String get(String key){
        try {
            Field field = Msg.class.getDeclaredField(key);
            field.setAccessible(true);
            return String.valueOf(field.get(this));
        } catch (Exception e) {
            return "null";
        }
    }
}
