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

    @Override
    public Map<String, Object> getObjectValue(String name) {
        Object value = this.request.getParameter(name);
        return (Map<String, Object>) value;
    }

    @Override
    public List<Map<String, Object>> getObjectListValue(String name) {
        Object value = this.request.getParameter(name);
        return (List<Map<String, Object>>) value;
    }

    @Override
    public Long getLongValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        return (Long) value;
    }

    @Override
    public Boolean getBooleanValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        return (Boolean) value;
    }

    @Override
    public Double getDoubleValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        return (Double) value;
    }

    @Override
    public String getStringValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        return (String) value;
    }

    @Override
    public List<Long> getLongListValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        return (List<Long>) value;
    }

    @Override
    public List<String> getStringListValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        return (List<String>) value;
    }

    @Override
    public Map<String, Object> getObjectValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        return (Map<String, Object>) value;
    }

    @Override
    public List<Map<String, Object>> getObjectListValue(Map<String, Object> object, String name) {
        Object value = object.get(name);
        return (List<Map<String, Object>>) value;
    }

}
