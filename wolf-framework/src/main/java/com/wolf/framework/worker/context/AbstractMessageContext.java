package com.wolf.framework.worker.context;

import com.wolf.framework.comet.CometContext;
import com.wolf.framework.config.ResponseCode;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public abstract class AbstractMessageContext  {

    protected final WorkerContext workerContext;
    private final Map<String, String> parameterMap = new HashMap<String, String>(8, 1);
    //message
    private String error = "";
    private String responseMessage = "";
    protected String state = ResponseCode.FAILURE;
    protected final String[] returnParameter;
    protected final Map<String, ResponseParameterHandler> parameterHandlerMap;
    //session
    protected String newSid;

    public AbstractMessageContext(WorkerContext workerContext, String[] returnParameter, Map<String, ResponseParameterHandler> parameterHandlerMap) {
        this.workerContext = workerContext;
        this.returnParameter = returnParameter;
        this.parameterHandlerMap = parameterHandlerMap;
    }

    public abstract String createMessage();

    public final WorkerContext getWorkerContext() {
        return this.workerContext;
    }

    public final String getParameter(String name) {
        return this.parameterMap.get(name);
    }

    public final Map<String, String> getParameterMap() {
        return this.parameterMap;
    }

    public final String getState() {
        return this.state;
    }

    public final void denied() {
        this.state = ResponseCode.DENIED;
    }

    public final void invalid() {
        this.state = ResponseCode.INVALID;
    }

    public final void unlogin() {
        this.state = ResponseCode.UNLOGIN;
    }

    public final void success() {
        this.state = ResponseCode.SUCCESS;
    }

    public final void setState(String state) {
        this.state = state;
    }

    public final void setError(String error) {
        this.error = error;
    }

    public void setNewSessionId(String sid) {
        this.newSid = sid;
    }

    public final ApplicationContext getApplicationContext() {
        return ApplicationContext.CONTEXT;
    }

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

    public final String getResponseMessage() {
        if (this.responseMessage.isEmpty()) {
            this.responseMessage = this.createMessage();
        }
        return this.responseMessage;
    }

    public final String getResponseMessage(boolean useCache) {
        String result;
        if (useCache) {
            result = this.getResponseMessage();
        } else {
            this.responseMessage = this.createMessage();
            result = this.responseMessage;
        }
        return result;
    }

    public void putParameter(String name, String value) {
        this.parameterMap.put(name, value);
    }

    public boolean push(String sid, String responseMessage) {
        CometContext cometContext = this.getApplicationContext().getCometContext();
        return cometContext.push(sid, responseMessage);
    }
}