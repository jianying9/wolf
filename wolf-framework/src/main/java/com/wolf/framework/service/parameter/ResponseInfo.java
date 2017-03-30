package com.wolf.framework.service.parameter;

import com.wolf.framework.service.parameter.filter.FilterType;
import java.util.List;

/**
 *
 * @author aladdin
 */
public interface ResponseInfo {

    /**
     * 参数名
     *
     * @return
     */
    public String getName();

    /**
     * 数据类型
     *
     * @return
     */
    public ResponseDataType getDataType();
    
    /**
     * 描述
     *
     * @return
     */
    public String getDesc();

    /**
     * 该parameter在输出时过滤行为
     *
     * @return
     */
    public FilterType[] getFilterTypes();

    /**
     * 子参数
     *
     * @return
     */
    public List<ResponseInfo> getChildList();
    
    public void setChildList(List<ResponseInfo> childList);
}
