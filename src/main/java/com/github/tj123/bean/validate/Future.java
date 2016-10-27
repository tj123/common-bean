package com.github.tj123.bean.validate;

import java.lang.annotation.*;

/**
 * 以后的时间 不要忘了 还需要一个 @DatePattern
 */
@Target({ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Future {

    String message() default ValidateUtil.FUTURE_DEFAULT_MESSAGE;

    int priority() default ValidateUtil.FUTURE_DEFAULT_PRIORITY;

}
