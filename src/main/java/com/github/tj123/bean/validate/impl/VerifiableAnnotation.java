package com.github.tj123.bean.validate.impl;

import com.github.tj123.bean.DatePattern;
import com.github.tj123.bean.validate.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 注解的包裹类
 */
public class VerifiableAnnotation {

    private Log log = LogFactory.getLog(VerifiableAnnotation.class);

    public VerifiableAnnotation() {
    }

    public VerifiableAnnotation(Annotation annotation, Class<? extends Annotation> annotationClass) {
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
     * 获取注解的 value();
     *
     * @return
     */
    public String[] values() {
        try {
            Method method = annotationClass.getMethod("value");
            Object invoke = method.invoke(annotation);
            if (invoke == null) {
                return null;
            }
            return (String[])invoke;
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
     * 获取注解的 message();
     *
     * @return
     */
    public String[] messages() {
        try {
            Method method = annotationClass.getMethod("message");
            Object invoke = method.invoke(annotation);
            if (invoke == null) {
                return null;
            }
            return (String[]) invoke;
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
        if (annotation == null)
            return null;
        return annotation.annotationType().getAnnotation(clazz);
    }

    /**
     * 获取注解上的注解包裹类
     *
     * @param clazz
     * @return
     */
    public VerifiableAnnotation getOnVerifiableAnnotation(Class<? extends Annotation> clazz) {
        return new VerifiableAnnotation(getOnAnnotation(clazz), clazz);
    }

    /**
     * 获取多个注解上的注解包裹类
     *
     * @param classes
     * @return
     */
    public List<VerifiableAnnotation> scanOn(@SuppressWarnings("unchecked") Class<? extends Annotation>... classes) {
        List<VerifiableAnnotation> list = new ArrayList<>();
        for (Class<? extends Annotation> clazz : classes) {
            VerifiableAnnotation onAnnotation = getOnVerifiableAnnotation(clazz);
            if (onAnnotation.isExist()) {
                list.add(onAnnotation);
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
        return isExist() && clazz.equals(annotationClass);
    }

    /**
     * 是否为此注解中的一个
     *
     * @param classes
     * @return
     */
    public boolean is(@SuppressWarnings("unchecked") Class<? extends Annotation>... classes) {
        for (Class<? extends Annotation> clazz : classes) {
            if (is(clazz))
                return true;
        }
        return false;
    }

    /**
     * 强转
     * @param clazz
     * @param <A>
     * @return
     */
    public <A extends Annotation> A castTo(Class<A> clazz){
        return (A) annotation;
    }

    /**
     * 递归验证
     *
     * @param field
     * @throws NotValidException
     */
    @SuppressWarnings("unchecked")
	public void validate(VerifiableField field) throws NotValidException {
        if(!isExist()) return;
        if (is(Email.class, Phone.class, Tel.class, QQ.class,Numeric.class)) {
            for (VerifiableAnnotation annotation : scanOn(ValidRegExp.class,
                    InvalidRegExp.class)) {
                annotation.validate(field);
            }
        } else if (is(NotNull.class)) {
            if (field.isNull()) {
                throw new NotValidException(this, field);
            }
        } else if (is(NotBlank.class)) {
            if (field.isNull()) {
                throw new NotValidException(this, field);
            }else{
                NotBlank notBlank = castTo(NotBlank.class);
                String stringValue = field.getStringValue();
                if (notBlank.trim()) {
                    stringValue = stringValue.trim();
                }
                if("".equals(stringValue)){
                    throw new NotValidException(this, field);
                }

            }
        } else if (is(ValidRegExp.class)) {
            if (field.isNotBlank()) {
                ValidRegExp validRegExp = castTo(ValidRegExp.class);
                String[] values = validRegExp.value();
                String stringValue = field.getStringValue();
                NotValidException exception = new NotValidException();
                for (int i = 0; i < values.length; i++) {
                    if (!Pattern.compile(values[i]).matcher(stringValue).find())
                        exception.addError(this, field,i);
                }
                if (exception.hasError()) {
                    throw exception;
                }
            }
        } else if (is(InvalidRegExp.class)) {
            if (field.isNotBlank()) {
                InvalidRegExp invalidRegExp = castTo(InvalidRegExp.class);
                String[] values = invalidRegExp.value();
                String stringValue = field.getStringValue();
                NotValidException exception = new NotValidException();
                for (int i = 0; i < values.length; i++) {
                    if (Pattern.compile(values[i]).matcher(stringValue).find())
                        exception.addError(this, field,i);
                }
                if (exception.hasError()) {
                    throw exception;
                }
            }
        } else if (is(MinLength.class)) {
            if (field.isNotBlank()) {
                if (field.getStringValue().length() < Integer.valueOf(value())) {
                    throw new NotValidException(this, field);
                }
            }
        } else if (is(MaxLength.class)) {
            if (field.isNotBlank()) {
                if (field.getStringValue().length() > Integer.valueOf(value())) {
                    throw new NotValidException(this, field);
                }
            }
        } else if (is(ValidateMethod.class)) {
            ValidateMethod validateMethod = (ValidateMethod) getAnnotation();
            Class<? extends Checkable> value = validateMethod.value();
            try {
                value.newInstance().check(field.getFieldValue());
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
                if (log.isErrorEnabled()) {
                    log.error("调用错误", e);
                }
            }
        } else if (is(DatePattern.class)) {
            if (field.isNotBlank()) {
                try {
                    ValidateUtil.stringToDate(field.getStringValue(), value());
                } catch (Exception e) {
                    throw new NotValidException(this, field);
                }
            }
        }else if(is(Max.class)){
            if(field.isNotBlank()){
                getOnVerifiableAnnotation(Numeric.class).validate(field);
                if(Double.valueOf(field.getStringValue()) > Double.valueOf(value())){
                    throw new NotValidException(this, field);
                }
            }
        }else if(is(Min.class)) {
            if (field.isNotBlank()) {
                getOnVerifiableAnnotation(Numeric.class).validate(field);
                if (Double.valueOf(field.getStringValue()) < Double.valueOf(value())) {
                    throw new NotValidException(this, field);
                }
            }
        }else if(is(AssertBlank.class)){
            if (field.getFieldValue() != null) {
                AssertBlank assertBlank = castTo(AssertBlank.class);
                String stringValue = field.getStringValue();
                if (assertBlank.trim()) {
                    stringValue = stringValue.trim();
                }
                if (!"".equals(stringValue)) {
                    throw new NotValidException(this, field);
                }
            }
        }else if(is(Assert.class)){
            if (field.getFieldValue() != null) {
                Assert anAssert = castTo(Assert.class);
                String stringValue = field.getStringValue();
                if (anAssert.trim()) {
                    stringValue = stringValue.trim();
                }
                boolean has = false;
                for (String value : anAssert.value()) {
                    if (stringValue.equals(value)) {
                        has = true;
                        break;
                    }
                }
                if (!has) {
                    throw new NotValidException(this, field);
                }
            }
        }else if(is(InEnum.class)){
            if (field.isNotBlank()) {
                InEnum inEnum = castTo(InEnum.class);
                String stringValue = field.getStringValue();
                if(inEnum.trim()){
                    stringValue = stringValue.trim();
                }
                ValidateUtil.checkInNum(inEnum.value(),stringValue,this,field);
            }
        }else if(is(Future.class)){
            if (field.isNotBlank()) {
                try {
                    Date date = ValidateUtil.stringToDate(field.getStringValue(), value());
                    if(date.getTime() < System.currentTimeMillis()){
                        throw new Exception();
                    }
                } catch (Exception e) {
                    throw new NotValidException(this, field);
                }
            }
        }else if(is(Past.class)){
            if (field.isNotBlank()) {
                try {
                    Date date = ValidateUtil.stringToDate(field.getStringValue(), value());
                    if(date.getTime() > System.currentTimeMillis()){
                        throw new Exception();
                    }
                } catch (Exception e) {
                    throw new NotValidException(this, field);
                }
            }
        } else {
            if (log.isErrorEnabled()) {
                log.error("不能识别的注解：" + annotation);
            }
        }
    }

}
