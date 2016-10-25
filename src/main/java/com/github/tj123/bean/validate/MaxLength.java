package com.github.tj123.bean.validate;

import java.lang.annotation.*;

/**
 * 最小长度
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MaxLength {

    int value();

    String message() default ValidateUtil.MAX_LENGTH_DEFAULT_MESSAGE;
    int messagePriority() default ValidateUtil.LOWEST_PRIORITY - 3 * ValidateUtil.PRIORITY_STEP;

}
