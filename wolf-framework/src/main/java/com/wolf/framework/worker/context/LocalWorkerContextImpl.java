package com.wolf.framework.worker.context;

import com.wolf.framework.worker.ServiceWorker;

/**
 *
 * @author aladdin
 */
public class LocalWorkerContextImpl extends AbstractWorkContext {

    private String sid;

    public LocalWorkerContextImpl(String sid, String act, ServiceWorker serviceWorker) {
        super(act, serviceWorker);
        this.sid = sid;
    }

    @Override
    public String getSessionId() {
        return this.sid;
    }

    @Override
    public void saveNewSession(String sid) {
        this.sid = sid;
    }

    @Override
    public void removeSession() {
        this.sid = null;
    }

    @Override
    public void closeSession(String sid) {
    }
}
