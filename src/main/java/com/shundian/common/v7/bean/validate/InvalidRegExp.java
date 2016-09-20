package com.shundian.common.v7.bean.validate;

import java.lang.annotation.*;

/**
 *  若正则验证匹配就报异常
 * Created by TJ on 2016/3/2.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InvalidRegExp{

    String value();

    String message() default "";
}
