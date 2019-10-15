package com.wolf.framework.service.parameter;

import java.util.List;

/**
 *
 * @author jianying9
 */
public interface RequestInfo {
    
    /**
     * 参数名
     * @return 
     */
    public String getName();

    /**
     * 是否必填,默认ture
     * @return 
     */
    public boolean isRequired();
    
    /**
     * 非必填是,忽略空字符串数据
     * @return 
     */
    public boolean isIgnoreEmpty();

    /**
     * 数据类型
     *
     * @return
     */
    public RequestDataType getDataType();
    
    /**
     * 最大
     * @return 
     */
    public long getMax();
    
    /**
     * 最小
     * @return 
     */
    public long getMin();
    
    /**
     * 自定义文本
     * @return 
     */
    public String getText();
    
    /**
     * 子参数
     * @return 
     */
    public List<RequestInfo> getChildList();
    
    public void setChildList(List<RequestInfo> childList);
    
    /**
     * 描述
     *
     * @return
     */
    public String getDesc();
    
}
