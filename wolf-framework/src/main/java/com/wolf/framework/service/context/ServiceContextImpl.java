package com.wolf.framework.service.context;

import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.SessionHandleType;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.RequestParameterHandler;
import com.wolf.framework.service.parameter.RequestParameterHandlerBuilder;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.service.parameter.ResponseParameterHandlerBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.wolf.framework.worker.build.WorkerBuildContext;
import com.wolf.framework.service.ResponseCode;
import com.wolf.framework.service.parameter.PushConfig;
import com.wolf.framework.service.parameter.PushHandler;
import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.ResponseDataType;
import com.wolf.framework.service.parameter.ServiceExtend;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    private final Map<String, RequestParameterHandler> requestParameterHandlerMap;
    private final Map<String, ResponseParameterHandler> responseParameterHandlerMap;
    private final Map<String, PushHandler> pushHandlerMap;
    private final RequestConfig[] requestConfigs;
    private final ResponseConfig[] responseConfigs;
    private final ResponseCode[] responseCodes;
    private final PushConfig[] pushConfigs;
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

    public ServiceContextImpl(ServiceConfig serviceConfig, WorkerBuildContext workerBuildContext) {
        this.route = serviceConfig.route();
        this.desc = serviceConfig.desc();
        this.sessionHandleType = serviceConfig.sessionHandleType();
        this.requireTransaction = serviceConfig.requireTransaction();
        this.validateSession = serviceConfig.validateSession();
        this.validateSecurity = serviceConfig.validateSecurity();
        this.requestConfigs = serviceConfig.requestConfigs();
        this.responseConfigs = serviceConfig.responseConfigs();
        this.pushConfigs = serviceConfig.pushConfigs();
        this.responseCodes = serviceConfig.responseCodes();
        boolean asyncResponse = false;
        if (pushConfigs.length > 0) {
            asyncResponse = true;
        }
        this.hasAsyncResponse = asyncResponse;
        //
        final ServiceExtend serviceExtend = workerBuildContext.getServiceExtend();
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
        //处理外部参数
        List<RequestConfig> requestConfigList = new ArrayList(this.requestConfigs.length);
        List<RequestConfig> extendRequestConfigList;
        for (RequestConfig requestConfig : this.requestConfigs) {
            //判断是是否外部参数引用
            if (requestConfig.dataType() == RequestDataType.EXTEND && requestConfig.extendName().isEmpty() == false) {
                //为外部参数引用
                extendRequestConfigList = serviceExtend.getRequestExtend(requestConfig.extendName());
                if (extendRequestConfigList != null) {
                    for (RequestConfig extendrequestConfig : extendRequestConfigList) {
                        if (extendrequestConfig.dataType() != RequestDataType.EXTEND) {
                            requestConfigList.add(extendrequestConfig);
                        }
                    }
                }
            } else {
                requestConfigList.add(requestConfig);
            }
        }
        //
        final List<RequestConfig> requiredRequestConfigList = new ArrayList<>(0);
        final List<RequestConfig> unrequiredRequestConfigList = new ArrayList<>(0);
        for (RequestConfig requestConfig : requestConfigList) {
            if (reservedParamSet.contains(requestConfig.name())) {
                //配置中存在保留参数名,抛出异常提示
                throw new RuntimeException("Error when read ServiceConfig. Cause: route[" + serviceConfig.route() + "] contain reserved param[".concat(requestConfig.name()) + "]");
            }
            if (requestConfig.required()) {
                requiredRequestConfigList.add(requestConfig);
            } else {
                unrequiredRequestConfigList.add(requestConfig);
            }
        }
        //
        RequestParameterHandler requestParameterHandler;
        RequestParameterHandlerBuilder requestParameterHandlerBuilder;
        final Map<String, RequestParameterHandler> requestParameterMap = new HashMap<>(this.requestConfigs.length, 1);
        List<String> unrequiredNameList = new ArrayList<>(unrequiredRequestConfigList.size());
        for (RequestConfig requestConfig : unrequiredRequestConfigList) {
            requestParameterHandlerBuilder = new RequestParameterHandlerBuilder(requestConfig);
            requestParameterHandler = requestParameterHandlerBuilder.build();
            if (requestParameterHandler != null) {
                requestParameterMap.put(requestConfig.name(), requestParameterHandler);
                unrequiredNameList.add(requestConfig.name());
            }
        }
        //
        final String[] unrequiredNames = unrequiredNameList.toArray(new String[unrequiredNameList.size()]);
        this.unrequiredParameter = unrequiredNames;
        //
        List<String> requiredNameList = new ArrayList<>(requiredRequestConfigList.size());
        for (RequestConfig requestConfig : requiredRequestConfigList) {
            requestParameterHandlerBuilder = new RequestParameterHandlerBuilder(requestConfig);
            requestParameterHandler = requestParameterHandlerBuilder.build();
            if (requestParameterHandler != null) {
                requestParameterMap.put(requestConfig.name(), requestParameterHandler);
                requiredNameList.add(requestConfig.name());
            }
        }
        final String[] requiredNames = requiredNameList.toArray(new String[requiredNameList.size()]);
        this.requiredParameter = requiredNames;
        this.requestParameterHandlerMap = requestParameterMap;
        //
        ResponseParameterHandler responseParameterHandler;
        ResponseParameterHandlerBuilder responseParameterHandlerBuilder;
        //获取返回参数
        final Map<String, ResponseParameterHandler> returnParameterMap;
        final String[] returnNames;
        if (this.responseConfigs.length > 0) {
            //处理外部扩展的参数
            List<ResponseConfig> extendResponseConfigList;
            List<ResponseConfig> responseConfigList = new ArrayList(this.requestConfigs.length);
            for (ResponseConfig responseConfig : this.responseConfigs) {
                if (responseConfig.dataType() == ResponseDataType.EXTEND && responseConfig.extendName().isEmpty() == false) {
                    extendResponseConfigList = serviceExtend.getResponseExtend(responseConfig.extendName());
                    for (ResponseConfig extendResponseConfig : extendResponseConfigList) {
                        if(extendResponseConfig.dataType() != ResponseDataType.EXTEND) {
                            responseConfigList.add(extendResponseConfig);
                        }
                    }
                } else {
                    responseConfigList.add(responseConfig);
                }
            }
            //
            List<String> returnNameList = new ArrayList<>(responseConfigList.size());
            returnParameterMap = new HashMap(responseConfigList.size(), 1);
            for (ResponseConfig responseConfig : responseConfigList) {
                responseParameterHandlerBuilder = new ResponseParameterHandlerBuilder(
                        responseConfig);
                responseParameterHandler = responseParameterHandlerBuilder.build();
                if (responseParameterHandler != null) {
                    returnParameterMap.put(responseConfig.name(), responseParameterHandler);
                    returnNameList.add(responseConfig.name());
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
        if (this.pushConfigs.length > 0) {
            pushMap = new HashMap(this.pushConfigs.length, 1);
            Map<String, ResponseParameterHandler> pushResponseParameterMap;
            String[] pushReturnNames;
            List<String> pushReturnNameList;
            String pushRouteName;
            PushHandler pushHandler;
            for (PushConfig pushConfig : this.pushConfigs) {
                pushRouteName = pushConfig.route();
                pushReturnNameList = new ArrayList(pushConfig.responseConfigs().length);
                pushResponseParameterMap = new HashMap(pushConfig.responseConfigs().length, 1);
                for (ResponseConfig parameterConfig : pushConfig.responseConfigs()) {
                    responseParameterHandlerBuilder = new ResponseParameterHandlerBuilder(
                            parameterConfig);
                    responseParameterHandler = responseParameterHandlerBuilder.build();
                    if (responseParameterHandler != null) {
                        pushResponseParameterMap.put(parameterConfig.name(), responseParameterHandler);
                        pushReturnNameList.add(parameterConfig.name());
                    }
                }
                pushReturnNames = pushReturnNameList.toArray(new String[pushReturnNameList.size()]);
                //
                pushHandler = new PushHandler(pushRouteName, pushReturnNames, pushResponseParameterMap);
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
    public Map<String, RequestParameterHandler> requestParameterHandlerMap() {
        return this.requestParameterHandlerMap;
    }

    @Override
    public String[] returnParameter() {
        return this.returnParameter;
    }

    @Override
    public Map<String, ResponseParameterHandler> responseParameterHandlerMap() {
        return this.responseParameterHandlerMap;
    }

    @Override
    public RequestConfig[] requestConfigs() {
        return this.requestConfigs;
    }

    @Override
    public ResponseConfig[] responseConfigs() {
        return this.responseConfigs;
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
    public PushConfig[] pushConfigs() {
        return this.pushConfigs;
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
