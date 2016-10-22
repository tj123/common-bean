package com.github.tj123.db.commonv7.bean.validate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 校验的工具类
 * @author TJ
 *
 */
public class ValidateUtil {

	public static final String NOT_VALID_MESSAGE = " not valid!";

	/**
	 * 判断注解是否存在
	 * @param annotation
	 * @return
	 */
	public static boolean exist(Annotation annotation) {
		return annotation != null;
	}

	/**
	 * 判断是否为空
	 * @param string
	 * @return
	 */
	public static boolean isBlank(String string){
		return string == null || string.trim().equals("");
	}
	
	/**
	 * 获取数据直到不为空的数据
	 * @param strings
	 * @return
	 */
	public static String getNotNull(String... strings){
		for(String string:strings){
			if(!isBlank(string)) return string;
		}
		return "";
	}
	
	/**
	 * 当注解的value() 返回为String时返回 value值
	 * @param annotation
	 * @return
	 */
	public static String getStringValue(Annotation annotation){
		if(!exist(annotation)) return null;
		 Class<?> clazz = annotation.getClass();
		try {
			Method method = clazz.getMethod("value");
			Object o = method.invoke(annotation);
			if(o instanceof String){
				return (String) o;
			}
		} catch (Exception e) {
			
		}
		return null;
	}
	
	/**
	 * 当注解的message() 返回为String时返回 message值
	 * @param annotation
	 * @return
	 */
	public static String getMessage(Annotation annotation){
		if(!exist(annotation)) return null;
		 Class<?> clazz = annotation.getClass();
		try {
			Method method = clazz.getMethod("message");
			Object o = method.invoke(annotation);
			if(o instanceof String){
				return (String) o;
			}
		} catch (Exception e) {
			
		}
		return null;
	}
	
	/**
	 * 如果字符串为空则返回 ""
	 * @param string
	 * @return
	 */
	public static String check(String string){
		if(isBlank(string)){
			string = "";
		}
		return string;
	}

	/**
	 * 注解验证
	 * @param checkAll
	 */
	public static void validate(boolean checkAll) throws NotValidException{

	}
}


