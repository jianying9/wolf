package com.wolf.framework.service.parameter;

import com.wolf.framework.service.ResponseCode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jianying9
 */
public class PushInfoImpl implements PushInfo {
    
    private final String route;
    private final ResponseCode[] responseCodes;
    private final List<ResponseInfo> responseInfoList;
    
    

    public PushInfoImpl(PushConfig pushConfig) {
        this.route = pushConfig.route();
        this.responseCodes = pushConfig.responseCodes();
        ResponseConfig[] responseConfigs = pushConfig.responseConfigs();
        this.responseInfoList = new ArrayList(responseConfigs.length);
        for (ResponseConfig responseConfig : responseConfigs) {
            this.responseInfoList.add(new ResponseInfoImpl(responseConfig));
        }
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
    public ResponseCode[] responseCodes() {
        return this.responseCodes;
    }

    @Override
    public void setResponseInfoList(List<ResponseInfo> responseInfoList) {
        this.responseInfoList.clear();
        this.responseInfoList.addAll(responseInfoList);
    }
    
}
