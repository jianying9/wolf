package com.wolf.framework.service.request;

/**
 *  普通类别请求
 * @author jianying9
 */
public interface ListServiceRequest extends ServiceRequest {

    public String getNextIndex();
    
    public int getNextSize();
}
