package com.github.tj123.bean.validate;


import com.github.tj123.bean.validate.impl.NotValidException;

/**
 * 验证 字段 调用接口
 * @author TJ
 *
 */
public interface Checkable {

	/**
	 * 返回字段的值 若错误就抛出异常
	 * @param value
	 * @throws NotValidException
	 */
	void check(Object value) throws NotValidException;
	
}
