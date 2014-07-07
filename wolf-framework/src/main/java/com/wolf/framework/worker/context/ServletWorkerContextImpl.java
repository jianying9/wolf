package com.wolf.framework.worker.context;

import com.wolf.framework.session.Session;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public class ServletWorkerContextImpl extends AbstractWorkContext {

    private final Session session;
    private final Map<String, Session> sessionMap;

    public ServletWorkerContextImpl(Map<String, Session> sessionMap, Session session, String act, Map<String, String> parameterMap) {
        super(act, parameterMap);
        this.session = session;
        this.sessionMap = sessionMap;
    }

    @Override
    public Session getSession() {
        return this.session;
    }

    @Override
    public void sendMessage(String message) {
    }

    @Override
    public void close() {
    }

    @Override
    public void saveNewSession(Session newSession) {
        if (newSession != null) {
            String newSid = newSession.getSid();
            this.sessionMap.put(newSid, newSession);
        }
    }

    @Override
    public void removeSession() {
        if (this.session != null) {
            this.sessionMap.remove(this.session.getSid());
        }
    }
}
