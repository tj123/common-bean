package com.github.tj123.bean;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定日期格式 (用于类型转换,也用于验证)\
 * (不晓得日期转 字符串的时候会报什么异常)
 * @author TJ
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DatePattern {
	
	String value();
	
}
