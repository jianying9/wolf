package com.wolf.framework.reponse;

import com.wolf.framework.config.ResponseState;
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
    private String state = ResponseState.FAILURE;

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

    @Override
    public final void success() {
        this.state = ResponseState.SUCCESS;
    }
    
    @Override
    public final void failure() {
        this.state = ResponseState.FAILURE;
    }

    @Override
    public final void setState(String state) {
        this.state = state;
    }

    @Override
    public final void setError(String error) {
        this.error = error;
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

    private String createResponseMessage() {
        StringBuilder jsonBuilder = new StringBuilder(128);
        jsonBuilder.append("{\"state\":\"").append(this.state)
                .append("\",\"route\":\"").append(this.workerContext.getRoute())
                .append("\",\"data\":").append(this.dataMessage).append("}");
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
}
