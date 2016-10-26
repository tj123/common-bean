package com.github.tj123.bean;

/**
 * Created by TJ on 2016/9/2.
 */
class CannotConvertException extends Exception {

	private static final long serialVersionUID = 1L;

	public CannotConvertException() {
    }

    public CannotConvertException(String message) {
        super(message);
    }

    public CannotConvertException(Throwable e) {
        super(e);
    }
}
