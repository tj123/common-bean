package com.github.tj123.bean.validate;

import java.lang.annotation.*;

/**
 * 调用验证方法，完成一个接口即可使用
 *
 * @author TJ
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidateMethod {

    Class<? extends Checkable> value();

    String message() default ValidateUtil.VALIDATE_METHOD_DEFAULT_ERROR;

}
