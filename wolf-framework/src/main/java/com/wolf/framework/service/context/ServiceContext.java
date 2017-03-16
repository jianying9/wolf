package com.wolf.framework.service.context;

import com.wolf.framework.service.SessionHandleType;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.RequestParameterHandler;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import java.util.Map;
import com.wolf.framework.service.ResponseCode;
import com.wolf.framework.service.parameter.PushConfig;
import com.wolf.framework.service.parameter.PushHandler;

/**
 *
 * @author jianying9
 */
public interface ServiceContext {
    
    public String route();
    
    public boolean requireTransaction();
    
    public SessionHandleType sessionHandleType();
    
    public boolean validateSession();
    
    public boolean validateSecurity();
    
    public boolean page();
    
    public boolean isList();
    
    public String desc();
    
    public String[] requiredParameter();
    
    public String[] unrequiredParameter();
    
    public Map<String, RequestParameterHandler> requestParameterHandlerMap();
    
    public String[] returnParameter();
    
    public Map<String, ResponseParameterHandler> responseParameterHandlerMap();
    
    public RequestConfig[] requestConfigs();
    
    public ResponseConfig[] responseConfigs();
    
    public ResponseCode[] responseCodes();
    
    public PushConfig[] pushConfigs();
    
    public Map<String, PushHandler> pushHandlerMap();
    
    public boolean hasAsyncResponse();
}
