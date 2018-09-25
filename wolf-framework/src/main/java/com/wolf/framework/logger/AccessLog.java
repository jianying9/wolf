package com.wolf.framework.logger;

/**
 *
 * @author aladdin
 */
public final class AccessLog {

    private final String type;

    private final String route;

    private final String sid;

    private final String request;

    private final String response;

    private final String operate;

    private final long createTime;

    public AccessLog(String route, String sid, String request, String response, long createTime) {
        this.route = route;
        //
        if (sid == null) {
            sid = "";
        }
        this.sid = sid;
        //
        if (request == null) {
            request = "";
        }
        this.request = request;
        //
        this.response = response;
        this.operate = "";
        this.type = "service";
        this.createTime = createTime;
    }

    public AccessLog(String sid, String type, String operate, long createTime) {
        this.route = "";
        //
        if (sid == null) {
            sid = "";
        }
        this.sid = sid;
        this.request = "";
        this.response = "";
        this.operate = operate;
        this.type = type;
        this.createTime = createTime;
    }

    public String getRoute() {
        return route;
    }

    public String getSid() {
        return sid;
    }

    public String getRequest() {
        return request;
    }

    public String getResponse() {
        return response;
    }

    public String getType() {
        return type;
    }

    public String getOperate() {
        return operate;
    }

    public long getCreateTime() {
        return createTime;
    }

}
