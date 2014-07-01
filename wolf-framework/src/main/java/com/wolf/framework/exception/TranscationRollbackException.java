package com.wolf.framework.exception;

/**
 *
 * @author aladdin
 */
public final class TranscationRollbackException extends RuntimeException {

    private static final long serialVersionUID = 489653045742631404L;
    private final String state;

    public TranscationRollbackException(final String state) {
        super(state);
        this.state = state;
    }

    public String getState() {
        return this.state;
    }
}
