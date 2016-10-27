package com.github.tj123.bean.validate;

import java.lang.annotation.*;

/**
 * 字段为电话号码
 *
 * @author TJ
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidRegExp("^[0-9a-zA-Z_@]+(\\.[a-zA-Z]{1,3})?$")
@MinLength(4)
@MaxLength(64)
public @interface QQ {

    String message() default ValidateUtil.QQ_DEFAULT_MESSAGE;

    int priority() default ValidateUtil.QQ_DEFAULT_PRIORITY;

}
