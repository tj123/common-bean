package com.github.tj123.bean;


import com.github.tj123.bean.validate.NotValidException;
import com.github.tj123.bean.validate.ValidateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.ParameterizedType;

/**
 * dto 基类
 * Created by TJ on 2016/9/3.
 */
public abstract class BaseDto<PO extends BasePo> extends BaseBean implements Dto {

    private Log log = LogFactory.getLog(BaseDto.class);

    /**
     * 转 PO
     *
     * @return
     */
    public PO toPo() throws BeanConvertException {
        return (PO) BeanUtil.convert(this, (Class) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    /**
     * 当数据为 null 时 替换为 ""
     *
     * @param <DTO>
     * @return
     */
    public <DTO extends BaseDto> DTO fillNull() {
        DTO dto = (DTO) this;
        BeanUtil.fillNull(dto);
        return dto;
    }

    /**
     * 验证
     *
     * @return
     */
    public <DTO extends BaseDto> DTO validate() throws NotValidException {
        ValidateUtil.validate(this);
        return (DTO) this;
    }

}
