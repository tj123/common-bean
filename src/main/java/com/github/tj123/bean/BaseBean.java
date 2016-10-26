package com.github.tj123.bean;




import java.io.Serializable;
import java.util.Map;

/**
 * Created by TJ on 2016/9/3.
 */
@SuppressWarnings("serial")
abstract class BaseBean implements Bean,Serializable{

    //private static Log log = LogFactory.getLog(BaseBean.class);

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
    public Map<String,Object> toMap() throws BeanConvertException {
        return toMap(true);
    }


    /**
     * 对象转为 map
     *
     * @param includeNull 是否包含空
     * @return
     */
    public <B extends BaseBean> Map<String, Object> toMap(boolean includeNull) throws BeanConvertException {
        @SuppressWarnings("unchecked")
		B bean = (B) this;
        Map<String,Object> map = BeanUtil.toMap(bean, includeNull);
        return map;
    }


}
