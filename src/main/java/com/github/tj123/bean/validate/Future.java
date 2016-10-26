package com.github.tj123.bean.validate;

import java.lang.annotation.*;

/**
 * 最小长度
 */
@Target({ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Future {

    String message() default ValidateUtil.FUTURE_DEFAULT_MESSAGE;

    int priority() default ValidateUtil.FUTURE_DEFAULT_PRIORITY;

}
