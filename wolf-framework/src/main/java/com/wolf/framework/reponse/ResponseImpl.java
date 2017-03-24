package com.wolf.framework.reponse;

import com.wolf.framework.config.ResponseCodeConfig;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.service.ResponseCode;
import com.wolf.framework.service.SessionHandleType;
import com.wolf.framework.service.context.ServiceContext;
import com.wolf.framework.service.parameter.PushHandler;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.utils.EntityUtils;
import com.wolf.framework.utils.SecurityUtils;
import com.wolf.framework.worker.context.WorkerContext;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class ResponseImpl<T extends Entity> implements WorkerResponse<T> {

    private final WorkerContext workerContext;
    //message
    private String error = "";
    private String code = ResponseCodeConfig.SUCCESS;
    private String newSessionId = null;
    private final Set<String> customCodeSet;
    private Map<String, Object> dataMap = Collections.EMPTY_MAP;

    public ResponseImpl(WorkerContext workerContext) {
        this.workerContext = workerContext;
        ServiceContext serviceContext = this.workerContext.getServiceWorker().getServiceContext();
        ResponseCode[] responseCodes = serviceContext.responseCodes();
        this.customCodeSet = new HashSet<>(responseCodes.length);
        for (ResponseCode responseCode : responseCodes) {
            this.customCodeSet.add(responseCode.code());
        }
    }

    @Override
    public final String getCode() {
        return this.code;
    }

    @Override
    public final void denied() {
        this.code = ResponseCodeConfig.DENIED;
    }

    @Override
    public final void invalid() {
        this.code = ResponseCodeConfig.INVALID;
    }

    @Override
    public final void unlogin() {
        this.code = ResponseCodeConfig.UNLOGIN;
    }

    @Override
    public final void timeout() {
        this.code = ResponseCodeConfig.TIMEOUT;
    }

    @Override
    public final void success() {
        this.code = ResponseCodeConfig.SUCCESS;
    }

    @Override
    public final void exception() {
        this.code = ResponseCodeConfig.EXCEPTION;
    }

    @Override
    public final void unsupport() {
        this.code = ResponseCodeConfig.UNKNOWN;
    }

    @Override
    public final void setCode(String code) {
        if (this.customCodeSet.contains(code)) {
            this.code = code;
        } else {
            this.code = ResponseCodeConfig.UNKNOWN;
        }
    }

    @Override
    public final void setError(String error) {
        this.error = error;
    }

    @Override
    public final String getResponseMessage() {
        String responseMsg = "{}";
        Map<String, Object> responseMap = new HashMap(8, 1);
        //核心返回数据
        responseMap.put("code", this.code);
        responseMap.put("route", this.workerContext.getRoute());
        responseMap.put("data", this.dataMap);
        //md5判断本次返回数据是否没有变化
        String md5 = workerContext.getMd5();
        ObjectMapper mapper = new ObjectMapper();
        if (md5 != null) {
            String thisData = "{}";
            try {
                thisData = mapper.writeValueAsString(responseMap);
            } catch (IOException ex) {
            }
            //判断数据是否有变化
            String newMd5 = SecurityUtils.encryptByMd5(thisData);
            if (md5.equals(newMd5)) {
                //数据没有变化
                responseMap.put("code", ResponseCodeConfig.UNMODIFYED);
                responseMap.put("data", Collections.EMPTY_MAP);
            } else {
                md5 = newMd5;
            }
            responseMap.put("md5", md5);
        }
        //
        ServiceContext serviceContext = this.workerContext.getServiceWorker().getServiceContext();
        if (this.newSessionId != null && serviceContext.sessionHandleType() == SessionHandleType.SAVE) {
            responseMap.put("sid", this.newSessionId);
        }
        //
        if (this.error.isEmpty() == false) {
            responseMap.put("error", this.error);
        }
        String callback = workerContext.getCallback();
        if (callback != null) {
            responseMap.put("callback", callback);
        }
        try {
            responseMsg = mapper.writeValueAsString(responseMap);
        } catch (IOException ex) {
        }
        return responseMsg;
    }

    @Override
    public String getNewSessionId() {
        return this.newSessionId;
    }

    @Override
    public void setNewSessionId(String newSessionId) {
        this.newSessionId = newSessionId;
    }

    @Override
    public void closeOtherSession(String otherSid) {
        this.workerContext.closeSession(otherSid);
    }

    protected final Map<String, Object> checkAndFilterDataMap(Map<String, Object> paraMap) {
        Map<String, Object> resultMap = null;
        if (paraMap != null) {
            ServiceContext serviceContext = this.workerContext.getServiceWorker().getServiceContext();
            String[] returnParameter = serviceContext.returnParameter();
            Map<String, ResponseParameterHandler> parameterHandlerMap = serviceContext.responseParameterHandlerMap();
            Object paraValue;
            ResponseParameterHandler responseParameterHandler;
            resultMap = new HashMap(paraMap.size(), 1);
            //过滤
            for (String paraName : returnParameter) {
                paraValue = paraMap.get(paraName);
                if (paraValue != null) {
                    responseParameterHandler = parameterHandlerMap.get(paraName);
                    paraValue = responseParameterHandler.getResponseValue(paraValue);
                    resultMap.put(paraName, paraValue);
                }
            }
        }
        return resultMap;
    }

    @Override
    public void setEntity(T t) {
        Map<String, Object> tMap = EntityUtils.getMap(t);
        this.setDataMap(tMap);
    }

    @Override
    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = this.checkAndFilterDataMap(dataMap);
    }

    @Override
    public void setData(String name, Object value) {
        Map<String, Object> newDataMap = new HashMap<>(2, 1);
        newDataMap.put(name, value);
        //检测并过滤响应参数
        this.dataMap = this.checkAndFilterDataMap(newDataMap);
    }

    @Override
    public PushResponse getPushResponse(String route) {
        PushResponse pushResponse = null;
        ServiceContext serviceContext = this.workerContext.getServiceWorker().getServiceContext();
        Map<String, PushHandler> pushHandlerMap = serviceContext.pushHandlerMap();
        PushHandler pushHandler = pushHandlerMap.get(route);
        if(pushHandler != null) {
            pushResponse = new PushResponseImpl(pushHandler);
        }
        return pushResponse;
    }
}
