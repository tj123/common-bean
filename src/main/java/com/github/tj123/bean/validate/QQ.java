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

    String message() default ValidateUtil.TEL_DEFAULT_MESSAGE;

    int messagePriority() default ValidateUtil.HIGHEST_PRIORITY - ValidateUtil.PRIORITY_STEP;

}
