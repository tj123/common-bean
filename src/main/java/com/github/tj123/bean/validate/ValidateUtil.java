package com.github.tj123.bean.validate;

import com.github.tj123.bean.DatePattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验的工具类
 *
 * @author TJ
 */
public class ValidateUtil {

    private static final Log log = LogFactory.getLog(ValidateUtil.class);

    public static final String NOT_VALID_MESSAGE = " not valid!";

    public static final String MIN_LENGTH_DEFAULT_MESSAGE = "字段：{field} 长度不能小于{annoValue}";
    public static final String MAX_LENGTH_DEFAULT_MESSAGE = "字段：{field} 长度不能大于{annoValue}";
    public static final String EMAIL_DEFAULT_MESSAGE = "字段：{field} 邮箱格式错误";
    public static final String VALID_REGEXP_DEFAULT_MESSAGE = "字段：{field} 值：{value} 格式不符合要求";
    public static final String INVALID_REGEXP_DEFAULT_MESSAGE = "字段：{field} 值：{value} 格式不符合要求";
    public static final String NOTNULL_DEFAULT_MESSAGE = "字段：{field} 不能为空";
    public static final String PHONE_DEFAULT_MESSAGE = "字段：{field} 格式不正确";
    public static final String TEL_DEFAULT_MESSAGE = "字段：{field} 格式不正确";
    public static final String VALIDATE_METHOD_DEFAULT_ERROR = "字段：{field} 错误";
    public static final String DATE_PATTERN_DEFAULT_MESSAGE = "字段：{field} 日期格式错误";

    public static final int LOWEST_PRIORITY = 30;
    public static final int HIGHEST_PRIORITY = 0;
    public static final int PRIORITY_STEP = 1;


    /**
     * 判断注解是否存在
     *
     * @param annotation
     * @return
     */
    public static boolean exist(Annotation annotation) {
        return annotation != null;
    }

    /**
     * 判断是否为空
     *
     * @param string
     * @return
     */
    public static boolean isBlank(String string) {
        return string == null || string.trim().equals("");
    }

    /**
     * 获取数据直到不为空的数据
     *
     * @param strings
     * @return
     */
    public static String getNotNull(String... strings) {
        for (String string : strings) {
            if (!isBlank(string)) return string;
        }
        return "";
    }

    /**
     * 当注解的value() 返回为String时返回 value值
     *
     * @param annotation
     * @return
     */
    public static String getStringValue(Annotation annotation) {
        if (!exist(annotation)) return null;
        Class<?> clazz = annotation.getClass();
        try {
            Method method = clazz.getMethod("value");
            Object o = method.invoke(annotation);
            if (o instanceof String) {
                return (String) o;
            }
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 当注解的message() 返回为String时返回 message值
     *
     * @param annotation
     * @return
     */
    public static String getMessage(Annotation annotation) {
        if (!exist(annotation)) return null;
        Class<?> clazz = annotation.getClass();
        try {
            Method method = clazz.getMethod("message");
            Object o = method.invoke(annotation);
            if (o instanceof String) {
                return (String) o;
            }
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 如果字符串为空则返回 ""
     *
     * @param string
     * @return
     */
    public static String check(String string) {
        if (isBlank(string)) {
            string = "";
        }
        return string;
    }

    /**
     * 注解验证
     */
    public static <V> void validate(V value) throws NotValidException {
        validate(value, true);
    }

    /**
     * 检查字段的值是否为空
     *
     * @param value
     * @param callback
     * @throws NotValidException
     */
    private static void checkFieldBlank(Object value, CheckBlankCallBack callback) throws NotValidException {
        if (value == null || (value instanceof String && isBlank((String) value))) {
            callback.onBlank();
        } else if (value instanceof String) {
            callback.onNotBlank(((String) value).trim());
        } else {
            callback.onBlank();
        }
    }


    /**
     * 读取 标注在annotations 上的 clazz
     *
     * @param annotations
     * @throws NotValidException
     */
    private static <A extends Annotation> void checkAnnoBehind(Class<A> clazz, CheckBlankCallBack2 callBack,
                                                               Annotation... annotations) throws NotValidException {
        for (Annotation annotation : annotations) {
            if (exist(annotation)) {
                A anno = annotation.annotationType().getAnnotation(clazz);
                String value = getStringValue(anno);
                if (isBlank(value)) {
                    callBack.onBlank();
                } else {
                    callBack.onNotBlank(value, annotation);
                }
            }
        }
    }

    /**
     * 注解验证
     *
     * @param checkAll
     */
    public static <V> void validate(V value, final boolean checkAll) throws NotValidException {
        Class<?> clazz = value.getClass();
        if (!exist(clazz.getAnnotation(Validate.class))) {
            if (log.isWarnEnabled()) {
                log.warn(clazz + "忽略校验!");
            }
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        final NotValidException notValidException = new NotValidException();
        for (final Field field : fields) {
            field.setAccessible(true);
            Object fieldValue = null;
            try {
                fieldValue = field.get(value);
            } catch (IllegalAccessException e) {
                if (log.isDebugEnabled()) {
                    log.debug("获取字段失败", e);
                }
            }
            final InvalidRegExp invalidRegExp = field.getAnnotation(InvalidRegExp.class);
            final Message message = field.getAnnotation(Message.class);
            final NotNull notNull = field.getAnnotation(NotNull.class);
            final ValidRegExp validRegExp = field.getAnnotation(ValidRegExp.class);
            final Msg msg = new Msg();
            msg.setField(field.getName());
            msg.setValue(String.valueOf(fieldValue));
            //@NotNull
            if (exist(notNull)) {
                msg.setAnnoValue(notNull);
                checkFieldBlank(fieldValue, new CheckBlankCallBack() {
                    @Override
                    public void onNotBlank(String value) throws NotValidException {

                    }

                    @Override
                    public void onBlank() throws NotValidException {
                        String mg = format(getNotNull(getMessage(notNull), ValidateUtil.NOTNULL_DEFAULT_MESSAGE, getStringValue(message)), msg);
                        if (checkAll) {
                            notValidException.addError(msg.getField(), mg);
                        } else {
                            throw new NotValidException(mg);
                        }
                    }
                });
            }

            //@ValidRegExp
            if (exist(validRegExp)) {
                msg.setAnnoValue(validRegExp);
                checkFieldBlank(fieldValue, new CheckBlankCallBack() {
                    @Override
                    public void onNotBlank(String value) throws NotValidException {
                        if (!value.matches(validRegExp.value())) {
                            String mg = format(getNotNull(getMessage(validRegExp), getStringValue(message)), msg);
                            if (checkAll) {
                                notValidException.addError(msg.getField(), mg);
                            } else {
                                throw new NotValidException(mg);
                            }
                        }
                    }

                    @Override
                    public void onBlank() throws NotValidException {
                    }
                });
            }

            //@ValidRegExp
            if (exist(invalidRegExp)) {
                msg.setAnnoValue(invalidRegExp);
                checkFieldBlank(fieldValue, new CheckBlankCallBack() {
                    @Override
                    public void onNotBlank(String value) throws NotValidException {
                        if (value.matches(invalidRegExp.value())) {
                            String mg = format(getNotNull(getMessage(invalidRegExp), getStringValue(message)), msg);
                            if (checkAll) {
                                notValidException.addError(msg.getField(), mg);
                            } else {
                                throw new NotValidException(mg);
                            }
                        }
                    }

                    @Override
                    public void onBlank() throws NotValidException {
                    }
                });
            }

            final MaxLength maxLength = field.getAnnotation(MaxLength.class);
            final MinLength minLength = field.getAnnotation(MinLength.class);

            if (exist(maxLength)) {
                msg.setAnnoValue(maxLength);
                checkFieldBlank(fieldValue, new CheckBlankCallBack() {
                    @Override
                    public void onNotBlank(String value) throws NotValidException {
                        if (value.length() > maxLength.value()) {
                            String mg = format(getNotNull(getMessage(maxLength), getStringValue(message)), msg);
                            if (checkAll) {
                                notValidException.addError(msg.getField(), mg);
                            } else {
                                throw new NotValidException(mg);
                            }
                        }
                    }

                    @Override
                    public void onBlank() throws NotValidException {
                    }
                });
            }

            if (exist(minLength)) {
                msg.setAnnoValue(minLength);
                checkFieldBlank(fieldValue, new CheckBlankCallBack() {
                    @Override
                    public void onNotBlank(String value) throws NotValidException {
                        if (value.length() < minLength.value()) {
                            String mg = format(getNotNull(getMessage(minLength), getStringValue(message)), msg);
                            if (checkAll) {
                                notValidException.addError(msg.getField(), mg);
                            } else {
                                throw new NotValidException(mg);
                            }
                        }
                    }

                    @Override
                    public void onBlank() throws NotValidException {
                    }
                });
            }

            Phone phone = field.getAnnotation(Phone.class);
            Email email = field.getAnnotation(Email.class);
            Tel tel = field.getAnnotation(Tel.class);
            QQ qq = field.getAnnotation(QQ.class);


            // 所有的需要正则执行的都在这里
            checkAnnoBehind(ValidRegExp.class, new CheckBlankCallBack2() {

                @Override
                public void onNotBlank(String value, Annotation annotation) throws NotValidException {
                    msg.setAnnoValue(annotation);
                    String mg = format(getNotNull(getMessage(invalidRegExp), getStringValue(message)), msg);
                    try {
                        Object fieldVa = field.get(value);
                        if (fieldVa instanceof String) {
                            if (((String) fieldVa).matches(value))
                                return;
                        } else {
                            return;
                        }
                    } catch (IllegalArgumentException | IllegalAccessException e) {

                    }
                    if (checkAll) {
                        notValidException.addError(msg.getField(), mg);
                    } else {
                        throw new NotValidException(mg);
                    }
                }

                @Override
                public void onBlank() throws NotValidException {

                }
            }, email, phone, tel, qq);


            // 调用反射调用的方法
            ValidateMethod validateMethod = field.getAnnotation(ValidateMethod.class);
            if (exist(validateMethod)) {
                Class<? extends Checkable> methodClass = validateMethod.value();
                try {
                    methodClass.newInstance().check(fieldValue);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
                    if (log.isDebugEnabled()) {
                        log.debug("调用错误", e);
                    }
                } catch (NotValidException e) {
                    msg.setAnnoValue(validateMethod);
                    String mg = format(getNotNull(getMessage(validateMethod), getStringValue(message)), msg);
                    if (checkAll) {
                        notValidException.addError(msg.getField(), mg);
                    } else {
                        throw e.setMessage(mg);
                    }
                }
            }
            // 验证日期
            DatePattern datePattern = field.getAnnotation(DatePattern.class);
            String pattern = getStringValue(datePattern);
            if (!isBlank(pattern)) {
                try {
                    if (fieldValue instanceof String) {
                        try {
                            msg.setAnnoValue(datePattern);
                            stringToDate((String) fieldValue, pattern);
                        } catch (Exception e) {
                            String mg = format(getNotNull(getMessage(datePattern), getStringValue(message)), msg);
                            if (checkAll) {
                                notValidException.addError(msg.getField(), mg);
                            } else {
                                throw new NotValidException(mg);
                            }
                        }
                    }
                } catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.debug("调用错误", e);
                    }
                }

            }
        }
        if (notValidException.hasError())
            throw notValidException;
    }

    /**
     * 日期转换
     */
    public static Date stringToDate(String date, String pattern) throws Exception {
        return new SimpleDateFormat(pattern).parse(date);
    }

    /**
     * 正则替换
     *
     * @param value
     * @param msg
     * @return
     */
    public static String format(String value, Msg msg) {
        Pattern pattern = Pattern.compile("\\{[^\\{\\}]*\\}");
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            String group = matcher.group();
            String key = group.substring(1, group.length() - 1).trim();
            value = value.replace(group, msg.get(key));
        }
        return value;
    }

}


