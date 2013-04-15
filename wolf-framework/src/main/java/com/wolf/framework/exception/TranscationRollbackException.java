package com.wolf.framework.exception;

import com.wolf.framework.config.ResponseFlagType;

/**
 *
 * @author aladdin
 */
public final class TranscationRollbackException extends RuntimeException {

    private static final long serialVersionUID = 489653045742631404L;
    private final ResponseFlagType responseFlagType;

    public TranscationRollbackException(final ResponseFlagType responseFlagType) {
        super(responseFlagType.getFlagName());
        this.responseFlagType = responseFlagType;
    }

    public ResponseFlagType getFlag() {
        return this.responseFlagType;
    }
}
