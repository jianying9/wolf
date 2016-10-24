package com.wolf.framework.service.response;

import com.wolf.framework.dao.Entity;
import java.util.Map;

/**
 *
 * @author jianying9
 * @param <T>
 */
public interface BaseServiceResponse<T extends Entity> {
    
    public String getCode();
    
    public void setCode(String code);
    
    public void success();
    
    public void failure();
    
    public String getDataMessage();
    
    public void setNewSessionId(String sid);
    
    public String getNewSessionId();
    
    public String getResponseMessage();

    public boolean push(String sid);
    
    public boolean push(String sid, String responseMessage);
}
