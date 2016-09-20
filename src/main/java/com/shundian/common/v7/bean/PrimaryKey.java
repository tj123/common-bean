package com.shundian.common.v7.bean;

import java.lang.annotation.*;

/**
 * 主键
 * @author TJ
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PrimaryKey {

}
