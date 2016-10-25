package com.github.tj123.bean.validate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段匹配的 正则
 * @author TJ
 */
@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidRegExp {
	
	String value();

	String message() default ValidateUtil.VALID_REGEXP_DEFAULT_MESSAGE;

	int messagePriority() default ValidateUtil.HIGHEST_PRIORITY - ValidateUtil.PRIORITY_STEP;

}