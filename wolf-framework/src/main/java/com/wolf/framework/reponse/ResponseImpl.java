package com.wolf.framework.reponse;

import com.wolf.framework.config.ResponseState;
import com.wolf.framework.worker.context.WorkerContext;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class ResponseImpl  implements WorkerResponse {

    private final WorkerContext workerContext;
    private final Map<String, String> parameterMap = new HashMap<String, String>(8, 1);
    //message
    private String error = "";
    private String responseMessage = "";
    private String state = ResponseState.FAILURE;
    //session
    protected String newSid;

    public ResponseImpl(WorkerContext workerContext) {
        this.workerContext = workerContext;
    }

    public final WorkerContext getWorkerContext() {
        return this.workerContext;
    }

    @Override
    public final String getState() {
        return this.state;
    }

    @Override
    public final void denied() {
        this.state = ResponseState.DENIED;
    }

    @Override
    public final void invalid() {
        this.state = ResponseState.INVALID;
    }

    @Override
    public final void unlogin() {
        this.state = ResponseState.UNLOGIN;
    }

    public final void success() {
        this.state = ResponseState.SUCCESS;
    }

    @Override
    public final void setState(String state) {
        this.state = state;
    }

    @Override
    public final void setError(String error) {
        this.error = error;
    }

    public void setNewSessionId(String sid) {
        this.newSid = sid;
    }

    @Override
    public final String createErrorMessage() {
        StringBuilder jsonBuilder = new StringBuilder(128);
        jsonBuilder.append("{\"state\":\"").append(this.state)
                .append("\",\"route\":\"").append(this.workerContext.getRoute())
                .append("\",\"error\":\"").append(this.error).append("\"}");
        this.responseMessage = jsonBuilder.toString();
        return this.responseMessage;
    }

    public final String getSessionId() {
        return this.workerContext.getSessionId();
    }

    public final String getNewSessionId() {
        return this.newSid;
    }

    @Override
    public final String getResponseMessage() {
        if (this.responseMessage.isEmpty()) {
            this.responseMessage = this.workerContext.getServiceWorker().createResponseMessage(this);
        }
        return this.responseMessage;
    }

    @Override
    public final String getResponseMessage(boolean useCache) {
        String result;
        if (useCache) {
            result = this.getResponseMessage();
        } else {
            this.responseMessage = this.workerContext.getServiceWorker().createResponseMessage(this);
            result = this.responseMessage;
        }
        return result;
    }
}