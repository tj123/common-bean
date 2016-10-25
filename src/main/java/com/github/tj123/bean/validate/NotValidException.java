package com.github.tj123.bean.validate;

import java.util.*;

/**
 * Created by TJ on 2016/5/24.
 * 校验错误的返回对象
 */
public class NotValidException extends Exception {

    public static final String NOT_VALID_MESSAGE = "not valid!";

    private String message;
    private Map<String, List<String>> error = new HashMap<>();

    public NotValidException() {
        this(NOT_VALID_MESSAGE);
    }

    public NotValidException(String message) {
        super(message);
        setMessage(message);
    }

    public NotValidException(String field, String message) {
        this(message);
        addError(field, message);
    }

    @Override
    public String getMessage() {
        Iterator<Map.Entry<String, List<String>>> it = error.entrySet().iterator();
        if (it.hasNext()) {
            Map.Entry<String, List<String>> entry = it.next();
            List<String> list = entry.getValue();
            return list.get(0);
        }
        return message;
    }

    /**
     * 设置消息
     */
    public NotValidException setMessage(String message) {
        if (message != null && !message.trim().equals(""))
            this.message = message;
        return this;
    }

    /**
     * 添加错误
     */
    public NotValidException addError(String field, String message) {
        List<String> msgs = error.get(field);
        if (msgs == null) {
            msgs = new ArrayList<>();
        }
        msgs.add(message);
        error.put(field, msgs);
        return this;
    }

    /**
     * 获所有的错误
     *
     * @return
     */
    public Map<String, List<String>> error() {
        return error;
    }

    /**
     * 是否包含有错误
     */
    public boolean hasError() {
        return error != null && !error.isEmpty() && error.size() != 0;
    }

}
