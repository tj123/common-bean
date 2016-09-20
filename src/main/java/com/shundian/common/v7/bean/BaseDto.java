package com.shundian.common.v7.bean;


import com.shundian.common.v7.bean.validate.NotValidException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * dto 基类
 * Created by TJ on 2016/9/3.
 */
public abstract class BaseDto<PO extends BasePo> extends BaseBean {

    private Log log = LogFactory.getLog(BaseDto.class);

    /**
     * 转 PO
     *
     * @return
     */
    public PO toPo() throws BeanConvertException {
//        return (PO) BeanUtil.toPo(this, (Class) ((ParameterizedType) getClass()
//                .getGenericSuperclass()).getActualTypeArguments()[0]);
        return null;
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
        DTO dto = (DTO) this;
        Class<? extends BaseDto> clazz = dto.getClass();

        return dto;
    }

}
