package com.github.tj123.bean.validate.impl;

import com.github.tj123.bean.DatePattern;
import com.github.tj123.bean.validate.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 注解的包裹类
 */
public class AnnotationWrapper {

    private Log log = LogFactory.getLog(AnnotationWrapper.class);

    public AnnotationWrapper() {
    }

    public AnnotationWrapper(Annotation annotation, Class<? extends Annotation> annotationClass) {
        setAnnotation(annotation, annotationClass);
    }

    private Annotation annotation;
    private Class<? extends Annotation> annotationClass;

    public void setAnnotation(Annotation annotation, Class<? extends Annotation> annotationClass) {
        this.annotation = annotation;
        this.annotationClass = annotationClass;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    /**
     * 判断是否存在
     *
     * @return
     */
    public boolean isExist() {
        return annotation != null;
    }

    /**
     * 判断是否存在
     *
     * @return
     */
    public boolean isNotExist() {
        return !isExist();
    }

    /**
     * 获取注解的 value();
     *
     * @return
     */
    public String value() {
        try {
            Method method = annotationClass.getMethod("value");
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
        try {
            Method method = annotationClass.getMethod("message");
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
     *
     * @return
     */
    public boolean hasValue() {
        return value() != null;
    }

    /**
     * 是否有 message()
     *
     * @return
     */
    public boolean hasMessage() {
        return value() != null;
    }

    /**
     * 获取注解的 priority();
     *
     * @return
     */
    public int priority() {
        try {
            Method method = annotationClass.getMethod("priority");
            Object invoke = method.invoke(annotation);
            if (invoke == null) {
                return ValidateUtil.LOWEST_PRIORITY;
            }
            return Integer.valueOf(String.valueOf(invoke));
        } catch (Exception e) {
        }
        return ValidateUtil.LOWEST_PRIORITY;
    }

    /**
     * 获取此注解上的注解
     *
     * @return
     */
    public <A extends Annotation> A getOnAnnotation(Class<A> clazz) {
        if(annotation == null)
            return null;
        return annotation.annotationType().getAnnotation(clazz);
    }

    /**
     * 获取注解上的注解包裹类
     *
     * @param clazz
     * @return
     */
    public AnnotationWrapper getOnAnnotationWrapper(Class<? extends Annotation> clazz) {
        return new AnnotationWrapper(getOnAnnotation(clazz), clazz);
    }

    /**
     * 获取多个注解上的注解包裹类
     *
     * @param classes
     * @return
     */
    public List<AnnotationWrapper> scanOn(Class<? extends Annotation>... classes) {
        List<AnnotationWrapper> list = new ArrayList<>();
        for (Class<? extends Annotation> clazz : classes) {
            AnnotationWrapper onAnnotationWrapper = getOnAnnotationWrapper(clazz);
            if (onAnnotationWrapper.isExist()) {
                list.add(onAnnotationWrapper);
            }
        }
        return list;
    }

    /**
     * 是否为此注解
     *
     * @param clazz
     * @return
     */
    public boolean is(Class<? extends Annotation> clazz) {
        return clazz.equals(annotationClass);
    }

    /**
     * 是否为此注解中的一个
     *
     * @param classes
     * @return
     */
    public boolean is(Class<? extends Annotation>... classes) {
        for (Class<? extends Annotation> clazz : classes) {
            if (is(clazz))
                return true;
        }
        return false;
    }

    /**
     * 递归验证
     * @param fieldWrapper
     * @throws NotValidException
     */
    public void validate(FieldWrapper fieldWrapper) throws NotValidException{
        if (is(Email.class, Phone.class, Tel.class, QQ.class)) {
            for (AnnotationWrapper annotationWrapper : scanOn(ValidateUtil.annotations)) {
                annotationWrapper.validate(fieldWrapper);
            }
        }else if (is(NotNull.class)){
            if (fieldWrapper.isNull()) {
                throw new NotValidException(this,fieldWrapper);
            }
        }else if(is(ValidRegExp.class)){
            if(fieldWrapper.isNotBlank()){
                if(!fieldWrapper.getStringValue().matches(value()))
                    throw new NotValidException(this,fieldWrapper);
            }
        }else if(is(InvalidRegExp.class)){
            if(fieldWrapper.isNotBlank()){
                if(fieldWrapper.getStringValue().matches(value()))
                    throw new NotValidException(this,fieldWrapper);
            }
        }else if(is(MinLength.class)){
            if (fieldWrapper.isNotBlank()) {
                if (fieldWrapper.getStringValue().length() < Integer.valueOf(value())) {
                    throw new NotValidException(this,fieldWrapper);
                }
            }
        }else if(is(MaxLength.class)){
            if (fieldWrapper.isNotBlank()) {
                if (fieldWrapper.getStringValue().length() > Integer.valueOf(value())) {
                    throw new NotValidException(this,fieldWrapper);
                }
            }
        }else if(is(ValidateMethod.class)){
            ValidateMethod validateMethod = (ValidateMethod)getAnnotation();
            Class<? extends Checkable> value = validateMethod.value();
            try {
                value.newInstance().check(fieldWrapper.getFieldValue());
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
                if (log.isErrorEnabled()) {
                    log.error("调用错误", e);
                }
            }
        }else if(is(DatePattern.class)){
            if (fieldWrapper.isNotBlank()) {
                try {
                    ValidateUtil.stringToDate(fieldWrapper.getStringValue(),value());
                } catch (Exception e) {
                    throw new NotValidException(this,fieldWrapper);
                }
            }
        }else{
            if (log.isErrorEnabled()) {
                log.error("不能识别的注解：" + annotation);
            }
        }
    }

}
