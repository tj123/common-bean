package com.github.tj123.bean;

/**
 * po 转换时的错误
 * @author TJ
 *
 */
public class BeanConvertException extends Exception {

	private static final long serialVersionUID = 1L;

	public BeanConvertException() {
		this(BeanConfig.DEFAULT_ERROR);
	}

	public BeanConvertException(Throwable cause) {
		super(cause);
	}

	public BeanConvertException(String message) {
		super(message);
	}
	
}
