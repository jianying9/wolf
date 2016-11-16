package com.wolf.framework.service.request;

import com.wolf.framework.request.Request;
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
    public final Map<String, String> getParameterMap() {
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
    public final String getParameter(String name) {
        return this.request.getParameter(name);
    }
}
