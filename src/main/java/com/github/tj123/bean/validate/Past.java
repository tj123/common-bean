package com.github.tj123.bean.validate;

import java.lang.annotation.*;

/**
 * 以前的时间 不要忘了 还需要一个 @DatePattern
 */
@Target({ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Past {

    String message() default ValidateUtil.PAST_DEFAULT_MESSAGE;

    int priority() default ValidateUtil.PAST_DEFAULT_PRIORITY;

}
