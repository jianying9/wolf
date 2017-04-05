package com.wolf.framework.service.parameter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jianying9
 */
public class PushInfoImpl implements PushInfo {
    
    private final String route;
    private final String desc;
    private final List<ResponseInfo> responseInfoList;
    private final List<String> serviceList;
    
    

    public PushInfoImpl(String route, PushConfig pushConfig) {
        this.route = route;
        this.desc = pushConfig.desc();
        ResponseConfig[] responseConfigs = pushConfig.responseConfigs();
        this.responseInfoList = new ArrayList(responseConfigs.length);
        for (ResponseConfig responseConfig : responseConfigs) {
            this.responseInfoList.add(new ResponseInfoImpl(responseConfig));
        }
        this.serviceList = new ArrayList(0);
    }

    @Override
    public String getRoute() {
        return route;
    }

    @Override
    public List<ResponseInfo> getResponseInfoList() {
        return this.responseInfoList;
    }

    @Override
    public void setResponseInfoList(List<ResponseInfo> responseInfoList) {
        this.responseInfoList.clear();
        this.responseInfoList.addAll(responseInfoList);
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public void addService(String route) {
        this.serviceList.add(route);
    }

    @Override
    public List<String> getServiceList() {
        return this.serviceList;
    }
    
}
