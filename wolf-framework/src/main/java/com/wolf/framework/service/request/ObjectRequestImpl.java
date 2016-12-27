package com.wolf.framework.service.request;

import com.wolf.framework.request.Request;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class ObjectRequestImpl implements ObjectRequest {

    private final Request request;

    public ObjectRequestImpl(Request request) {
        this.request = request;
    }

    @Override
    public final Map<String, Object> getValueMap() {
        return this.request.getParameterMap();
    }

    @Override
    public final String getRoute() {
        return this.request.getRoute();
    }

    @Override
    public final String getSessionId() {
        return this.request.getSessionId();
    }

    @Override
    public final Object getValue(String name) {
        return this.request.getParameter(name);
    }

    @Override
    public Long getLongValue(String name) {
        Object value = this.request.getParameter(name);
        return (Long) value;
    }

    @Override
    public Boolean getBooleanValue(String name) {
        Object value = this.request.getParameter(name);
        return (Boolean) value;
    }

    @Override
    public Double getDoubleValue(String name) {
        Object value = this.request.getParameter(name);
        return (Double) value;
    }

    @Override
    public String getStringValue(String name) {
        Object value = this.request.getParameter(name);
        return (String) value;
    }

    @Override
    public List<Long> getLongListValue(String name) {
        Object value = this.request.getParameter(name);
        return (List<Long>) value;
    }

    @Override
    public List<String> getStringListValue(String name) {
        Object value = this.request.getParameter(name);
        return (List<String>) value;
    }

}
