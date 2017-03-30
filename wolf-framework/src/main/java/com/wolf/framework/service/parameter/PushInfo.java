package com.wolf.framework.service.parameter;

import com.wolf.framework.service.ResponseCode;
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
    
    /**
     * 返回的参数
     *
     * @return String[]
     */
    public List<ResponseInfo> getResponseInfoList();
    
    public void setResponseInfoList(List<ResponseInfo> responseInfoList);
    
    /**
     * 返回标志描述
     *
     * @return
     */
    public ResponseCode[] responseCodes();
    
}
