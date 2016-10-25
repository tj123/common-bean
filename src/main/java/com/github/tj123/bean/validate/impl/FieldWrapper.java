package com.github.tj123.bean.validate.impl;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 注解的工具类
 */
public class FieldWrapper {

    public FieldWrapper(Field field, Object fieldValue) {
        this.field = field;
        this.fieldValue = fieldValue;
        this.fieldStringValue = String.valueOf(fieldValue);
    }

    private Field field;
    private Object fieldValue;
    private String fieldStringValue;
    private List<AnnotationWrapper> annotationWrappers;

    public void setAnnotationWrappers(List<AnnotationWrapper> annotationWrappers) {
        this.annotationWrappers = annotationWrappers;
    }

    public List<AnnotationWrapper> getAnnotationWrappers() {
        return annotationWrappers;
    }

    /**
     * 判断字段是否为空
     *
     * @return
     */
    public boolean fieldIsNotBlank() {
        return fieldStringValue != null && !"null".equals(fieldStringValue)
                && !fieldStringValue.trim().equals("");
    }

    public String getFieldStringValue() {
        return fieldStringValue;
    }

    public String getErrorMessage(){
        return null;
    }
}
