package com.wolf.framework.exception;

/**
 *
 * @author aladdin
 */
public final class ResponseCodeException extends RuntimeException {

    private static final long serialVersionUID = -8389962280396248534L;
    private final String code;

    public ResponseCodeException(final String code) {
        super(code);
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
