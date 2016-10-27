package com.github.tj123.bean.validate;

import java.lang.annotation.*;

/**
 * 字段为数字
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidRegExp(value = "^\\d+(\\.\\d+)?$", message = "只能为数字", trim = true)
public @interface Numeric {

    String message() default ValidateUtil.NUMERIC_DEFAULT_MESSAGE;

    int priority() default ValidateUtil.NUMERIC_DEFAULT_PRIORITY;

    boolean trim() default true;

}
