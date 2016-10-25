package com.github.tj123.bean.validate;

import java.lang.annotation.Annotation;

/**
 * 校验为空的回掉方法
 * @author TJ
 *
 */
interface CheckBlankCallBack2 {
	
	/**
	 * 不为空的处理
	 * @param value
	 * @param annotation
	 * @throws NotValidException
	 */
	void onNotBlank(String value, Annotation annotation) throws NotValidException;
	
	/**
	 * 为空的处理
	 * @throws NotValidException 
	 */
	void onBlank() throws NotValidException;

}
