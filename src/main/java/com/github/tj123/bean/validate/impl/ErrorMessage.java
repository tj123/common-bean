package com.github.tj123.bean.validate.impl;

import com.github.tj123.bean.validate.ValidateUtil;

/**
 * 错误的消息对象
 */
public class ErrorMessage {

    private Integer priority;
    private String message;

    public ErrorMessage(Integer priority, String message) {
        this.message = message;
        this.priority = priority;
    }

    public Integer getPriority() {
        if (priority == null) {
            return ValidateUtil.LOWEST_PRIORITY;
        }
        return priority;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "priority=" + priority +
                ", message='" + message + '\'' +
                '}';
    }
}