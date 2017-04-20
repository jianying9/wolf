package com.wolf.framework.service.parameter;

/**
 *
 * @author jianying9
 */
public interface ResponseHandler {

    public String getName();

    public ResponseDataType getDataType();

    public Object getResponseValue(Object value);
}
