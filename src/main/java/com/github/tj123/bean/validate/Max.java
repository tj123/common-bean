package com.github.tj123.bean.validate;

import java.lang.annotation.*;

/**
 * 最小长度
 */
@Target({ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Numeric
public @interface Max {

    double value();

    String message() default ValidateUtil.MAX_DEFAULT_MESSAGE;

    int priority() default ValidateUtil.MAX_DEFAULT_PRIORITY;

}
