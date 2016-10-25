package com.github.tj123.bean.validate;

import java.lang.annotation.*;

/**
 * 字段不能为空
 * Created by TJ on 2016/3/2.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotNull {

    String message() default ValidateUtil.NOTNULL_DEFAULT_MESSAGE;

    int messagePriority() default ValidateUtil.HIGHEST_PRIORITY;

}