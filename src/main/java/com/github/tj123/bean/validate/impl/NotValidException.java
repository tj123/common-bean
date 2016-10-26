package com.github.tj123.bean.validate.impl;

import com.github.tj123.bean.validate.ValidateUtil;

import java.util.ArrayList;
import java.util.Map;

/**
 * 校验错误的返回对象
 */
public class NotValidException extends Exception {

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

    private Errors getErrorsInner() {
        return errors;
    }

    public Errors getErrors() {
        errors.sort();
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

    public NotValidException(VerifiableAnnotation annotationWrapper, VerifiableField verifiableField) {
        addError(annotationWrapper, verifiableField);
    }

    public NotValidException(VerifiableAnnotation annotationWrapper, VerifiableField verifiableField,int location) {
        addError(annotationWrapper, verifiableField,location);
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
    public NotValidException addError(String field, ErrorMessage message) {
        if (message == null) {
            return this;
        }
        addError(field, message.getPriority(), message.getMessage());
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
        errors.put(field, messages);
        return this;
    }

    /**
     * 添加错误消息
     *
     * @return
     */
    public NotValidException addError(VerifiableAnnotation annotation, VerifiableField field) {
        addError(field.getField().getName(), annotation.priority(), field.getMessage(annotation));
        return this;
    }

    /**
     * 添加错误消息
     *
     * @return
     */
    public NotValidException addError(VerifiableAnnotation annotation, VerifiableField field,int location) {
        addError(field.getField().getName(), annotation.priority(), field.getMessage(annotation,location));
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
        if (notValidException == null)
            return this;
        Errors otherErrors = notValidException.getErrorsInner();
        if (otherErrors != null && !otherErrors.isEmpty() && otherErrors.size() != 0) {
            for (Map.Entry<String, ArrayList<ErrorMessage>> entry : otherErrors.entrySet()) {
                String key = entry.getKey();
                for (ErrorMessage errorMessage : entry.getValue()) {
                    addError(key,errorMessage);
                }
            }
        }
        return this;
    }

}
