package com.wolf.framework.worker.context;

import com.wolf.framework.session.Session;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public class LocalMessageContextImpl extends AbstractMessageContext implements FrameworkMessageContext {

    private final Session session;

    public LocalMessageContextImpl(Session session, String act, Map<String, String> parameterMap) {
        super(act, parameterMap);
        this.session = session;
    }

    @Override
    public Session getSession() {
        return this.session;
    }

    @Override
    public void sendMessage() {
    }

    @Override
    public void broadcastMessage() {
    }

    @Override
    public void close() {
    }

    @Override
    public void saveNewSession() {
    }

    @Override
    public void removeSession() {
    }

    @Override
    public boolean isOnline(String userId) {
        return false;
    }

    public String getResponseMessage() {
        return this.responseMessage;
    }

    @Override
    public void sendSystemMessage(String message) {
    }
}
