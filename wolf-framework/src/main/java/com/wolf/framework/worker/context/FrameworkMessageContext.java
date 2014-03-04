package com.wolf.framework.worker.context;

import com.wolf.framework.service.parameter.ParameterHandler;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface FrameworkMessageContext extends MessageContext {

    public void putParameter(String name, String value);

    public void removeParameter(String name);

    public String getAct();

    public void setPageIndex(int pageIndex);

    public void setPageSize(int pageSize);

    public void invalid();

    public void unlogin();

    public void setError(String error);

    public void sendMessage();

    public void broadcastMessage();

    public void close();

    public void saveNewSession();

    public void removeSession();

    public void createErrorMessage();

    public void createMessage(String[] parameterNames, Map<String, ParameterHandler> parameterHandlerMap);

    public void createPageMessage(String[] parameterNames, Map<String, ParameterHandler> parameterHandlerMap);

    public void sendSystemMessage(String message);
}
