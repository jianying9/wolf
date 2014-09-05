package com.wolf.framework.worker.context;

import com.wolf.framework.comet.CometContext;
import com.wolf.framework.config.DefaultResponseStates;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.session.Session;
import com.wolf.framework.session.SessionImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public abstract class AbstractMessageContext implements FrameworkMessageContext {

    protected final WorkerContext workerContext;
    private final Map<String, String> parameterMap = new HashMap<String, String>(8, 1);
    //message
    private String error = "";
    private String responseMessage = "";
    protected String state = DefaultResponseStates.FAILURE;
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

    @Override
    public final WorkerContext getWorkerContext() {
        return this.workerContext;
    }

    @Override
    public final String getParameter(String name) {
        return this.parameterMap.get(name);
    }

    @Override
    public final Map<String, String> getParameterMap() {
        return this.parameterMap;
    }

    @Override
    public final String getState() {
        return this.state;
    }

    @Override
    public final void denied() {
        this.state = DefaultResponseStates.DENIED;
    }

    @Override
    public final void invalid() {
        this.state = DefaultResponseStates.INVALID;
    }

    @Override
    public final void unlogin() {
        this.state = DefaultResponseStates.UNLOGIN;
    }

    @Override
    public final void success() {
        this.state = DefaultResponseStates.SUCCESS;
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
    public final <T extends Entity> void setEntityData(T t) {
        Map<String, String> entityMap = t.toMap();
        this.setMapData(entityMap);
    }

    @Override
    public final <T extends Entity> void setEntityListData(List<T> tList) {
        List<Map<String, String>> entityMapList = new ArrayList<Map<String, String>>(tList.size());
        for (T t : tList) {
            entityMapList.add(t.toMap());
        }
        this.setMapListData(entityMapList);
    }

    @Override
    public final void setNewSession(Session session) {
        if(session != null) {
            this.newSid = session.getSid();
        }
    }

    @Override
    public void setNewSessionId(String sid) {
        this.newSid = sid;
    }

    @Override
    public final ApplicationContext getApplicationContext() {
        return ApplicationContext.CONTEXT;
    }

    @Override
    public final String createErrorMessage() {
        StringBuilder jsonBuilder = new StringBuilder(128);
        jsonBuilder.append("{\"state\":\"").append(this.state)
                .append("\",\"act\":\"").append(this.workerContext.getAct())
                .append("\",\"error\":\"").append(this.error).append("\"}");
        this.responseMessage = jsonBuilder.toString();
        return this.responseMessage;
    }

    @Override
    public final Session getSession() {
        String sid = this.workerContext.getSessionId();
        if (sid == null) {
            return null;
        } else {
            return new SessionImpl(sid);
        }
    }

    @Override
    public final String getSessionId() {
        return this.workerContext.getSessionId();
    }

    @Override
    public final String getNewSessionId() {
        return this.newSid;
    }

    @Override
    public final String getResponseMessage() {
        if (this.responseMessage.isEmpty()) {
            this.responseMessage = this.createMessage();
        }
        return this.responseMessage;
    }

    @Override
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

    @Override
    public void putParameter(String name, String value) {
        this.parameterMap.put(name, value);
    }

    @Override
    public boolean push(String sid, String responseMessage) {
        CometContext cometContext = this.getApplicationContext().getCometContext();
        return cometContext.push(sid, responseMessage);
    }
}