package com.github.tj123.bean.validate;

import java.lang.annotation.*;

/**
 * 字段为 email
 *
 * @author TJ
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidRegExp("\\w+@\\w+")
public @interface Email {

    String message() default ValidateUtil.EMAIL_DEFAULT_MESSAGE;

    int priority() default ValidateUtil.EMAIL_DEFAULT_PRIORITY;

}
