package com.wolf.framework.worker.context;

import com.wolf.framework.comet.CometContext;
import com.wolf.framework.session.Session;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public class LocalMessageContextImpl extends AbstractMessageContext implements FrameworkMessageContext {

    private final Session session;

    public LocalMessageContextImpl(Session session, String act, Map<String, String> parameterMap, CometContext cometContext) {
        super(act, parameterMap, cometContext);
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
    public void close() {
    }

    @Override
    public void saveNewSession() {
    }

    @Override
    public void removeSession() {
    }

    public String getResponseMessage() {
        return this.responseMessage;
    }
}
