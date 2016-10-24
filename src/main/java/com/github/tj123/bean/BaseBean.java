package com.github.tj123.bean;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by TJ on 2016/9/3.
 */
abstract class BaseBean implements Bean,Serializable{

    private static Log log = LogFactory.getLog(BaseBean.class);

    /**
     * 生产 UUID
     * @return
     */
    public String getUUID(){
        return Util.getUUID();
    }

    /**
     * 对象转为 map 包含为空字段
     */
    public Map toMap() throws BeanConvertException {
        return toMap(true);
    }


    /**
     * 对象转为 map
     *
     * @param includeNull 是否包含空
     * @return
     */
    public <B extends BaseBean> Map toMap(boolean includeNull) throws BeanConvertException {
        B bean = (B) this;
        Map map = BeanUtil.toMap(bean, includeNull);
        return map;
    }


}
