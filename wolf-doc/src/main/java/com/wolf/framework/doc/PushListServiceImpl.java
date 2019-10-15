package com.wolf.framework.doc;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.reponse.Response;
import com.wolf.framework.request.Request;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.parameter.PushInfo;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.service.parameter.ResponseDataType;
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
        route = "/wolf/push/list",
        requestConfigs = {},
        responseConfigs = {
            @ResponseConfig(name = "pushArray", dataType = ResponseDataType.OBJECT_ARRAY, desc = "",
                    secondResponseConfigs = {
                        @SecondResponseConfig(name = "routeName", dataType = ResponseDataType.STRING, desc = ""),
                        @SecondResponseConfig(name = "desc", dataType = ResponseDataType.STRING, desc = "")
                    })
        },
        responseCodes = {},
        validateSession = false,
        desc = "")
public class PushListServiceImpl implements Service {

    @Override
    public void execute(Request request, Response response) {
        Map<String, PushInfo> pushInfoMap = ApplicationContext.CONTEXT.getPushInfoMap();
        //过滤系统接口
        List<PushInfo> pushInfoList = new ArrayList<>(pushInfoMap.size());
        pushInfoList.addAll(pushInfoMap.values());
        //输出
        Map<String, Object> resultMap;
        List<Map<String, Object>> resultMapList = new ArrayList<>(pushInfoList.size());
        for (PushInfo pushInfo : pushInfoList) {
            resultMap = new HashMap<>(4, 1);
            resultMap.put("routeName", pushInfo.getRoute());
            resultMap.put("desc", pushInfo.getDesc());
            resultMapList.add(resultMap);
        }
        Map<String, Object> dataMap = new HashMap(2, 1);
        dataMap.put("pushArray", resultMapList);
        response.setDataMap(dataMap);
        response.success();
    }
    
}
