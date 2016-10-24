package com.wolf.framework.reponse;

import com.wolf.framework.config.ResponseCode;
import com.wolf.framework.service.SessionHandleType;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.context.WorkerContext;

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
    private String code = ResponseCode.FAILURE;
    private String newSessionId = null;

    public ResponseImpl(WorkerContext workerContext) {
        this.workerContext = workerContext;
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
        this.code = ResponseCode.DENIED;
    }

    @Override
    public final void invalid() {
        this.code = ResponseCode.INVALID;
    }

    @Override
    public final void unlogin() {
        this.code = ResponseCode.UNLOGIN;
    }

    @Override
    public final void success() {
        this.code = ResponseCode.SUCCESS;
    }

    @Override
    public final void failure() {
        this.code = ResponseCode.FAILURE;
    }

    @Override
    public final void setCode(String code) {
        this.code = code;
    }

    @Override
    public final void setError(String error) {
        this.error = error;
    }

    @Override
    public final String createErrorMessage() {
        StringBuilder jsonBuilder = new StringBuilder(128);
        jsonBuilder.append("{\"code\":\"").append(this.code)
                .append("\",\"route\":\"").append(this.workerContext.getRoute())
                .append("\",\"error\":\"").append(this.error).append("\"}");
        this.responseMessage = jsonBuilder.toString();
        return this.responseMessage;
    }

    private String createResponseMessage() {
        ServiceWorker serviceWorker = this.workerContext.getServiceWorker();
        StringBuilder jsonBuilder = new StringBuilder(128);
        jsonBuilder.append("{\"code\":\"").append(this.code)
                .append("\",\"route\":\"").append(this.workerContext.getRoute());
        if(this.newSessionId != null && serviceWorker.getSessionHandleType() == SessionHandleType.SAVE) {
            jsonBuilder.append("\",\"sid\":\"").append(this.newSessionId);
        }
        jsonBuilder.append("\",\"data\":").append(this.dataMessage).append("}");
        this.responseMessage = jsonBuilder.toString();
        return this.responseMessage;
    }

    @Override
    public final String getResponseMessage() {
        if (this.responseMessage.isEmpty()) {
            this.createResponseMessage();
        }
        return this.responseMessage;
    }

    @Override
    public final String getResponseMessage(boolean useCache) {
        String result;
        if (useCache) {
            result = this.getResponseMessage();
        } else {
            result = this.createResponseMessage();
        }
        return result;
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
}
