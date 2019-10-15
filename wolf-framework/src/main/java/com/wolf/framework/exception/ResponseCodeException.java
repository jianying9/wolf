package com.wolf.framework.exception;

/**
 *
 * @author aladdin
 */
public final class ResponseCodeException extends RuntimeException {

    private final String code;
    private String desc;

    public ResponseCodeException(final String code) {
        super(code);
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
