package com.shundian.common.v7.bean.validate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段为电话号码
 * @author TJ
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidRegExp("\\d+")
public @interface Tel {

    String message() default "";

}
