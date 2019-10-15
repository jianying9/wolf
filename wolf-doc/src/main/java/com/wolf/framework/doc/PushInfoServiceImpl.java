package com.wolf.framework.doc;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.reponse.Response;
import com.wolf.framework.request.Request;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.parameter.PushInfo;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.service.parameter.ResponseDataType;
import com.wolf.framework.service.parameter.ResponseInfo;
import com.wolf.framework.service.parameter.SecondResponseConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 */
@ServiceConfig(
        route = "/wolf/push/info",
        requestConfigs = {
            @RequestConfig(name = "routeName", dataType = RequestDataType.STRING, max = 200, desc = "")
        },
        responseConfigs = {
            @ResponseConfig(name = "routeName", dataType = ResponseDataType.STRING, filterTypes = {}, desc = ""),
            @ResponseConfig(name = "desc", dataType = ResponseDataType.STRING, desc = ""),
            @ResponseConfig(name = "responseArray", dataType = ResponseDataType.OBJECT_ARRAY, desc = "",
                    secondResponseConfigs = {
                        @SecondResponseConfig(name = "name", dataType = ResponseDataType.STRING, desc = ""),
                        @SecondResponseConfig(name = "type", dataType = ResponseDataType.STRING, desc = ""),
                        @SecondResponseConfig(name = "desc", dataType = ResponseDataType.STRING, desc = "")
                    }),
            @ResponseConfig(name = "serviceArray", dataType = ResponseDataType.STRING_ARRAY, desc = "")
        },
        responseCodes = {},
        validateSession = false,
        desc = "")
public class PushInfoServiceImpl implements Service {

    @Override
    public void execute(Request request, Response response) {
        String route = request.getStringValue("routeName");
        Map<String, PushInfo> pushInfoMap = ApplicationContext.CONTEXT.getPushInfoMap();
        PushInfo pushInfo = pushInfoMap.get(route);
        if (pushInfo != null) {
            List<Map<String, Object>> responseMapList = this.createResponseList("", pushInfo.getResponseInfoList());
            //
            Map<String, Object> dataMap = new HashMap(4, 1);
            dataMap.put("routeName", pushInfo.getRoute());
            dataMap.put("desc", pushInfo.getDesc());
            dataMap.put("responseArray", responseMapList);
            dataMap.put("serviceArray", pushInfo.getServiceList());
            //
            response.setDataMap(dataMap);
        }
    }
    
    private List<Map<String, Object>> createResponseList(String parentName, List<ResponseInfo> responseInfoList) {
        Map<String, Object> responseMap;
        List<ResponseInfo> childResponseInfoList;
        String name;
        List<Map<String, Object>> childResultList;
        List<Map<String, Object>> resultList = new ArrayList(responseInfoList.size());
        for (ResponseInfo responseInfo : responseInfoList) {
            responseMap = new HashMap<>(4, 1);
            if(parentName.isEmpty() == false) {
                name = parentName + "." + responseInfo.getName();
            } else {
                name = responseInfo.getName();
            }
            responseMap.put("name", name);
            responseMap.put("type", responseInfo.getDataType().name());
            responseMap.put("desc", responseInfo.getDesc());
            resultList.add(responseMap);
            childResponseInfoList = responseInfo.getChildList();
            if (childResponseInfoList.isEmpty() == false) {
                childResultList = this.createResponseList(name, childResponseInfoList);
                resultList.addAll(childResultList);
            }
        }
        return resultList;
    }
}
