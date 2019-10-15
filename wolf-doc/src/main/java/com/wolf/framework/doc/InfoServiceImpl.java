package com.wolf.framework.doc;

import com.wolf.framework.config.ResponseCodeConfig;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.reponse.Response;
import com.wolf.framework.request.Request;
import com.wolf.framework.service.ResponseCode;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.context.ServiceContext;
import com.wolf.framework.service.parameter.PushInfo;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestInfo;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.service.parameter.ResponseDataType;
import com.wolf.framework.service.parameter.ResponseInfo;
import com.wolf.framework.service.parameter.SecondResponseConfig;
import com.wolf.framework.worker.ServiceWorker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author jianying9
 */
@ServiceConfig(
        route = "/wolf/service/info",
        requestConfigs = {
            @RequestConfig(name = "routeName", dataType = RequestDataType.STRING, max = 200, desc = "")
        },
        responseConfigs = {
            @ResponseConfig(name = "routeName", dataType = ResponseDataType.STRING, filterTypes = {}, desc = ""),
            @ResponseConfig(name = "group", dataType = ResponseDataType.STRING, filterTypes = {}, desc = ""),
            @ResponseConfig(name = "desc", dataType = ResponseDataType.STRING, desc = ""),
            @ResponseConfig(name = "page", dataType = ResponseDataType.BOOLEAN, desc = ""),
            @ResponseConfig(name = "validateSession", dataType = ResponseDataType.BOOLEAN, desc = ""),
            @ResponseConfig(name = "groupName", dataType = ResponseDataType.STRING, desc = ""),
            @ResponseConfig(name = "requestArray", dataType = ResponseDataType.OBJECT_ARRAY, desc = "",
                    secondResponseConfigs = {
                        @SecondResponseConfig(name = "name", dataType = ResponseDataType.STRING, desc = ""),
                        @SecondResponseConfig(name = "required", dataType = ResponseDataType.BOOLEAN, desc = ""),
                        @SecondResponseConfig(name = "ignoreEmpty", dataType = ResponseDataType.BOOLEAN, desc = ""),
                        @SecondResponseConfig(name = "type", dataType = ResponseDataType.STRING, desc = ""),
                        @SecondResponseConfig(name = "desc", dataType = ResponseDataType.STRING, desc = "")
                    }),
            @ResponseConfig(name = "codeArray", dataType = ResponseDataType.OBJECT_ARRAY, desc = "",
                    secondResponseConfigs = {
                        @SecondResponseConfig(name = "code", dataType = ResponseDataType.STRING, desc = ""),
                        @SecondResponseConfig(name = "async", dataType = ResponseDataType.BOOLEAN, desc = ""),
                        @SecondResponseConfig(name = "type", dataType = ResponseDataType.STRING, desc = ""),
                        @SecondResponseConfig(name = "desc", dataType = ResponseDataType.STRING, desc = "")
                    }),
            @ResponseConfig(name = "responseArray", dataType = ResponseDataType.OBJECT_ARRAY, desc = "",
                    secondResponseConfigs = {
                        @SecondResponseConfig(name = "name", dataType = ResponseDataType.STRING, desc = ""),
                        @SecondResponseConfig(name = "type", dataType = ResponseDataType.STRING, desc = ""),
                        @SecondResponseConfig(name = "desc", dataType = ResponseDataType.STRING, desc = "")
                    }),
            @ResponseConfig(name = "pushArray", dataType = ResponseDataType.OBJECT_ARRAY, desc = "",
                    secondResponseConfigs = {
                        @SecondResponseConfig(name = "routeName", dataType = ResponseDataType.STRING, desc = ""),
                        @SecondResponseConfig(name = "desc", dataType = ResponseDataType.STRING, desc = ""),
                        
                    }),
            @ResponseConfig(name = "hasAsyncResponse", dataType = ResponseDataType.BOOLEAN, desc = "")
        },
        responseCodes = {},
        validateSession = false,
        desc = "")
public class InfoServiceImpl implements Service {

    @Override
    public void execute(Request request, Response response) {
        String route = request.getStringValue("routeName");
        ServiceWorker serviceWorker = ApplicationContext.CONTEXT.getServiceWorker(route);
        if (serviceWorker != null) {
            response.setNewSessionId(UUID.randomUUID().toString());
            ServiceContext serviceContext = serviceWorker.getServiceContext();
            //
            List<Map<String, Object>> requestMapList = this.createRequestList("", serviceContext.requestConfigs());
            //
            List<Map<String, Object>> responseMapList = this.createResponseList("", serviceContext.responseConfigs());
            //
            List<Map<String, Object>> codeMapList = this.createResonseCode(serviceContext.responseCodes());
            //
            List<Map<String, Object>> pushList = this.createPush(serviceContext.pushConfigs());
            //
            Map<String, Object> dataMap = new HashMap<>(8, 1);
            dataMap.put("routeName", serviceContext.route());
            dataMap.put("group", serviceContext.group());
            dataMap.put("validateSession", serviceContext.validateSession());
            dataMap.put("desc", serviceContext.desc());
            dataMap.put("requestArray", requestMapList);
            dataMap.put("responseArray", responseMapList);
            dataMap.put("codeArray", codeMapList);
            dataMap.put("hasAsyncResponse", serviceContext.hasAsyncResponse());
            dataMap.put("pushArray", pushList);
            response.setDataMap(dataMap);
        }
    }

    private List<Map<String, Object>> createRequestList(String parentName, List<RequestInfo> requestInfoList) {
        RequestDataType type;
        List<RequestInfo> childRequestInfoList;
        String typeStr;
        boolean ignoreEmpty;
        Map<String, Object> requestMap;
        String name;
        String desc;
        List<Map<String, Object>> childResultList;
        List<Map<String, Object>> resultList = new ArrayList(requestInfoList.size());
        for (RequestInfo requestInfo : requestInfoList) {
            requestMap = new HashMap<>(8, 1);
            type = requestInfo.getDataType();
            typeStr = type.name();
            if (type == RequestDataType.LONG || type == RequestDataType.DOUBLE || type == RequestDataType.STRING) {
                typeStr = typeStr + "[" + requestInfo.getMin() + "," + requestInfo.getMax() + "]";
            }
            ignoreEmpty = requestInfo.isIgnoreEmpty();
            if (requestInfo.isRequired()) {
                ignoreEmpty = false;
            }
            if(parentName.isEmpty() == false) {
                name = parentName + "." + requestInfo.getName();
            } else {
                name = requestInfo.getName();
            }
            requestMap.put("name", name);
            requestMap.put("required", requestInfo.isRequired());
            requestMap.put("ignoreEmpty", ignoreEmpty);
            requestMap.put("type", typeStr);
            desc = requestInfo.getDesc() + ":" + requestInfo.getText();
            requestMap.put("desc", desc);
            resultList.add(requestMap);
            //
            childRequestInfoList = requestInfo.getChildList();
            if (childRequestInfoList.isEmpty() == false) {
                childResultList = this.createRequestList(name, childRequestInfoList);
                resultList.addAll(childResultList);
            }
        }
        return resultList;
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
    
    private List<Map<String, Object>> createResonseCode(ResponseCode[] responseCodes) {
        List<Map<String, Object>> codeMapList = new ArrayList<>();
        Map<String, String> responseCodeMap = new HashMap<>();
        responseCodeMap.put(ResponseCodeConfig.SUCCESS, "操作成功");
        responseCodeMap.put(ResponseCodeConfig.INVALID, "非法参数");
        responseCodeMap.put(ResponseCodeConfig.UNLOGIN, "未登陆");
        responseCodeMap.put(ResponseCodeConfig.TIMEOUT, "session超期");
        responseCodeMap.put(ResponseCodeConfig.DENIED, "无权限访问");
        responseCodeMap.put(ResponseCodeConfig.NOTFOUND, "服务不存在");
        responseCodeMap.put(ResponseCodeConfig.UNMODIFYED, "请求返回数据没有变化");
        Map<String, Object> codeMap;
        for (ResponseCode responseCode : responseCodes) {
            responseCodeMap.remove(responseCode.code());
            codeMap = new HashMap<>(4, 1);
            codeMap.put("code", responseCode.code());
            codeMap.put("desc", responseCode.desc());
            codeMap.put("type", "custom");
            codeMapList.add(codeMap);
        }
        Set<Map.Entry<String, String>> codeSet = responseCodeMap.entrySet();
        for (Map.Entry<String, String> entry : codeSet) {
            codeMap = new HashMap<>(4, 1);
            codeMap.put("code", entry.getKey());
            codeMap.put("desc", entry.getValue());
            codeMap.put("type", "global");
            codeMapList.add(codeMap);
        }
        return codeMapList;
    }
    
    private List<Map<String, Object>> createPush(List<PushInfo> pushInfoList) {
        Map<String, Object> pushMap;
        List<Map<String, Object>> resultList = new ArrayList(pushInfoList.size());
        for (PushInfo pushInfo : pushInfoList) {
            pushMap = new HashMap(2, 1);
            pushMap.put("routeName", pushInfo.getRoute());
            pushMap.put("desc", pushInfo.getDesc());
            resultList.add(pushMap);
        }
        return resultList;
    }
}
