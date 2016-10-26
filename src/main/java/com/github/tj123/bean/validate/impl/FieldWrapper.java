package com.github.tj123.bean.validate.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字段的包装类
 */
public class FieldWrapper {

    private Log log = LogFactory.getLog(FieldWrapper.class);

    public FieldWrapper() {
    }

    public FieldWrapper(Field field, Object fieldValue) {
        setField(field, fieldValue);
    }

    private Field field;
    private Object fieldValue;
    private String fieldStringValue;
    private List<AnnotationWrapper> annotationWrappers;
    private Map<String, String> message = new HashMap<>();

    public void setField(Field field, Object fieldValue) {
        this.field = field;
        this.fieldValue = fieldValue;
        this.fieldStringValue = String.valueOf(fieldValue);
        message.put("field", field.getName());
        message.put("value", getFieldStringValue());
        if (annotationWrappers != null) {
            annotationWrappers.clear();
        }
    }

    /**
     * 设置此字段的注解
     *
     * @param classes
     */
    public void setAnnotations(@SuppressWarnings("unchecked") Class<? extends Annotation>... classes) {
        if(field == null){
            if(log.isErrorEnabled()){
                log.error("必须先调用setField()",new Exception("必须先调用setField()"));
            }
        }
        if (annotationWrappers == null) {
            annotationWrappers = new ArrayList<>();
        }
        for (Class<? extends Annotation> clazz : classes) {
            Annotation annotation = field.getAnnotation(clazz);
            annotationWrappers.add(new AnnotationWrapper(annotation, clazz));
        }
    }

    public void setAnnotationWrappers(List<AnnotationWrapper> annotationWrappers) {
        this.annotationWrappers = annotationWrappers;
    }

    public List<AnnotationWrapper> getAnnotationWrappers() {
        return annotationWrappers;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public String getFieldStringValue() {
        return fieldStringValue;
    }

    public Field getField() {
        return field;
    }

    /**
     * 判断是否为空
     * @return
     */
    public boolean isNull(){
        return fieldValue == null;
    }

    /**
     * 判断字段是否为空
     *
     * @return
     */
    public boolean isNotBlank() {
        return fieldStringValue != null && !"null".equals(fieldStringValue)
                && !fieldStringValue.trim().equals("");
    }

    public String getStringValue() {
        return fieldStringValue;
    }

    /**
     * 正则替换
     *
     * @param value
     * @return
     */
    public String format(String value, AnnotationWrapper wrapper) {
        message.put("annoValue", String.valueOf(wrapper.value()));
        Pattern pattern = Pattern.compile("\\{[^\\{\\}]*\\}");
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            String group = matcher.group();
            String key = group.substring(1, group.length() - 1).trim();
            value = value.replace(group, message.get(key));
        }
        return value;
    }

    /**
     * 字段验证
     */
    public void validate(boolean checkAll) throws NotValidException{
        NotValidException notValidException = null;
        for (AnnotationWrapper annotationWrapper : annotationWrappers) {
            try {
                annotationWrapper.validate(this);
            } catch (NotValidException e) {
                if(checkAll){
                    if (notValidException == null) {
                        notValidException = e;
                    }else {
                        notValidException.merge(e);
                    }
                }else {
                    throw e;
                }
            }
        }
        if(notValidException != null && notValidException.hasError()){
            throw notValidException;
        }
    }

}
