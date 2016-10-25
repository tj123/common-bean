package com.github.tj123.bean.validate;

import java.lang.annotation.*;

/**
 * 验证错误提示信息
 * Created by TJ on 2016/3/2.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Message {

    String message();

    int messagePriority() default ValidateUtil.LOWEST_PRIORITY;

}
