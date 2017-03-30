package com.wolf.framework.service.context;

import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.SessionHandleType;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.RequestHandlerBuilder;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.service.parameter.ResponseHandlerBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.wolf.framework.worker.build.WorkerBuildContext;
import com.wolf.framework.service.ResponseCode;
import com.wolf.framework.service.parameter.PushConfig;
import com.wolf.framework.service.parameter.PushHandler;
import com.wolf.framework.service.parameter.PushInfo;
import com.wolf.framework.service.parameter.PushInfoImpl;
import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.RequestInfo;
import com.wolf.framework.service.parameter.RequestInfoImpl;
import com.wolf.framework.service.parameter.ResponseInfo;
import com.wolf.framework.service.parameter.ResponseInfoImpl;
import com.wolf.framework.service.parameter.ServiceExtend;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import com.wolf.framework.service.parameter.RequestHandler;
import com.wolf.framework.service.parameter.ResponseDataType;
import com.wolf.framework.service.parameter.ResponseHandler;

/**
 *
 * @author jianying9
 */
public class ServiceContextImpl implements ServiceContext {

    private final String route;
    private final String desc;
    private final boolean requireTransaction;
    private final boolean validateSession;
    private final boolean validateSecurity;
    private final SessionHandleType sessionHandleType;
    private final String[] requiredParameter;
    private final String[] unrequiredParameter;
    private final String[] returnParameter;
    private final Map<String, RequestHandler> requestParameterHandlerMap;
    private final Map<String, ResponseHandler> responseParameterHandlerMap;
    private final Map<String, PushHandler> pushHandlerMap;
    private final List<RequestInfo> requestInfoList;
    private final List<ResponseInfo> responseInfoList;
    private final ResponseCode[] responseCodes;
    private final List<PushInfo> pushInfoList;
    private final boolean hasAsyncResponse;

    /**
     * 保留字段集合
     *
     * @return
     */
    public static Set<String> getReservedParamSet() {
        Set<String> wordSet = new HashSet<>(4, 1);
        //保留接口参数
        wordSet.add("route");
        wordSet.add("filters");
        wordSet.add("sid");
        wordSet.add("comet");
        wordSet.add("callback");
        return Collections.unmodifiableSet(wordSet);
    }

    /**
     * 保留code集合
     *
     * @return
     */
    public static Set<String> getReservedCodeSet() {
        Set<String> codeSet = new HashSet<>(4, 1);
        //保留接口参数
        codeSet.add("unlogin");
        codeSet.add("unmodifyed");
        codeSet.add("invalid");
        codeSet.add("denied");
        codeSet.add("notfound");
        codeSet.add("unsupport");
        codeSet.add("exception");
        codeSet.add("unknown");
        return Collections.unmodifiableSet(codeSet);
    }

    private List<RequestInfo> executeRequestExtend(ServiceExtend serviceExtend, List<RequestInfo> requestInfoList) {
        List<RequestInfo> resultList = Collections.EMPTY_LIST;
        if (requestInfoList.isEmpty() == false) {
            resultList = new ArrayList(requestInfoList.size());
            List<RequestInfo> extendRequestInfoList;
            List<RequestInfo> childList;
            for (RequestInfo requestInfo : requestInfoList) {
                if (requestInfo.getDataType() == RequestDataType.EXTEND) {
                    //外部参数
                    extendRequestInfoList = serviceExtend.getRequestExtend(requestInfo.getName());
                    if (extendRequestInfoList != null) {
                        resultList.addAll(extendRequestInfoList);
                    }
                } else {
                    //过滤子参数
                    childList = this.executeRequestExtend(serviceExtend, requestInfo.getChildList());
                    requestInfo.setChildList(childList);
                    resultList.add(requestInfo);
                }
            }
        }
        return resultList;
    }
    
    private List<ResponseInfo> executeResponseExtend(ServiceExtend serviceExtend, List<ResponseInfo> responseInfoList) {
        List<ResponseInfo> resultList = Collections.EMPTY_LIST;
        if (responseInfoList.isEmpty() == false) {
            resultList = new ArrayList(responseInfoList.size());
            List<ResponseInfo> extendRequestInfoList;
            List<ResponseInfo> childList;
            for (ResponseInfo responseInfo : responseInfoList) {
                if (responseInfo.getDataType() == ResponseDataType.EXTEND) {
                    //外部参数
                    extendRequestInfoList = serviceExtend.getResponseExtend(responseInfo.getName());
                    if (extendRequestInfoList != null) {
                        resultList.addAll(extendRequestInfoList);
                    }
                } else {
                    //过滤子参数
                    childList = this.executeResponseExtend(serviceExtend, responseInfo.getChildList());
                    responseInfo.setChildList(childList);
                    resultList.add(responseInfo);
                }
            }
        }
        return resultList;
    }
    
    

    public ServiceContextImpl(ServiceConfig serviceConfig, WorkerBuildContext workerBuildContext) {
        this.route = serviceConfig.route();
        this.desc = serviceConfig.desc();
        this.sessionHandleType = serviceConfig.sessionHandleType();
        this.requireTransaction = serviceConfig.requireTransaction();
        this.validateSession = serviceConfig.validateSession();
        this.validateSecurity = serviceConfig.validateSecurity();
        this.responseCodes = serviceConfig.responseCodes();
        final ServiceExtend serviceExtend = workerBuildContext.getServiceExtend();
        //
        List<RequestInfo> requestInfoResultList = new ArrayList(serviceConfig.requestConfigs().length);
        for (RequestConfig requestConfig : serviceConfig.requestConfigs()) {
            requestInfoResultList.add(new RequestInfoImpl(requestConfig));
        }
        //处理外部请求参数
        this.requestInfoList = this.executeRequestExtend(serviceExtend, requestInfoResultList);
        //
        List<ResponseInfo> responseInfoResultList = new ArrayList(serviceConfig.responseConfigs().length);
        for (ResponseConfig responseConfig : serviceConfig.responseConfigs()) {
            responseInfoResultList.add(new ResponseInfoImpl(responseConfig));
        }
        //处理外部响应参数
        this.responseInfoList = this.executeResponseExtend(serviceExtend, responseInfoResultList);
        //
        this.pushInfoList = new ArrayList(serviceConfig.pushConfigs().length);
        PushInfo tempPushInfo;
        List<ResponseInfo> pushResponseInfoResultList;
        for (PushConfig pushConfig : serviceConfig.pushConfigs()) {
            tempPushInfo = new PushInfoImpl(pushConfig);
            //处理外部请求参数
            pushResponseInfoResultList = this.executeResponseExtend(serviceExtend, tempPushInfo.getResponseInfoList());
            tempPushInfo.setResponseInfoList(pushResponseInfoResultList);
            this.pushInfoList.add(tempPushInfo);
        }
        //
        boolean asyncResponse = false;
        if (pushInfoList.isEmpty() == false) {
            asyncResponse = true;
        }
        this.hasAsyncResponse = asyncResponse;
        //
        Set<String> reservedParamSet = ServiceContextImpl.getReservedParamSet();
        Set<String> reservedCodeSet = ServiceContextImpl.getReservedCodeSet();
        //
        for (ResponseCode responseCode : this.responseCodes) {
            if (reservedCodeSet.contains(responseCode.code())) {
                //配置中存在保留code,抛出异常提示
                throw new RuntimeException("Error when read ServiceConfig. Cause: route[" + serviceConfig.route() + "] contain reserved code[".concat(responseCode.code()) + "]");
            }
        }
        //
        final List<RequestInfo> requiredRequestInfoList = new ArrayList<>(0);
        final List<RequestInfo> unrequiredRequestInfoList = new ArrayList<>(0);
        for (RequestInfo requestInfo : this.requestInfoList) {
            if (reservedParamSet.contains(requestInfo.getName())) {
                //配置中存在保留参数名,抛出异常提示
                throw new RuntimeException("Error when read ServiceConfig. Cause: route[" + serviceConfig.route() + "] contain reserved param[".concat(requestInfo.getName()) + "]");
            }
            if (requestInfo.isRequired()) {
                requiredRequestInfoList.add(requestInfo);
            } else {
                unrequiredRequestInfoList.add(requestInfo);
            }
        }
        //
        RequestHandler requestHandler;
        RequestHandlerBuilder requestHandlerBuilder;
        final Map<String, RequestHandler> requestParameterMap = new HashMap<>(this.requestInfoList.size(), 1);
        List<String> unrequiredNameList = new ArrayList<>(unrequiredRequestInfoList.size());
        for (RequestInfo requestInfo : unrequiredRequestInfoList) {
            requestHandlerBuilder = new RequestHandlerBuilder(requestInfo);
            requestHandler = requestHandlerBuilder.build();
            if (requestHandler != null) {
                requestParameterMap.put(requestInfo.getName(), requestHandler);
                unrequiredNameList.add(requestInfo.getName());
            }
        }
        //
        final String[] unrequiredNames = unrequiredNameList.toArray(new String[unrequiredNameList.size()]);
        this.unrequiredParameter = unrequiredNames;
        //
        List<String> requiredNameList = new ArrayList<>(requiredRequestInfoList.size());
        for (RequestInfo requestInfo : requiredRequestInfoList) {
            requestHandlerBuilder = new RequestHandlerBuilder(requestInfo);
            requestHandler = requestHandlerBuilder.build();
            if (requestHandler != null) {
                requestParameterMap.put(requestInfo.getName(), requestHandler);
                requiredNameList.add(requestInfo.getName());
            }
        }
        final String[] requiredNames = requiredNameList.toArray(new String[requiredNameList.size()]);
        this.requiredParameter = requiredNames;
        this.requestParameterHandlerMap = requestParameterMap;
        //
        ResponseHandler responseHandler;
        ResponseHandlerBuilder responseHandlerBuilder;
        //获取返回参数
        final Map<String, ResponseHandler> returnParameterMap;
        final String[] returnNames;
        if (this.responseInfoList.isEmpty() == false) {
            //
            List<String> returnNameList = new ArrayList<>(this.responseInfoList.size());
            returnParameterMap = new HashMap(this.responseInfoList.size(), 1);
            for (ResponseInfo responseInfo : this.responseInfoList) {
                responseHandlerBuilder = new ResponseHandlerBuilder(
                        responseInfo);
                responseHandler = responseHandlerBuilder.build();
                if (responseHandler != null) {
                    returnParameterMap.put(responseInfo.getName(), responseHandler);
                    returnNameList.add(responseInfo.getName());
                }
            }
            returnNames = returnNameList.toArray(new String[returnNameList.size()]);
        } else {
            returnParameterMap = Collections.EMPTY_MAP;
            returnNames = new String[0];
        }
        this.returnParameter = returnNames;
        this.responseParameterHandlerMap = returnParameterMap;
        //获取push配置
        final Map<String, PushHandler> pushMap;
        if (this.pushInfoList.isEmpty() == false) {
            pushMap = new HashMap(this.pushInfoList.size(), 1);
            Map<String, ResponseHandler> pushResponseHandlerMap;
            String[] pushReturnNames;
            List<String> pushReturnNameList;
            List<ResponseInfo> pushResponseInfoList;
            String pushRouteName;
            PushHandler pushHandler;
            for (PushInfo pushInfo : this.pushInfoList) {
                pushRouteName = pushInfo.getRoute();
                pushResponseInfoList = pushInfo.getResponseInfoList();
                pushReturnNameList = new ArrayList(pushResponseInfoList.size());
                pushResponseHandlerMap = new HashMap(pushResponseInfoList.size(), 1);
                for (ResponseInfo responseInfo : pushResponseInfoList) {
                    responseHandlerBuilder = new ResponseHandlerBuilder(
                            responseInfo);
                    responseHandler = responseHandlerBuilder.build();
                    if (responseHandler != null) {
                        pushResponseHandlerMap.put(responseInfo.getName(), responseHandler);
                        pushReturnNameList.add(responseInfo.getName());
                    }
                }
                pushReturnNames = pushReturnNameList.toArray(new String[pushReturnNameList.size()]);
                //
                pushHandler = new PushHandler(pushRouteName, pushReturnNames, pushResponseHandlerMap);
                pushMap.put(pushRouteName, pushHandler);
            }
        } else {
            pushMap = Collections.EMPTY_MAP;
        }
        this.pushHandlerMap = pushMap;
    }

    @Override
    public String route() {
        return this.route;
    }

    @Override
    public boolean requireTransaction() {
        return this.requireTransaction;
    }

    @Override
    public SessionHandleType sessionHandleType() {
        return this.sessionHandleType;
    }

    @Override
    public boolean validateSession() {
        return this.validateSession;
    }

    @Override
    public boolean validateSecurity() {
        return this.validateSecurity;
    }

    @Override
    public String desc() {
        return this.desc;
    }

    @Override
    public String[] requiredParameter() {
        return this.requiredParameter;
    }

    @Override
    public String[] unrequiredParameter() {
        return this.unrequiredParameter;
    }

    @Override
    public Map<String, RequestHandler> requestParameterHandlerMap() {
        return this.requestParameterHandlerMap;
    }

    @Override
    public String[] returnParameter() {
        return this.returnParameter;
    }

    @Override
    public Map<String, ResponseHandler> responseParameterHandlerMap() {
        return this.responseParameterHandlerMap;
    }

    @Override
    public List<RequestInfo> requestConfigs() {
        return this.requestInfoList;
    }

    @Override
    public List<ResponseInfo> responseConfigs() {
        return this.responseInfoList;
    }

    @Override
    public ResponseCode[] responseCodes() {
        return this.responseCodes;
    }

    @Override
    public boolean hasAsyncResponse() {
        return this.hasAsyncResponse;
    }

    @Override
    public List<PushInfo> pushConfigs() {
        return this.pushInfoList;
    }

    @Override
    public Map<String, PushHandler> pushHandlerMap() {
        return this.pushHandlerMap;
    }

    @Override
    public PushHandler getPushHandler(String route) {
        return this.pushHandlerMap.get(route);
    }
}
