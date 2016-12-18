package com.wolf.framework.service.request;

/**
 *  普通类别请求
 * @author jianying9
 */
public interface ListRequest extends ObjectRequest {

    public long getNextIndex();
    
    public int getNextSize();
}
