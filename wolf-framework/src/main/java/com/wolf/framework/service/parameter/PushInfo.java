package com.wolf.framework.service.parameter;

import java.util.List;

/**
 *
 * @author jianying9
 */
public interface PushInfo {
    
/**
     * 路由地址
     *
     * @return route
     */
    public String getRoute();
    
    public String getDesc();
    
    /**
     * 返回的参数
     *
     * @return String[]
     */
    public List<ResponseInfo> getResponseInfoList();
    
    public void setResponseInfoList(List<ResponseInfo> responseInfoList);
    
    public void addService(String route);
    
    public List<String> getServiceList();
    
}
