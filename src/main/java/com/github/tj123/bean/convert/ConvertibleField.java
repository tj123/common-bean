package com.github.tj123.bean.convert;

import com.github.tj123.bean.DateConvert;
import com.github.tj123.bean.DatePattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 可以转换的字段 只适用于转对象
 */
public class ConvertibleField {

    private Log log = LogFactory.getLog(ConvertibleField.class);

    private String MAP_DATE_PATTERN_SUFFIX = "_DatePattern";

    public ConvertibleField(Map<String, Object> map, String fieldName) {
        this.bean = map;
        this.fieldName = fieldName;
        if (map != null && !map.isEmpty() && map.size() != 0 && map.get(fieldName) != null) {
            fieldValue = map.get(fieldName);
            fieldClass = fieldValue.getClass();
        }
        isBean = false;
    }

    public ConvertibleField(Object bean, Object fieldValue, Field field) {
        field.setAccessible(true);
        this.bean = bean;
        this.fieldValue = fieldValue;
        this.fieldClass = field.getType();
        this.field = field;
        this.fieldName = field.getName();
        isBean = true;
    }

    private boolean isBean = false;

    private String fieldName;
    private Map<String, Object> map;
    private Object bean;
    private Object fieldValue;
    private Class<?> fieldClass;
    private Field field;

    /**
     * 是否为对象
     *
     * @return
     */
    public boolean isBean() {
        return isBean;
    }

    /**
     * 是否为map对象
     *
     * @return
     */
    public boolean isMap() {
        return !isBean();
    }

    /**
     * 是否为空
     *
     * @return
     */
    public boolean isNull() {
        return fieldValue == null;
    }

    /**
     * 是否为空
     *
     * @return
     */
    public boolean isNotNull() {
        return !isNull();
    }

    /**
     * 设置字段的值
     *
     * @param fieldValue
     */
    public void setFieldValue(Object fieldValue) throws Exception {
        if (fieldValue == null) {
            return;
        }
        fieldClass = fieldValue.getClass();
        this.fieldValue = fieldValue;
        setOriginFieldValue(fieldValue);
    }

    /**
     * 设置字段的值
     *
     * @param fieldValue
     */
    public void setOriginFieldValue(Object fieldValue) throws Exception {
        if (isBean()) {
            field.set(bean, fieldValue);
        } else {
            ((Map<String, Object>) bean).put(fieldName, fieldValue);
        }
    }

    /**
     * 被注解了 @DatePattern
     *
     * @return
     */
    public boolean hasDatePatternAnnotation() {
        return isBean() && field.getAnnotation(DatePattern.class) != null;
    }

    /**
     * 为字符串
     *
     * @return
     */
    public boolean isString() {
        return String.class.equals(fieldClass);
    }

    /**
     * 为double
     *
     * @return
     */
    public boolean isDouble() {
        return Double.class.equals(fieldClass);
    }

    /**
     * 为integer
     *
     * @return
     */
    public boolean isInteger() {
        return Integer.class.equals(fieldClass);
    }

    /**
     * 为float
     *
     * @return
     */
    public boolean isFloat() {
        return Float.class.equals(fieldClass);
    }

    /**
     * 为short
     *
     * @return
     */
    public boolean isShort() {
        return Short.class.equals(fieldClass);
    }

    /**
     * 为日期类型
     *
     * @return
     */
    public boolean isDate() {
        return Date.class.equals(fieldClass);
    }

    /**
     * 为日期的子类
     *
     * @return
     */
    public boolean isSubclassOfDate() {
        return fieldClass != null && Util.isSuperClass(Date.class, fieldClass);
    }

    /**
     * 为枚举
     *
     * @return
     */
    public boolean isEnum() {
        return fieldClass != null && fieldClass.isEnum();
    }

    /**
     * 判断类型是否相同
     *
     * @param field
     * @return
     */
    public boolean typeEqual(ConvertibleField field) {
        return fieldClass != null && field.fieldClass != null
                && fieldClass.equals(field.fieldClass);
    }

    /**
     * 获取日期格式
     *
     * @return
     */
    public String getDatePattern() {
        if (isMap() && bean != null) {
            Map<String, Object> map = (Map<String, Object>) this.bean;
            if (!map.isEmpty() && map.size() != 0) {
                Object value = map.get(fieldName + MAP_DATE_PATTERN_SUFFIX);
                if (value != null && value instanceof String)
                    return (String) value;
            }
            return null;
        }
        if (isBean()) {
            DatePattern annotation = field.getAnnotation(DatePattern.class);
            if (annotation != null) {
                return annotation.value();
            }
        }
        return null;
    }

    /**
     * 根据多种情况获取日期格式
     *
     * @return
     */
    public static String getDatePattern(ConvertibleField... fields) {
        String datePattern = null;
        for (ConvertibleField field : fields) {
            String pattern = field.getDatePattern();
            if (pattern != null) {
                datePattern = pattern;
            }
        }
        if (datePattern == null) {
            datePattern = BeanConfig.DEFAULT_DATE_PATTEN;
        }
        return datePattern;
    }

    /**
     * 设置值
     */
    public void setValue(String value, ConvertibleField field) throws Exception {
        if (value == null || value.trim().equals("")) {
            return;
        }
        if (isDouble()) {
            setFieldValue(Double.valueOf(value));
        } else if (isInteger()) {
            setFieldValue(Integer.valueOf(value));
        } else if (isFloat()) {
            setFieldValue(Float.valueOf(value));
        } else if (isShort()) {
            setFieldValue(Short.valueOf(value));
        } else if (isEnum()) {
            setFieldValue(Util.toEnumByKeyOrValue((Class<Enum>) fieldClass, value));
        } else if (isDate()) {
            setFieldValue(Util.stringToDate(value, getDatePattern(this, field)));
        } else if (isSubclassOfDate()) {
            if (Util.isInterfaceOf(DateConvert.class, fieldClass)) {
                DateConvert date = (DateConvert) fieldClass.newInstance();
                date.setDate(Util.stringToDate(value, getDatePattern(this, field)));
                setFieldValue(date);
            } else {
                throw new BeanConvertException(fieldClass + " 必须实现 DateConvert 接口,以完成转换");
            }
        } else if (isString()) {
            setFieldValue(value);
        } else {
            if (log.isErrorEnabled()) {
                log.error("不能识别的类型" + fieldClass, new Exception());
            }
        }
    }

    /**
     * 获取当前字段的字符串值
     */
    public String stringValue(ConvertibleField field) throws Exception {
        if (isNull()) {
            return null;
        } else if (isString() ) {
            return String.valueOf(fieldValue);
        } else if( isDouble() || isInteger() || isFloat() || isShort()) {
            return new BigDecimal(String.valueOf(fieldValue)).toString();
        } else if (isEnum()) {
            return Util.getEnumKeyOrValue((Enum) fieldValue);
        } else if (isDate() || isSubclassOfDate()) {
            return Util.dateToString((Date) fieldValue, getDatePattern(this, field));
        } else {
            if (log.isErrorEnabled()) {
                log.error("未知字段", new Exception());
            }
            return null;
        }
    }

    /**
     * 从 ConvertibleField 中复制值
     *
     * @param field
     */
    public void copyValueFrom(ConvertibleField field) throws Exception {
        if (field.typeEqual(this)) {
            setFieldValue(field.fieldValue);
        } else {
            setValue(field.stringValue(this), field);
        }
    }

}
