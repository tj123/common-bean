package com.github.tj123.bean;


import com.github.tj123.bean.convert.BeanConvertException;
import com.github.tj123.bean.convert.BeanUtil;
import com.github.tj123.bean.validate.impl.NotValidException;
import com.github.tj123.bean.validate.ValidateUtil;

import java.lang.reflect.ParameterizedType;

/**
 * dto 基类
 * Created by TJ on 2016/9/3.
 */
@SuppressWarnings({ "serial", "unchecked" })
public abstract class BaseDto<PO extends BasePo<?>> extends BaseBean implements Dto<PO> {

    /**
     * 转 PO
     *
     * @return
     */
	public PO toPo() throws BeanConvertException {
        return (PO) BeanUtil.convert(this, (Class<?>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    /**
     * 当数据为 null 时 替换为 ""
     *
     * @param <DTO>
     * @return
     */
    public <DTO extends BaseDto<?>> DTO fillNull() {
        DTO dto = (DTO) this;
        BeanUtil.fillNull(dto);
        return dto;
    }

    /**
     * 验证
     *
     * @return
     */
    public <DTO extends BaseDto<?>> DTO validate() throws NotValidException {
        DTO dto = (DTO) this;
        ValidateUtil.validate(dto);
        return dto;
    }

    /**
     * 只验证一個字段
     *
     * @return
     */
    public <DTO extends BaseDto<?>> DTO validate(String fieldName) throws NotValidException {
        DTO dto = (DTO) this;
        ValidateUtil.validate(dto,fieldName);
        return dto;
    }

    /**
     * 只验证多個字段
     *
     * @return
     */
    public <DTO extends BaseDto<?>> DTO validate(String... fieldNames) throws NotValidException {
        DTO dto = (DTO) this;
        ValidateUtil.validate(dto,fieldNames);
        return dto;
    }

    /**
     * 对不为空的进行 trim()
     *
     * @param <DTO>
     * @return
     */
    public <DTO extends BaseDto<?>> DTO trim() {
        DTO dto = (DTO) this;
        BeanUtil.trim(dto);
        return dto;
    }

}
