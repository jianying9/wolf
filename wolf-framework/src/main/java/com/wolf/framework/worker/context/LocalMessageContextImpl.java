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

    public Session getSession() {
        return this.session;
    }

    public void sendMessage() {
    }

    public void broadcastMessage() {
    }

    public void close() {
    }

    public void saveNewSession() {
    }

    public void removeSession() {
    }

    public boolean isOnline(String userId) {
        return false;
    }
    
    public String getResponseMessage() {
        return this.responseMessage;
    }
}
