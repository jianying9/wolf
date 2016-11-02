package com.wolf.framework.reponse;

import com.wolf.framework.comet.CometContext;
import com.wolf.framework.config.ResponseCodeConfig;
import com.wolf.framework.service.ResponseCode;
import com.wolf.framework.service.SessionHandleType;
import com.wolf.framework.service.context.ServiceContext;
import com.wolf.framework.worker.context.WorkerContext;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author jianying9
 */
public class ResponseImpl implements WorkerResponse {

    private final WorkerContext workerContext;
    //message
    private String error = "";
    private String dataMessage = "{}";
    private String responseMessage = "";
    private String code = ResponseCodeConfig.SUCCESS;
    private String newSessionId = null;
    private String callback = null;
    private final Set<String> customCodeSet;

    public ResponseImpl(WorkerContext workerContext) {
        this.workerContext = workerContext;
        this.callback = workerContext.getParameter("callback");
        ServiceContext serviceContext = this.workerContext.getServiceWorker().getServiceContext();
        ResponseCode[] responseCodes = serviceContext.responseCodes();
        this.customCodeSet = new HashSet<>(responseCodes.length);
        for (ResponseCode responseCode : responseCodes) {
            this.customCodeSet.add(responseCode.code());
        }
    }

    public final WorkerContext getWorkerContext() {
        return this.workerContext;
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
        if(this.customCodeSet.contains(code)) {
            this.code = code;
        } else {
            this.code = ResponseCodeConfig.UNKNOWN;
        }
    }

    @Override
    public final void setError(String error) {
        this.error = error;
    }

    private void createResponseMessage() {
        ServiceContext serviceContext = this.workerContext.getServiceWorker().getServiceContext();
        StringBuilder jsonBuilder = new StringBuilder(128);
        jsonBuilder.append("{\"code\":\"").append(this.code)
                .append("\",\"route\":\"").append(this.workerContext.getRoute());
        if (this.newSessionId != null && serviceContext.sessionHandleType() == SessionHandleType.SAVE) {
            jsonBuilder.append("\",\"sid\":\"").append(this.newSessionId);
        }
        if (this.error.isEmpty() == false) {
            jsonBuilder.append("\",\"error\":\"").append(this.error);
        }
        if (this.callback != null) {
            jsonBuilder.append("\",\"callback\":\"").append(this.callback);
        }
        jsonBuilder.append("\",\"data\":").append(this.dataMessage).append("}");
        this.responseMessage = jsonBuilder.toString();
    }


    @Override
    public final String getResponseMessage() {
        this.createResponseMessage();
        return this.responseMessage;
    }

    @Override
    public String getDataMessage() {
        return this.dataMessage;
    }

    @Override
    public void setDataMessage(String dataMessage) {
        this.dataMessage = dataMessage;
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
    public boolean push(String sid, String responseMessage) {
        CometContext cometContext = this.workerContext.getApplicationContext().getCometContext();
        return cometContext.push(sid, responseMessage);
    }

    @Override
    public boolean asyncPush(String sid, String responseMessage) {
        CometContext cometContext = this.workerContext.getApplicationContext().getCometContext();
        return cometContext.asyncPush(sid, responseMessage);
    }

    @Override
    public void closeOtherSession(String otherSid) {
        this.workerContext.closeSession(otherSid);
    }
}
