package com.github.tj123.bean.validate;

import com.github.tj123.bean.DatePattern;
import com.github.tj123.bean.validate.impl.VerifiableAnnotation;
import com.github.tj123.bean.validate.impl.VerifiableField;
import com.github.tj123.bean.validate.impl.NotValidException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 校验的工具类
 *
 * @author TJ
 */
public class ValidateUtil {

    /**
     * 可以使用的花括符 有：
     * {field} 字段名
     * {annoValue} 字段注解上的 value()
     * {value} 字段的值
     * {annoValues} 字段注解上的 value()[]
     */

    public static final String MIN_LENGTH_DEFAULT_MESSAGE = "长度不能小于{annoValue}";
    public static final String MAX_LENGTH_DEFAULT_MESSAGE = "长度不能大于{annoValue}";
    public static final String EMAIL_DEFAULT_MESSAGE = "邮箱格式错误";
    public static final String VALID_REGEXP_DEFAULT_MESSAGE = "{value} 格式不符合要求";
    public static final String INVALID_REGEXP_DEFAULT_MESSAGE = "{value} 格式不符合要求";
    public static final String NOTNULL_DEFAULT_MESSAGE = "不能为空";
    public static final String PHONE_DEFAULT_MESSAGE = "格式不正确";
    public static final String TEL_DEFAULT_MESSAGE = "格式不正确";
    public static final String QQ_DEFAULT_MESSAGE = "格式不正确";
    public static final String VALIDATE_METHOD_DEFAULT_ERROR = "错误";
    public static final String DATE_PATTERN_DEFAULT_MESSAGE = "日期格式错误";
    public static final String NOT_BLANK_DEFAULT_MESSAGE = "不能为空";
    public static final String NUMERIC_DEFAULT_MESSAGE = "只能为数字";
    public static final String ASSERT_DEFAULT_MESSAGE = "只能为{annoValues}";
    public static final String ASSERT_BLANK_DEFAULT_MESSAGE = "只能为空";
    public static final String MIN_DEFAULT_MESSAGE = "不能大于{annoValue}";
    public static final String MAX_DEFAULT_MESSAGE = "不能小于{annoValue}";
    public static final String PAST_DEFAULT_MESSAGE = "時間已經過了";
    public static final String FUTURE_DEFAULT_MESSAGE = "還沒有到呢";
    public static final String IN_ENUM_DEFAULT_MESSAGE = "值不包含在{annoValues}中";

    public static final int LOWEST_PRIORITY = 30;

    public static final int EMAIL_DEFAULT_PRIORITY = 3;
    public static final int INVALID_REGEXP_DEFAULT_PRIORITY = 2;
    public static final int MAX_LENGTH_DEFAULT_PRIORITY = 3;
    public static final int MESSAGE_DEFAULT_PRIORITY = LOWEST_PRIORITY;
    public static final int MIN_LENGTH_DEFAULT_PRIORITY = 3;
    public static final int NOTNULL_DEFAULT_PRIORITY = 1;
    public static final int PHONE_DEFAULT_PRIORITY = 4;
    public static final int QQ_DEFAULT_PRIORITY = 4;
    public static final int TEL_DEFAULT_PRIORITY = 4;
    public static final int VALIDATE_METHOD_DEFAULT_PRIORITY = 2;
    public static final int VALID_REGEXP_DEFAULT_PRIORITY = 2;
    public static final int NOT_BLANK_DEFAULT_PRIORITY = 1;
    public static final int NUMERIC_DEFAULT_PRIORITY = 4;
    public static final int ASSERT_DEFAULT_PRIORITY = 5;
    public static final int ASSERT_BLANK_DEFAULT_PRIORITY = 1;
    public static final int MIN_DEFAULT_PRIORITY = 2;
    public static final int MAX_DEFAULT_PRIORITY = 3;
    public static final int PAST_DEFAULT_PRIORITY = 6;
    public static final int FUTURE_DEFAULT_PRIORITY = 6;
    public static final int IN_ENUM_DEFAULT_PRIORITY = 6;


    /**
     * 日期转换
     */
    public static Date stringToDate(String date, String pattern) throws Exception {
        return new SimpleDateFormat(pattern).parse(date);
    }

    private static final Log log = LogFactory.getLog(ValidateUtil.class);

    @SuppressWarnings("unchecked")
	public static final Class<? extends Annotation>[] ANNOTATIONS = new Class[]{Email.class,
            InvalidRegExp.class, MaxLength.class, MinLength.class, NotNull.class, Phone.class,
            QQ.class, Tel.class, ValidateMethod.class, ValidRegExp.class, DatePattern.class,
            Assert.class,AssertBlank.class,Max.class,Min.class,NotBlank.class,Past.class,
            Future.class,InEnum.class,Numeric.class};

    /**
     * 验证
     *
     * @param value
     * @param <V>
     */
    public static <V> void validate(V value) throws NotValidException {
        validate(value, true);
    }

    /**
     * 直验证一个字段
     *
     * @param value
     * @param <V>
     */
    public static <V> void validate(V value, String fieldName) throws NotValidException {
        validate(value, fieldName, true);
    }

    /**
     * 验证
     *
     * @param value
     * @param <V>
     */
    public static <V> void validate(V value, boolean checkAll) throws NotValidException {
        Class<?> clazz = value.getClass();
        if (clazz.getAnnotation(Validate.class) == null) {
            if (log.isWarnEnabled()) {
                log.warn(clazz + "忽略校验!");
            }
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        NotValidException notValidException = null;
        VerifiableField verifiableField = new VerifiableField();
        for (Field field : fields) {
            try {
                validateField(value, verifiableField, field, checkAll);
            } catch (NotValidException e) {
                if(checkAll){
                    if (notValidException == null) {
                        notValidException = e;
                    }else {
                        notValidException.merge(e);
                    }
                }else {
                    throw e;
                }
            }
        }
        if(notValidException != null && notValidException.hasError()){
            throw notValidException;
        }
    }

    /**
     * 验证多字段
     *
     * @param value
     * @param <V>
     */
    public static <V> void validate(V value, String... fieldNames) throws NotValidException {
        validate(value,fieldNames,true);
    }

    /**
     * 验证多字段
     *
     * @param value
     * @param <V>
     */
    public static <V> void validate(V value, String[] fieldNames, boolean checkAll) throws NotValidException {
        NotValidException notValidException = null;
        for (String fieldName : fieldNames) {
            try {
                validate(value,fieldName,checkAll);
            } catch (NotValidException e) {
                if(checkAll){
                    if (notValidException == null) {
                        notValidException = e;
                    }else {
                        notValidException.merge(e);
                    }
                }else {
                    throw e;
                }
            }
        }
        if(notValidException != null && notValidException.hasError()){
            throw notValidException;
        }
    }


    /**
     * 验证
     *
     * @param value
     * @param <V>
     */
    public static <V> void validate(V value, String fieldName, boolean checkAll) throws NotValidException {
        Class<?> clazz = value.getClass();
        if (clazz.getAnnotation(Validate.class) == null) {
            if (log.isWarnEnabled()) {
                log.warn(clazz + "忽略校验!");
            }
            return;
        }
        try {
            Field field = clazz.getDeclaredField(fieldName);
            VerifiableField verifiableField = new VerifiableField();
            validateField(value, verifiableField, field, checkAll);
        } catch (NoSuchFieldException e) {
            if(log.isErrorEnabled()){
                log.error(e.getMessage(),e);
            }
        }
    }

    /**
     * 单字段校验
     *
     * @param value
     * @param verifiableField
     * @param field
     * @param checkAll
     * @param <V>
     * @throws NotValidException
     */
    @SuppressWarnings("unused")
	private static <V> void validateField(V value, VerifiableField verifiableField, Field field, boolean checkAll) throws NotValidException {
        field.setAccessible(true);
        Object fieldValue = null;
        NotValidException notValidException = null;
        try {
            fieldValue = field.get(value);
        } catch (IllegalAccessException e) {
            if (log.isDebugEnabled()) {
                log.debug("获取字段失败", e);
            }
        }
        verifiableField.setField(field, fieldValue);
        verifiableField.setAnnotations(ANNOTATIONS);
        try {
            verifiableField.validate(checkAll);
        } catch (NotValidException e) {
            if(checkAll){
                if (notValidException == null) {
                    notValidException = e;
                }else {
                    notValidException.merge(e);
                }
            }else {
                throw e;
            }
        }
        if(notValidException != null && notValidException.hasError()){
            throw notValidException;
        }
    }

    /**
     * 驗證枚舉
     * @param values
     * @param stringValue
     * @param annotation
     * @param field
     * @throws NotValidException
     */
    public static void checkInNum(Class<? extends Enum<?>>[] values,
                                  String stringValue, VerifiableAnnotation annotation, VerifiableField field)
            throws NotValidException{
        for (Class<? extends Enum<?>> value : values) {
            for (Annotation anno : value.getAnnotations()) {
                if(String.valueOf(anno).equals(stringValue))
                    return;
            }
        }
        for (Class<? extends Enum<?>> value : values) {
            String va = null;
            try {
                Method method = value.getMethod("getKey");
                for (Annotation anno : value.getAnnotations()) {
                    Object invoke = method.invoke(anno);
                    if (String.valueOf(invoke).equals(stringValue)) {
                        return;
                    }
                }
                throw new NotValidException(annotation,field);
            } catch (NoSuchMethodException e) {
            } catch (InvocationTargetException e) {
            } catch (IllegalAccessException e) {
            }
        }
    }
}


