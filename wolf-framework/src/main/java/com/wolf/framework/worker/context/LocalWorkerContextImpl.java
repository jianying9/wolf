package com.wolf.framework.worker.context;

import java.util.Map;

/**
 *
 * @author aladdin
 */
public class LocalWorkerContextImpl extends AbstractWorkContext {

    private String sid;

    public LocalWorkerContextImpl(String sid, String act, Map<String, String> parameterMap) {
        super(act, parameterMap);
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
}
