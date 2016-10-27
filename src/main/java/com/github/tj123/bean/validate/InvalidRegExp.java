package com.github.tj123.bean.validate;

import java.lang.annotation.*;

/**
 * 若正则验证匹配就报异常
 * Created by TJ on 2016/3/2.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InvalidRegExp {

    String value()[];

    String message()[] default ValidateUtil.INVALID_REGEXP_DEFAULT_MESSAGE;

    int priority() default ValidateUtil.INVALID_REGEXP_DEFAULT_PRIORITY;

    boolean trim() default false;
}
