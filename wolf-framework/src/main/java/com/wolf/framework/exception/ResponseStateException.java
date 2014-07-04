package com.wolf.framework.exception;

/**
 *
 * @author aladdin
 */
public final class ResponseStateException extends RuntimeException {

    private static final long serialVersionUID = -8389962280396248534L;
    private final String state;

    public ResponseStateException(final String state) {
        super(state);
        this.state = state;
    }

    public String getState() {
        return this.state;
    }
}
