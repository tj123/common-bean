package com.github.tj123.bean.validate.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by TJ on 2016/10/25.
 */
public class AnnotationWrapper {

    public AnnotationWrapper(Annotation annotation){
        this.annotation = annotation;
    }

    private Annotation annotation;

    /**
     * 判断是否存在
     * @return
     */
    public boolean isExist(){
        return annotation != null;
    }

    /**
     * 判断是否存在
     * @return
     */
    public boolean isNotExist(){
        return !isExist();
    }

    /**
     * 获取注解的 value();
     *
     * @return
     */
    public String value() {
        Class<? extends Annotation> clazz = annotation.getClass();
        try {
            Method method = clazz.getMethod("value");
            Object invoke = method.invoke(annotation);
            if (invoke == null) {
                return null;
            }
            return String.valueOf(invoke);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取注解的 message();
     *
     * @return
     */
    public String message() {
        Class<? extends Annotation> clazz = annotation.getClass();
        try {
            Method method = clazz.getMethod("message");
            Object invoke = method.invoke(annotation);
            if (invoke == null) {
                return null;
            }
            return String.valueOf(invoke);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 是否有 value()
     * @return
     */
    public boolean hasValue(){
        return value() != null;
    }

    /**
     * 是否有 message()
     * @return
     */
    public boolean hasMessage(){
        return value() != null;
    }
}
