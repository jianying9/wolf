package com.wolf.framework.worker.context;

import com.wolf.framework.comet.CometContext;
import com.wolf.framework.config.DefaultResponseFlags;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.session.Session;
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
    protected String flag = DefaultResponseFlags.FAILURE;
    protected final String[] returnParameter;
    protected final Map<String, ResponseParameterHandler> parameterHandlerMap;
    //session
    private Session newSession = null;

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
    public final void invalid() {
        this.flag = DefaultResponseFlags.INVALID;
    }

    @Override
    public final void unlogin() {
        this.flag = DefaultResponseFlags.UNLOGIN;
    }

    @Override
    public final void success() {
        this.flag = DefaultResponseFlags.SUCCESS;
    }

    @Override
    public final void setFlag(String flag) {
        this.flag = flag;
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
        this.newSession = session;
    }

    @Override
    public final ApplicationContext getApplicationContext() {
        return ApplicationContext.CONTEXT;
    }

    @Override
    public final String createErrorMessage() {
        StringBuilder jsonBuilder = new StringBuilder(128);
        jsonBuilder.append("{\"flag\":\"").append(this.flag)
                .append("\",\"act\":\"").append(this.workerContext.getAct())
                .append("\",\"error\":\"").append(this.error).append("\"}");
        this.responseMessage = jsonBuilder.toString();
        return this.responseMessage;
    }

    @Override
    public final Session getSession() {
        return this.workerContext.getSession();
    }

    @Override
    public final Session getNewSession() {
        return this.newSession;
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
    public void push(String sid, String responseMessage) {
        CometContext cometContext = this.getApplicationContext().getCometContext();
        cometContext.push(sid, responseMessage);
    }
}