package com.github.tj123.bean.validate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段为手机号
 * @author TJ
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidRegExp("\\d{11}")
public @interface Phone {

    String message() default ValidateUtil.PHONE_DEFAULT_MESSAGE;

    int priority() default ValidateUtil.PHONE_DEFAULT_PRIORITY;

}
