package com.github.tj123.bean.validate;

import com.github.tj123.bean.validate.impl.AnnotationWrapper;
import com.github.tj123.bean.validate.impl.FieldWrapper;

import java.util.*;

/**
 * Created by TJ on 2016/5/24.
 * 校验错误的返回对象
 */
public class NotValidException extends Exception {

    /**
     * 内部消息类
     */
    private class ErrorMessage {

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

    }

    /**
     * 错误消息最新
     */
    private class Errors extends HashMap<String, ArrayList<ErrorMessage>> {

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
         * 添加内容
         *
         * @param errors
         */
        public void add(Errors errors) {
            for (String key : errors.keySet()) {
                ArrayList<ErrorMessage> addValue = errors.get(key);
                if (addValue != null && !addValue.isEmpty() && addValue.size() != 0) {
                    ArrayList<ErrorMessage> originValue = get(key);
                    if (originValue == null || addValue.isEmpty() || addValue.size() == 0) {
                        put(key, addValue);
                    } else {
                        originValue.addAll(addValue);
                    }
                }

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

    private static final long serialVersionUID = 1L;

    public static final String NOT_VALID_MESSAGE = "not valid!";

    private String message;
    private Errors errors;

    public NotValidException() {
        this(NOT_VALID_MESSAGE);
    }

    public NotValidException(String message) {
        super(message);
        setMessage(message);
    }

    private Errors getErrors() {
        return errors;
    }

    public NotValidException(String field, int priority, String message) {
        this(message);
        addError(field, priority, message);
    }

    public NotValidException(String field, String message) {
        this(message);
        addError(field, ValidateUtil.VALIDATE_METHOD_DEFAULT_PRIORITY, message);
    }

    public NotValidException(AnnotationWrapper annotationWrapper, FieldWrapper fieldWrapper) {
        addError(annotationWrapper, fieldWrapper);
    }

    @Override
    public String getMessage() {
        String one = errors.getOne();
        if (one != null) {
            return one;
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
    public synchronized NotValidException addError(String field, int priority, String message) {
        if (errors == null) {
            errors = new Errors();
        }
        ArrayList<ErrorMessage> messages = errors.get(field);
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(new ErrorMessage(priority, message));
        errors.put(field,messages);
        return this;
    }

    /**
     * 添加错误消息
     *
     * @return
     */
    public NotValidException addError(AnnotationWrapper annotationWrapper, FieldWrapper fieldWrapper) {
        addError(fieldWrapper.getField().getName(), annotationWrapper.priority(),
                fieldWrapper.format(annotationWrapper.message(), annotationWrapper));
        return this;
    }

    /**
     * 是否包含有错误
     */
    public boolean hasError() {
        return errors != null && !errors.isEmpty() && errors.size() != 0;
    }

    /**
     * 合并错误
     *
     * @param notValidException
     * @return
     */
    public synchronized NotValidException merge(NotValidException notValidException) {
        Errors otherErrors = notValidException.getErrors();
        if (otherErrors == null || otherErrors.isEmpty() || otherErrors.size() == 0) {
            errors.add(otherErrors);
            return this;
        }
        return this;
    }

}
