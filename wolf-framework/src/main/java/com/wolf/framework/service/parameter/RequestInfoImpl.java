package com.wolf.framework.service.parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jianying9
 */
public class RequestInfoImpl implements RequestInfo {
    
    private final String name;
    private final boolean required;
    private final boolean ignoreEmpty;
    private final RequestDataType dataType;
    private final long max;
    private final long min;
    private final String text;
    private final String desc;
    private final List<RequestInfo> childList;
    
    public RequestInfoImpl(RequestInfo parentRequestInfo, ThirdRequestConfig thirdRequestConfig) {
        this.name = thirdRequestConfig.name();
        this.required = thirdRequestConfig.required();
        this.ignoreEmpty = thirdRequestConfig.ignoreEmpty();
        this.dataType = thirdRequestConfig.dataType();
        this.max = thirdRequestConfig.max();
        this.min = thirdRequestConfig.min();
        this.text = thirdRequestConfig.text();
        this.desc = thirdRequestConfig.desc();
        this.childList = Collections.EMPTY_LIST;
    }

    public RequestInfoImpl(RequestInfo parentRequestInfo, SecondRequestConfig secondRequestConfig) {
        this.name = secondRequestConfig.name();
        this.required = secondRequestConfig.required();
        this.ignoreEmpty = secondRequestConfig.ignoreEmpty();
        this.dataType = secondRequestConfig.dataType();
        this.max = secondRequestConfig.max();
        this.min = secondRequestConfig.min();
        this.text = secondRequestConfig.text();
        this.desc = secondRequestConfig.desc();
        ThirdRequestConfig[] thirdRequestConfigs = secondRequestConfig.thirdRequestConfigs();
        this.childList = new ArrayList(thirdRequestConfigs.length);
        RequestInfo childRequestInfo;
        for (ThirdRequestConfig thirdRequestConfig : thirdRequestConfigs) {
            childRequestInfo = new RequestInfoImpl(this, thirdRequestConfig);
            this.childList.add(childRequestInfo);
        }
    }
    
    public RequestInfoImpl(RequestConfig requestConfig) {
        this.name = requestConfig.name();
        this.required = requestConfig.required();
        this.ignoreEmpty = requestConfig.ignoreEmpty();
        this.dataType = requestConfig.dataType();
        this.max = requestConfig.max();
        this.min = requestConfig.min();
        this.text = requestConfig.text();
        this.desc = requestConfig.desc();
        SecondRequestConfig[] secondRequestConfigs = requestConfig.secondRequestConfigs();
        this.childList = new ArrayList(secondRequestConfigs.length);
        RequestInfo childRequestInfo;
        for (SecondRequestConfig secondRequestConfig : secondRequestConfigs) {
            childRequestInfo = new RequestInfoImpl(this, secondRequestConfig);
            this.childList.add(childRequestInfo);
        }
    }
    
    public RequestInfoImpl(ExtendRequestConfig extendRequestConfig) {
        this.name = extendRequestConfig.name();
        this.required = extendRequestConfig.required();
        this.ignoreEmpty = extendRequestConfig.ignoreEmpty();
        this.dataType = extendRequestConfig.dataType();
        this.max = extendRequestConfig.max();
        this.min = extendRequestConfig.min();
        this.text = extendRequestConfig.text();
        this.desc = extendRequestConfig.desc();
        this.childList = Collections.EMPTY_LIST;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isRequired() {
        return this.required;
    }

    @Override
    public boolean isIgnoreEmpty() {
        return this.ignoreEmpty;
    }

    @Override
    public RequestDataType getDataType() {
        return this.dataType;
    }

    @Override
    public long getMax() {
        return this.max;
    }

    @Override
    public long getMin() {
        return this.min;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public List<RequestInfo> getChildList() {
        return this.childList;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public void setChildList(List<RequestInfo> childList) {
        this.childList.clear();
        this.childList.addAll(childList);
    }
    
}
