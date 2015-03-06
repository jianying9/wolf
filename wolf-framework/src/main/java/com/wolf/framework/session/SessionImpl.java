package com.wolf.framework.session;

/**
 *
 * @author aladdin
 */
@Deprecated
public class SessionImpl implements Session {

    private final String sid;

    public SessionImpl(String sid) {
        this.sid = sid;
    }

    @Override
    public String getSid() {
        return this.sid;
    }
}
