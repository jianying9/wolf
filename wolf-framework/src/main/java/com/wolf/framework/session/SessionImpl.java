package com.wolf.framework.session;

/**
 *
 * @author aladdin
 */
public class SessionImpl implements Session {

    private final String userId;

    public SessionImpl(String userId) {
        this.userId = userId;
    }

    @Override
    public String getUserId() {
        return this.userId;
    }

    @Override
    public String getSid() {
        return this.userId;
    }

    @Override
    public boolean isIdle() {
        return false;
    }
}
