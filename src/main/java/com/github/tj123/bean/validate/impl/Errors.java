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
    public Errors sort() {
        for (Map.Entry<String, ArrayList<ErrorMessage>> entry : entrySet()) {
            ArrayList<ErrorMessage> value = entry.getValue();
            Collections.sort(value, new Comparator<ErrorMessage>() {
                @Override
                public int compare(ErrorMessage msg1, ErrorMessage msg2) {
                    return msg1.getPriority() - msg2.getPriority();
                }
            });
            put(entry.getKey(),value);
        }
        return this;
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

    /**
     * 去除重复的消息
     */
    public Errors unique(){
        for (Map.Entry<String, ArrayList<ErrorMessage>> entry : entrySet()) {
            Iterator<ErrorMessage> iterator = entry.getValue().iterator();
            Set<ErrorMessage> set = new HashSet<>();
            while (iterator.hasNext()){
                ErrorMessage next = iterator.next();
                if (set.contains(next)) {
                    iterator.remove();
                }else{
                    set.add(next);
                }
            }
        }
        return this;
    }

    /**
     * 显示错误提示 （数组）
     * @return
     */
    public Map<String,List<String>> errors(){
        Map<String,List<String>> map = new HashMap<>();
        for (Map.Entry<String, ArrayList<ErrorMessage>> entry : entrySet()) {
            ArrayList<ErrorMessage> value = entry.getValue();
            List<String> list = new ArrayList<>();
            for (ErrorMessage errorMessage : value) {
                list.add(errorMessage.getMessage());
            }
            map.put(entry.getKey(),list);
        }
        return map;
    }

    /**
     * 显示错误提示 （同字段只有一个）
     * @return
     */
    public Map<String,String> error(){
        Map<String,String> map = new HashMap<>();
        for (Map.Entry<String, ArrayList<ErrorMessage>> entry : entrySet()) {
            ArrayList<ErrorMessage> value = entry.getValue();
            if (value != null && !value.isEmpty() && value.size() > 0) {
                map.put(entry.getKey(), value.get(0).getMessage());
            }
        }
        return map;
    }
}
