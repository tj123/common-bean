package com.github.tj123.bean.validate;

import java.lang.annotation.*;

/**
 * 字段必须为值
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Assert {

    String value()[];

    String message() default ValidateUtil.ASSERT_DEFAULT_MESSAGE;

    int priority() default ValidateUtil.ASSERT_DEFAULT_PRIORITY;

    boolean trim() default true;

}
