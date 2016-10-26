package com.github.tj123.bean;

import com.github.tj123.bean.validate.Validate;
import com.github.tj123.bean.validate.ValidateUtil;

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
	
	String value() default BeanConfig.DEFAULT_DATE_PATTEN;

	String message() default ValidateUtil.DATE_PATTERN_DEFAULT_MESSAGE;

	int priority() default ValidateUtil.LOWEST_PRIORITY;
}
