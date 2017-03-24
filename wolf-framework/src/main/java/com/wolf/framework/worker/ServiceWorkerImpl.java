package com.wolf.framework.worker;

import com.wolf.framework.config.ResponseCodeConfig;
import com.wolf.framework.service.context.ServiceContext;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.worker.context.WorkerContext;
import com.wolf.framework.worker.workhandler.WorkHandler;
import java.util.HashMap;
import java.util.Map;
import com.wolf.framework.service.ResponseCode;
import com.wolf.framework.service.parameter.PushConfig;
import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.SecondRequestConfig;
import com.wolf.framework.service.parameter.SecondResponseConfig;
import com.wolf.framework.service.parameter.ThirdRequestConfig;
import com.wolf.framework.service.parameter.ThirdResponseConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    private void createThirdRequestParameter(String parentName, ThirdRequestConfig[] thirdRequestConfigs) {
        RequestDataType type;
        String typeStr;
        boolean ignoreEmpty;
        String desc;
        Map<String, Object> requestMap;
        for (ThirdRequestConfig thirdRequestConfig : thirdRequestConfigs) {
            requestMap = new HashMap<>(8, 1);
            type = thirdRequestConfig.dataType();
            typeStr = type.name();
            if (type == RequestDataType.LONG || type == RequestDataType.DOUBLE || type == RequestDataType.STRING) {
                typeStr = typeStr + "[" + thirdRequestConfig.min() + "," + thirdRequestConfig.max() + "]";
            }
            ignoreEmpty = thirdRequestConfig.ignoreEmpty();
            if (thirdRequestConfig.required()) {
                ignoreEmpty = false;
            }
            String name = parentName + "." + thirdRequestConfig.name();
            requestMap.put("name", name);
            requestMap.put("required", thirdRequestConfig.required());
            requestMap.put("ignoreEmpty", ignoreEmpty);
            requestMap.put("type", typeStr);
            desc = thirdRequestConfig.desc() + ":" + thirdRequestConfig.text();
            requestMap.put("desc", desc);
            this.requestConfigs.add(requestMap);
        }
    }

    private void createSecondRequestParameter(String parentName, SecondRequestConfig[] secondRequestConfigs) {
        RequestDataType type;
        ThirdRequestConfig[] thirdRequestConfigs;
        String thirdParentName;
        String typeStr;
        boolean ignoreEmpty;
        Map<String, Object> requestMap;
        String name;
        String desc;
        for (SecondRequestConfig secondRequestConfig : secondRequestConfigs) {
            requestMap = new HashMap<>(8, 1);
            type = secondRequestConfig.dataType();
            typeStr = type.name();
            if (type == RequestDataType.LONG || type == RequestDataType.DOUBLE || type == RequestDataType.STRING) {
                typeStr = typeStr + "[" + secondRequestConfig.min() + "," + secondRequestConfig.max() + "]";
            }
            ignoreEmpty = secondRequestConfig.ignoreEmpty();
            if (secondRequestConfig.required()) {
                ignoreEmpty = false;
            }
            name = parentName + "." + secondRequestConfig.name();
            requestMap.put("name", name);
            requestMap.put("required", secondRequestConfig.required());
            requestMap.put("ignoreEmpty", ignoreEmpty);
            requestMap.put("type", typeStr);
            desc = secondRequestConfig.desc() + ":" + secondRequestConfig.text();
            requestMap.put("desc", desc);
            this.requestConfigs.add(requestMap);
            thirdRequestConfigs = secondRequestConfig.thirdRequestConfigs();
            if (thirdRequestConfigs.length > 0) {
                thirdParentName = name;
                this.createThirdRequestParameter(thirdParentName, thirdRequestConfigs);
            }
        }
    }

    private void createRequestParameter(RequestConfig[] requestConfigs) {
        RequestDataType type;
        SecondRequestConfig[] secondRequestConfigs;
        String typeStr;
        boolean ignoreEmpty;
        Map<String, Object> requestMap;
        String desc;
        for (RequestConfig requestConfig : requestConfigs) {
            requestMap = new HashMap<>(8, 1);
            type = requestConfig.dataType();
            typeStr = type.name();
            if (type == RequestDataType.LONG || type == RequestDataType.DOUBLE || type == RequestDataType.STRING) {
                typeStr = typeStr + "[" + requestConfig.min() + "," + requestConfig.max() + "]";
            }
            ignoreEmpty = requestConfig.ignoreEmpty();
            if (requestConfig.required()) {
                ignoreEmpty = false;
            }
            requestMap.put("name", requestConfig.name());
            requestMap.put("required", requestConfig.required());
            requestMap.put("ignoreEmpty", ignoreEmpty);
            requestMap.put("type", typeStr);
            desc = requestConfig.desc() + ":" + requestConfig.text();
            requestMap.put("desc", desc);
            this.requestConfigs.add(requestMap);
            secondRequestConfigs = requestConfig.secondRequestConfigs();
            if (secondRequestConfigs.length > 0) {
                this.createSecondRequestParameter(requestConfig.name(), secondRequestConfigs);
            }
        }
    }

    private List<Map<String, Object>> createThirdResponseParameter(String parentName, ThirdResponseConfig[] thirdResponseConfigs) {
        Map<String, Object> responseMap;
        String name;
        List<Map<String, Object>> thirdResultList = new ArrayList(thirdResponseConfigs.length);
        for (ThirdResponseConfig thirdResponseConfig : thirdResponseConfigs) {
            responseMap = new HashMap<>(4, 1);
            name = parentName + "." + thirdResponseConfig.name();
            responseMap.put("name", name);
            responseMap.put("type", thirdResponseConfig.dataType().name());
            responseMap.put("desc", thirdResponseConfig.desc());
            thirdResultList.add(responseMap);
        }
        return thirdResultList;
    }

    private List<Map<String, Object>> createSecondResponseParameter(String parentName, SecondResponseConfig[] sesondResponseConfigs) {
        Map<String, Object> responseMap;
        ThirdResponseConfig[] thirdResponseConfigs;
        String name;
        List<Map<String, Object>> thirdResultList;
        List<Map<String, Object>> secondResultList = new ArrayList(sesondResponseConfigs.length);
        for (SecondResponseConfig secondResponseConfig : sesondResponseConfigs) {
            responseMap = new HashMap<>(4, 1);
            name = parentName + "." + secondResponseConfig.name();
            responseMap.put("name", name);
            responseMap.put("type", secondResponseConfig.dataType().name());
            responseMap.put("desc", secondResponseConfig.desc());
            this.responseConfigs.add(responseMap);
            thirdResponseConfigs = secondResponseConfig.thirdResponseConfigs();
            thirdResultList = this.createThirdResponseParameter(name, thirdResponseConfigs);
            secondResultList.addAll(thirdResultList);
        }
        return secondResultList;
    }

    private List<Map<String, Object>> createResponseParameter(ResponseConfig[] responseConfigs) {
        Map<String, Object> responseMap;
        SecondResponseConfig[] sesondResponseConfigs;
        List<Map<String, Object>> secondResultList;
        List<Map<String, Object>> firstResultList = new ArrayList(responseConfigs.length);
        for (ResponseConfig responseConfig : responseConfigs) {
            responseMap = new HashMap<>(4, 1);
            responseMap.put("name", responseConfig.name());
            responseMap.put("type", responseConfig.dataType().name());
            responseMap.put("desc", responseConfig.desc());
            firstResultList.add(responseMap);
            sesondResponseConfigs = responseConfig.secondResponseConfigs();
            secondResultList = this.createSecondResponseParameter(responseConfig.name(), sesondResponseConfigs);
            firstResultList.addAll(secondResultList);
        }
        return firstResultList;
    }

    private void createPush(PushConfig[] pushConfigs) {
        Map<String, Object> pushMap;
        List<Map<String, Object>> pushResponseConfigs;
        for (PushConfig pushConfig : pushConfigs) {
            pushResponseConfigs = this.createResponseParameter(pushConfig.responseConfigs());
            pushMap = new HashMap(2, 1);
            pushMap.put("route", pushConfig.route());
            pushMap.put("responseConfigs", pushResponseConfigs);
            this.pushConfigs.add(pushMap);
        }
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
        this.createRequestParameter(this.serviceContext.requestConfigs());
        //构造返回参数信息
        List<Map<String, Object>> responseList = this.createResponseParameter(this.serviceContext.responseConfigs());
        this.responseConfigs.addAll(responseList);
        //返回状态提示
        this.createResonseCode();
        //构造push参数信息
        this.createPush(this.serviceContext.pushConfigs());
    }

    @Override
    public ServiceContext getServiceContext() {
        return this.serviceContext;
    }
}
