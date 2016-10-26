package com.github.tj123.bean;

/**
 * Created by TJ on 2016/10/20.
 */
public interface Dto<PO extends Po<?>> extends Bean {
	
	PO toPo() throws Exception;
	
}
