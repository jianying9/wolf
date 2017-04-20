package com.wolf.framework.service.parameter;

import com.wolf.framework.service.parameter.filter.FilterType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jianying9
 */
public class ResponseInfoImpl implements ResponseInfo {

    private final String name;
    private final ResponseDataType dataType;
    private final FilterType[] filterType;
    private final String desc;
    private final List<ResponseInfo> childList;
    
    public ResponseInfoImpl(ResponseInfo parentResponseParameter, FourResponseConfig fourResponseConfig) {
        this.name = fourResponseConfig.name();
        this.dataType = fourResponseConfig.dataType();
        this.filterType = fourResponseConfig.filterTypes();
        this.desc = fourResponseConfig.desc();
        this.childList = Collections.EMPTY_LIST;
    }
    
    public ResponseInfoImpl(ResponseInfo parentResponseParameter, ThirdResponseConfig thirdResponseConfig) {
        this.name = thirdResponseConfig.name();
        this.dataType = thirdResponseConfig.dataType();
        this.filterType = thirdResponseConfig.filterTypes();
        this.desc = thirdResponseConfig.desc();
        //
        FourResponseConfig[] fourResponseConfigs = thirdResponseConfig.fourResponseConfigs();
        this.childList = new ArrayList(fourResponseConfigs.length);
        ResponseInfo childResponseParameter;
        for (FourResponseConfig fourResponseConfig : fourResponseConfigs) {
            childResponseParameter = new ResponseInfoImpl(this, fourResponseConfig);
            this.childList.add(childResponseParameter);
        }
    }

    public ResponseInfoImpl(ResponseInfo parenteResponseInfo, SecondResponseConfig secondResponseConfig) {
        this.name = secondResponseConfig.name();
        this.dataType = secondResponseConfig.dataType();
        this.filterType = secondResponseConfig.filterTypes();
        this.desc = secondResponseConfig.desc();
        //
        ThirdResponseConfig[] thirdResponseConfigs = secondResponseConfig.thirdResponseConfigs();
        this.childList = new ArrayList(thirdResponseConfigs.length);
        ResponseInfo childResponseParameter;
        for (ThirdResponseConfig thirdResponseConfig : thirdResponseConfigs) {
            childResponseParameter = new ResponseInfoImpl(this, thirdResponseConfig);
            this.childList.add(childResponseParameter);
        }
    }
    
    public ResponseInfoImpl(ResponseConfig responseConfig) {
        this.name = responseConfig.name();
        this.dataType = responseConfig.dataType();
        this.filterType = responseConfig.filterTypes();
        this.desc = responseConfig.desc();
        //
        SecondResponseConfig[] secondResponseConfigs = responseConfig.secondResponseConfigs();
        this.childList = new ArrayList(secondResponseConfigs.length);
        ResponseInfo childResponseParameter;
        for (SecondResponseConfig secondResponseConfig : secondResponseConfigs) {
            childResponseParameter = new ResponseInfoImpl(this, secondResponseConfig);
            this.childList.add(childResponseParameter);
        }
    }
    
    public ResponseInfoImpl(ExtendResponseConfig extendResponseConfig) {
        this.name = extendResponseConfig.name();
        this.dataType = extendResponseConfig.dataType();
        this.filterType = extendResponseConfig.filterTypes();
        this.desc = extendResponseConfig.desc();
        this.childList = Collections.EMPTY_LIST;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ResponseDataType getDataType() {
        return this.dataType;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public FilterType[] getFilterTypes() {
        return this.filterType;
    }

    @Override
    public List<ResponseInfo> getChildList() {
        return this.childList;
    }

    @Override
    public void setChildList(List<ResponseInfo> childList) {
        this.childList.clear();
        this.childList.addAll(childList);
    }

}
