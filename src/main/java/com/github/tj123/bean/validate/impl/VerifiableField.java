package com.github.tj123.bean.validate.impl;

import com.github.tj123.bean.validate.InEnum;
import com.github.tj123.bean.validate.Message;
import com.github.tj123.bean.validate.ValidateUtil;
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
public class VerifiableField {

    private Log log = LogFactory.getLog(VerifiableField.class);

    public VerifiableField() {
    }

    public VerifiableField(Field field, Object fieldValue) {
        setField(field, fieldValue);
    }

    private Field field;
    private Object fieldValue;
    private String fieldStringValue;
    private List<VerifiableAnnotation> annotations;
    private Map<String, String> message = new HashMap<>();

    public void setField(Field field, Object fieldValue) {
        this.field = field;
        this.fieldValue = fieldValue;
        this.fieldStringValue = String.valueOf(fieldValue);
        message.put("field", field.getName());
        message.put("value", getFieldStringValue());
        if (annotations != null) {
            annotations.clear();
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
        if (annotations == null) {
            annotations = new ArrayList<>();
        }
        for (Class<? extends Annotation> clazz : classes) {
            Annotation annotation = field.getAnnotation(clazz);
            annotations.add(new VerifiableAnnotation(annotation, clazz));
        }
    }

    public void setAnnotations(List<VerifiableAnnotation> annotations) {
        this.annotations = annotations;
    }

    public List<VerifiableAnnotation> getAnnotations() {
        return annotations;
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
     * 判断是否为空
     * @return
     */
    public boolean isNotNull(){
        return !isNull();
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

    /**
     * 获取注解
     * @param clazz
     * @param <A>
     * @return
     */
    public <A extends Annotation> A getAnnotation(Class<A> clazz){
        return field.getAnnotation(clazz);
    }

    /**
     * 获取字符串值
     * @return
     */
    public String getStringValue() {
        return fieldStringValue;
    }

    /**
     * 获取字符串值 是否去除空格
     * @return
     */
    public String getStringValue(boolean trim) {
        String stringValue = getStringValue();
        if(trim && stringValue != null){
            stringValue = stringValue.trim();
        }
        return stringValue;
    }


    /**
     * 正则替换
     *
     * @param value
     * @return
     */
    public String format(String value, VerifiableAnnotation annotation,Integer location) {
        String[] values = annotation.values();
        if (location == null) {
            String vu = String.valueOf(annotation.value());
            if (Pattern.compile("^\\d+\\.0+$").matcher(vu).find()) {
                Matcher matcher = Pattern.compile("^\\d+").matcher(vu);
                if (matcher.find()) {
                    vu = matcher.group();
                }
            }
            message.put("annoValue", vu);
        }else{
            String va = values[location];
            if (va == null && va.trim().equals("")) {
                Message message = field.getAnnotation(Message.class);
                if (message.value() != null) {
                    va = message.value();
                }
            }
            message.put("annoValue", String.valueOf(va));
        }
        if (values != null) {
            StringBuilder sb = new StringBuilder();
            for (String val : values) {
                sb.append(val).append(",");
            }
            sb.deleteCharAt(sb.length()-1);
            message.put("annoValues",sb.toString());
        }
        if(annotation.is(InEnum.class)){
            Class<? extends Enum<?>>[] vues = annotation.castTo(InEnum.class).value();
            message.put("annoValues", ValidateUtil.getEnumKeyValues(vues));
            message.put("annoValue", ValidateUtil.getEnumValues(vues));
            message.put("enumValues", ValidateUtil.getEnumValues(vues));
            message.put("enumKeys", ValidateUtil.getEnumKeys(vues));
        }else{
            message.remove("enumValues");
            message.remove("enumKeys");
        }
        Pattern pattern = Pattern.compile("\\{[^\\{\\}]*\\}");
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            String group = matcher.group();
            String key = group.substring(1, group.length() - 1).trim();
            value = value.replace(group, String.valueOf(message.get(key)));
        }
        return value;
    }

    /**
     * 获取注解上的消息
     * @return
     */
    public String getAnnotationMessage(){
        if(field == null) return "";
        Message message = field.getAnnotation(Message.class);
        if (message != null) {
            return message.value();
        }
        return "";
    }

    /**
     * 获取消息
     * @param annotation
     * @return
     */
    public String getMessage(VerifiableAnnotation annotation){
        String message = annotation.message();
        if (message != null && !message.trim().equals("")) {
            return format(message,annotation,null);
        }
        return format(getAnnotationMessage(),annotation,null);
    }

    /**
     * 获取消息
     * @param annotation
     * @return
     */
    public String getMessage(VerifiableAnnotation annotation,int location){
        String[] messages = annotation.messages();
        if(messages != null){
            String message = messages[location];
            if (message != null && !message.trim().equals("")) {
                return format(message,annotation,location);
            }
        }
        return format(getAnnotationMessage(),annotation,location);
    }

    /**
     * 字段验证
     */
    public void validate(boolean checkAll) throws NotValidException{
        NotValidException notValidException = null;
        for (VerifiableAnnotation annotationWrapper : annotations) {
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
