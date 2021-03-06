package com.github.tj123.bean;


import com.github.tj123.bean.convert.BeanConvertException;
import com.github.tj123.bean.convert.BeanUtil;

import java.lang.reflect.ParameterizedType;

/**
 * po 基类
 * Created by TJ on 2016/9/3.
 */
@SuppressWarnings({ "serial", "unchecked" })
public abstract class BasePo<DTO extends BaseDto<?>> extends BaseBean implements Po<DTO> {

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
