package com.wolf.framework.worker.context;

import com.wolf.framework.session.Session;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public class LocalWorkerContextImpl extends AbstractWorkContext {

    private final Session session;

    public LocalWorkerContextImpl(Session session, String act, Map<String, String> parameterMap) {
        super(act, parameterMap);
        this.session = session;
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
    public void saveNewSession(Session session) {
    }

    @Override
    public void removeSession() {
    }
}
