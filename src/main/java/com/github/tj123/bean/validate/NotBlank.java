package com.github.tj123.bean.validate;

import java.lang.annotation.*;

/**
 * 字段不能为空
 * Created by TJ on 2016/3/2.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotBlank {

    String message() default ValidateUtil.NOT_BLANK_DEFAULT_MESSAGE;

    int priority() default ValidateUtil.NOT_BLANK_DEFAULT_PRIORITY;

    boolean trim() default true;

}
