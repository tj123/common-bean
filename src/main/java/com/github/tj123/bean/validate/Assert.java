package com.github.tj123.bean.validate;

import java.lang.annotation.*;

/**
 * 字段必须为值
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Assert {

    String value();

    String message() default ValidateUtil.NOT_BLANK_DEFAULT_MESSAGE;

    int priority() default ValidateUtil.NOT_BLANK_DEFAULT_PRIORITY;

    boolean trim() default true;

}
