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
@ValidRegExp("\\d+")
public @interface QQ {

    String message() default ValidateUtil.QQ_DEFAULT_MESSAGE;

    int priority() default ValidateUtil.QQ_DEFAULT_PRIORITY;

}
