package com.github.tj123.bean;

/**
 * Created by TJ on 2016/9/2.
 */
class CannotConvertException extends Exception{

    public CannotConvertException(){

    }

    public CannotConvertException(Throwable e) {
        super(e);
    }
}