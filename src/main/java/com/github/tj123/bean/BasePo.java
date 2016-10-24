package com.github.tj123.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * po 基类
 * Created by TJ on 2016/9/3.
 */
public abstract class BasePo<DTO extends BaseDto> extends BaseBean implements Po {

    private Log log = LogFactory.getLog(BasePo.class);

    /**
     * 转 DTO
     *
     * @return
     */
    public DTO toDto() throws BeanConvertException {
//        return (DTO) BeanUtil.toDto(this, (Class) ((ParameterizedType) getClass()
//                .getGenericSuperclass()).getActualTypeArguments()[0]);
        return null;
    }


}
