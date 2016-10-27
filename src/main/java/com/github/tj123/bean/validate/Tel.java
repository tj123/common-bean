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
@ValidRegExp("\\[d\\x20\\-]+")
public @interface Tel {

    String message() default ValidateUtil.TEL_DEFAULT_MESSAGE;

    int priority() default ValidateUtil.TEL_DEFAULT_PRIORITY;

}
