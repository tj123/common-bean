package com.github.tj123.bean.validate.impl;

import java.util.*;

/**
 * 错误消息对象
 */

public class Errors extends HashMap<String, ArrayList<ErrorMessage>> {

    private static final long serialVersionUID = 1L;

    /**
     * 排序
     */
    public void sort() {
        for (Map.Entry<String, ArrayList<ErrorMessage>> entry : entrySet()) {
            List<ErrorMessage> value = entry.getValue();
            Collections.sort(value, new Comparator<ErrorMessage>() {
                @Override
                public int compare(ErrorMessage msg1, ErrorMessage msg2) {
                    return msg1.getPriority() - msg2.getPriority();
                }
            });
        }
    }

    /**
     * 获取一个
     *
     * @return
     */
    public String getOne() {
        if (isEmpty() || size() == 0) {
            return null;
        }
        sort();
        for (Map.Entry<String, ArrayList<ErrorMessage>> entry : entrySet()) {
            ArrayList<ErrorMessage> list = entry.getValue();
            for (ErrorMessage errorMessage : list) {
                if (errorMessage != null) {
                    String message = errorMessage.getMessage();
                    if (message != null && !message.trim().equals("")) {
                        return message;
                    }
                }
            }
        }
        return null;
    }
}
