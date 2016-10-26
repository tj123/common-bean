package com.github.tj123.bean.validate;

import java.lang.annotation.*;

/**
 * 最小长度
 */
@Target({ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Past {

    String message() default ValidateUtil.PAST_DEFAULT_MESSAGE;

    int priority() default ValidateUtil.PAST_DEFAULT_PRIORITY;

}
