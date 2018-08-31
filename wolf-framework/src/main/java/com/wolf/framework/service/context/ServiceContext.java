package com.wolf.framework.service.context;

import com.wolf.framework.service.SessionHandleType;
import java.util.Map;
import com.wolf.framework.service.ResponseCode;
import com.wolf.framework.service.parameter.PushHandler;
import com.wolf.framework.service.parameter.PushInfo;
import java.util.List;
import com.wolf.framework.service.parameter.RequestInfo;
import com.wolf.framework.service.parameter.ResponseInfo;
import com.wolf.framework.service.parameter.RequestHandler;
import com.wolf.framework.service.parameter.ResponseHandler;

/**
 *
 * @author jianying9
 */
public interface ServiceContext {
    
    public String route();
    
    public String group();
    
    public boolean requireTransaction();
    
    public SessionHandleType sessionHandleType();
    
    public boolean validateSession();
    
    public boolean validateSecurity();
    
    public String desc();
    
    public String[] requiredParameter();
    
    public String[] unrequiredParameter();
    
    public Map<String, RequestHandler> requestParameterHandlerMap();
    
    public boolean isResponse();
    
    public String[] returnParameter();
    
    public Map<String, ResponseHandler> responseParameterHandlerMap();
    
    public List<RequestInfo> requestConfigs();
    
    public List<ResponseInfo> responseConfigs();
    
    public ResponseCode[] responseCodes();
    
    public boolean isResponseText();
    
    public List<PushInfo> pushConfigs();
    
    public Map<String, PushHandler> pushHandlerMap();
    
    public boolean hasAsyncResponse();
    
    public PushHandler getPushHandler(String route);
}
