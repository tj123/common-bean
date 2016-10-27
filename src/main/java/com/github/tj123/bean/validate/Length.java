package com.github.tj123.bean.validate;

import java.lang.annotation.*;

/**
 * 最小长度
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Length {

    int value()[];

    String message() default ValidateUtil.LENGTH_DEFAULT_MESSAGE;

    int priority() default ValidateUtil.LENGTH_DEFAULT_PRIORITY;

    boolean trim() default true;

}
