package com.github.tj123.bean;


import java.lang.reflect.ParameterizedType;

/**
 * po 基类
 * Created by TJ on 2016/9/3.
 */
@SuppressWarnings({ "serial", "unchecked" })
public abstract class BasePo<DTO extends BaseDto<?>> extends BaseBean implements Po<DTO> {

    //private Log log = LogFactory.getLog(BasePo.class);

    /**
     * 转 DTO
     *
     * @return
     */
    public DTO toDto() throws BeanConvertException {
        return (DTO) BeanUtil.convert(this, (Class<?>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }


}
