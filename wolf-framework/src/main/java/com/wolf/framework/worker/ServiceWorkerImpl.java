package com.wolf.framework.worker;

import com.wolf.framework.config.ResponseCodeConfig;
import com.wolf.framework.service.context.ServiceContext;
import com.wolf.framework.worker.context.WorkerContext;
import com.wolf.framework.worker.workhandler.WorkHandler;
import java.util.HashMap;
import java.util.Map;
import com.wolf.framework.service.ResponseCode;
import com.wolf.framework.service.parameter.PushInfo;
import com.wolf.framework.service.parameter.RequestDataType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.wolf.framework.service.parameter.RequestInfo;
import com.wolf.framework.service.parameter.ResponseInfo;

/**
 * 服务工作对象接口
 *
 * @author aladdin
 */
public class ServiceWorkerImpl implements ServiceWorker {

    private final WorkHandler nextWorkHandler;
    private final List<Map<String, Object>> requestConfigs = new ArrayList<>();
    private final List<Map<String, Object>> responseConfigs = new ArrayList<>();
    private final List<Map<String, Object>> responseCodes = new ArrayList<>();
    private final List<Map<String, Object>> pushConfigs = new ArrayList<>();
    private final ServiceContext serviceContext;

    public ServiceWorkerImpl(WorkHandler nextWorkHandler, ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
        this.nextWorkHandler = nextWorkHandler;
    }

    @Override
    public void doWork(WorkerContext workerContext) {
        this.nextWorkHandler.execute(workerContext);
    }

    @Override
    public Map<String, Object> getInfoMap() {
        Map<String, Object> infoMap = new HashMap<>(8, 1);
        infoMap.put("routeName", this.serviceContext.route());
        infoMap.put("validateSession", this.serviceContext.validateSession());
        infoMap.put("desc", this.serviceContext.desc());
        infoMap.put("requestConfigs", this.requestConfigs);
        infoMap.put("responseConfigs", this.responseConfigs);
        infoMap.put("responseCodes", this.responseCodes);
        infoMap.put("hasAsyncResponse", this.serviceContext.hasAsyncResponse());
        infoMap.put("pushConfigs", this.pushConfigs);
        return infoMap;
    }

    private List<Map<String, Object>> createRequestParameter(String parentName, List<RequestInfo> requestParameterList) {
        RequestDataType type;
        List<RequestInfo> childRequestParameterList;
        String childParentName;
        String typeStr;
        boolean ignoreEmpty;
        Map<String, Object> requestMap;
        String name;
        String desc;
        List<Map<String, Object>> childResultList;
        List<Map<String, Object>> resultList = new ArrayList(requestParameterList.size());
        for (RequestInfo requestParameter : requestParameterList) {
            requestMap = new HashMap<>(8, 1);
            type = requestParameter.getDataType();
            typeStr = type.name();
            if (type == RequestDataType.LONG || type == RequestDataType.DOUBLE || type == RequestDataType.STRING) {
                typeStr = typeStr + "[" + requestParameter.getMin() + "," + requestParameter.getMax() + "]";
            }
            ignoreEmpty = requestParameter.isIgnoreEmpty();
            if (requestParameter.isRequired()) {
                ignoreEmpty = false;
            }
            name = parentName + "." + requestParameter.getName();
            requestMap.put("name", name);
            requestMap.put("required", requestParameter.isRequired());
            requestMap.put("ignoreEmpty", ignoreEmpty);
            requestMap.put("type", typeStr);
            desc = requestParameter.getDesc() + ":" + requestParameter.getText();
            requestMap.put("desc", desc);
            resultList.add(requestMap);
            childRequestParameterList = requestParameter.getChildList();
            if (childRequestParameterList.isEmpty() == false) {
                childParentName = name;
                childResultList = this.createRequestParameter(childParentName, childRequestParameterList);
                resultList.addAll(childResultList);
            }
        }
        return resultList;
    }

    private List<Map<String, Object>> createResponseParameter(String parentName, List<ResponseInfo> responseParameterList) {
        Map<String, Object> responseMap;
        List<ResponseInfo> childResponseParameterList;
        String name;
        List<Map<String, Object>> childResultList;
        List<Map<String, Object>> resultList = new ArrayList(responseParameterList.size());
        for (ResponseInfo responseParameter : responseParameterList) {
            responseMap = new HashMap<>(4, 1);
            name = parentName + "." + responseParameter.getName();
            responseMap.put("name", name);
            responseMap.put("type", responseParameter.getDataType().name());
            responseMap.put("desc", responseParameter.getDesc());
            this.responseConfigs.add(responseMap);
            childResponseParameterList = responseParameter.getChildList();
            childResultList = this.createResponseParameter(name, childResponseParameterList);
            resultList.addAll(childResultList);
        }
        return resultList;
    }

    private List<Map<String, Object>> createPush(List<PushInfo> pushInfoList) {
        Map<String, Object> pushMap;
        List<Map<String, Object>> pushResponseConfigs;
        List<Map<String, Object>> resultList = new ArrayList(pushInfoList.size());
        for (PushInfo pushInfo : pushInfoList) {
            pushResponseConfigs = this.createResponseParameter("", pushInfo.getResponseInfoList());
            pushMap = new HashMap(2, 1);
            pushMap.put("route", pushInfo.getRoute());
            pushMap.put("responseConfigs", pushResponseConfigs);
            resultList.add(pushMap);
        }
        return resultList;
    }

    private void createResonseCode() {
        Map<String, String> responseCodeMap = new HashMap<>();
        responseCodeMap.put(ResponseCodeConfig.SUCCESS, "操作成功");
        responseCodeMap.put(ResponseCodeConfig.INVALID, "非法参数");
        responseCodeMap.put(ResponseCodeConfig.UNLOGIN, "未登陆");
        responseCodeMap.put(ResponseCodeConfig.TIMEOUT, "session超期");
        responseCodeMap.put(ResponseCodeConfig.DENIED, "无权限访问");
        responseCodeMap.put(ResponseCodeConfig.NOTFOUND, "服务不存在");
        responseCodeMap.put(ResponseCodeConfig.UNMODIFYED, "请求返回数据没有变化");
        Map<String, Object> codeMap;
        for (ResponseCode responseCode : this.serviceContext.responseCodes()) {
            responseCodeMap.remove(responseCode.code());
            codeMap = new HashMap<>(4, 1);
            codeMap.put("code", responseCode.code());
            codeMap.put("desc", responseCode.desc());
            codeMap.put("type", "custom");
            this.responseCodes.add(codeMap);
        }
        Set<Map.Entry<String, String>> codeSet = responseCodeMap.entrySet();
        for (Map.Entry<String, String> entry : codeSet) {
            codeMap = new HashMap<>(4, 1);
            codeMap.put("code", entry.getKey());
            codeMap.put("async", false);
            codeMap.put("desc", entry.getValue());
            codeMap.put("type", "global");
            this.responseCodes.add(codeMap);
        }
    }

    @Override
    public final void createInfo() {

        //构造请求参数信息
        List<Map<String, Object>> requestList = this.createRequestParameter("", this.serviceContext.requestConfigs());
        this.requestConfigs.addAll(requestList);
        //构造返回参数信息
        List<Map<String, Object>> responseList = this.createResponseParameter("", this.serviceContext.responseConfigs());
        this.responseConfigs.addAll(responseList);
        //返回状态提示
        this.createResonseCode();
        //构造push参数信息
        List<Map<String, Object>> pushList = this.createPush(this.serviceContext.pushConfigs());
        this.pushConfigs.addAll(pushList);
    }

    @Override
    public ServiceContext getServiceContext() {
        return this.serviceContext;
    }
}
