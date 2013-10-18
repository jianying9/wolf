package com.wolf.framework.exception;

/**
 *
 * @author aladdin
 */
public final class TranscationRollbackException extends RuntimeException {

    private static final long serialVersionUID = 489653045742631404L;
    private final String flag;

    public TranscationRollbackException(final String flag) {
        super(flag);
        this.flag = flag;
    }

    public String getFlag() {
        return this.flag;
    }
}
