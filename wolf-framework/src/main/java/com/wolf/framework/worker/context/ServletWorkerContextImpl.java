package com.wolf.framework.worker.context;

import com.wolf.framework.servlet.ServiceServlet;
import com.wolf.framework.worker.ServiceWorker;

/**
 *
 * @author aladdin
 */
public class ServletWorkerContextImpl extends AbstractWorkContext {
    
    private final ServiceServlet serviceServlet;

    private String sid;

    public ServletWorkerContextImpl(ServiceServlet serviceServlet, String sid, String act, ServiceWorker serviceWorker) {
        super(act, serviceWorker);
        this.sid = sid;
        this.serviceServlet = serviceServlet;
    }

    @Override
    public String getSessionId() {
        return this.sid;
    }

    @Override
    public void saveNewSession(String sid) {
        this.sid = sid;
        this.serviceServlet.saveNewSession(sid);
    }

    @Override
    public void removeSession() {
        this.sid = null;
        this.serviceServlet.removeSession(this.sid);
    }

    @Override
    public void closeSession(String sid) {
        this.serviceServlet.removeSession(sid);
    }
}
