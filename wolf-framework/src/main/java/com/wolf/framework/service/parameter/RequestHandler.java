package com.wolf.framework.service.parameter;

/**
 *
 * @author aladdin
 */
public interface RequestHandler {

    public String getName();

    public RequestDataType getDataType();

    public String validate(Object value);
    
    public boolean getIgnoreEmpty();
}
