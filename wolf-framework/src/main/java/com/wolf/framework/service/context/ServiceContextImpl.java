package com.wolf.framework.service.context;

import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.SessionHandleType;
import com.wolf.framework.service.parameter.ParameterContext;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jianying9
 */
public class ServiceContextImpl implements ServiceContext {

    private final String route;
    private final String group;
    private final String desc;
    private final boolean requireTransaction;
    private final boolean validateSession;
    private final boolean validateSecurity;
    private final boolean page;
    private final SessionHandleType sessionHandleType;
    private final String[] requiredParameter;
    private final String[] unrequiredParameter;
    private final String[] returnParameter;
    private final Map<String, RequestParameterHandler> requestParameterHandlerMap;
    private final Map<String, ResponseParameterHandler> responseParameterHandlerMap;
    private final RequestConfig[] requestConfigs;
    private final ResponseConfig[] responseConfigs;
    private final ResponseCode[] responseCodes;
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
        wordSet.add("nextIndex");
        wordSet.add("nextSize");
        wordSet.add("comet");
        wordSet.add("callback");
        return Collections.unmodifiableSet(wordSet);
    }
    
    /**
     * 保留code集合
     * @return 
     */
    public static Set<String> getReservedCodeSet() {
        Set<String> codeSet = new HashSet<>(4, 1);
        //保留接口参数
        codeSet.add("success");
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

    public ServiceContextImpl(ServiceConfig serviceConfig, boolean page, WorkerBuildContext workerBuildContext) {
        this.route = serviceConfig.route();
        this.desc = serviceConfig.desc();
        Pattern groupPattern = Pattern.compile("(?:/)([a-zA-Z]+)(?:/)");
        Matcher matcher = groupPattern.matcher(this.route);
        if(matcher.find()) {
            this.group = matcher.group(1);
        } else {
            this.group = "default";
        }
        this.sessionHandleType = serviceConfig.sessionHandleType();
        this.page = page;
        this.requireTransaction = serviceConfig.requireTransaction();
        this.validateSession = serviceConfig.validateSession();
        this.validateSecurity = serviceConfig.validateSecurity();
        this.requestConfigs = serviceConfig.requestConfigs();
        this.responseConfigs = serviceConfig.responseConfigs();
        this.responseCodes = serviceConfig.responseCodes();
        this.hasAsyncResponse = serviceConfig.hasAsyncResponse();
        //
        Set<String> reservedParamSet = ServiceContextImpl.getReservedParamSet();
        Set<String> reservedCodeSet = ServiceContextImpl.getReservedCodeSet();
        //
        for (ResponseCode responseCode : this.responseCodes) {
            if(reservedCodeSet.contains(responseCode.code())) {
                //配置中存在保留code,抛出异常提示
                throw new RuntimeException("Error when read ServiceConfig. Cause: route[" + serviceConfig.route() + "] contain reserved code[".concat(responseCode.code()) + "]");
            }
        }
        final List<RequestConfig> requiredRequestConfigList = new ArrayList<>(0);
        final List<RequestConfig> unrequiredRequestConfigList = new ArrayList<>(0);
        for (RequestConfig requestConfig : requestConfigs) {
            if(reservedParamSet.contains(requestConfig.name())) {
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
        ParameterContext parameterContext = workerBuildContext.getParameterContext();
        RequestParameterHandler requestParameterHandler;
        RequestParameterHandlerBuilder requestParameterHandlerBuilder;
        final Map<String, RequestParameterHandler> requestParameterMap = new HashMap<>(unrequiredRequestConfigList.size(), 1);
        List<String> unrequiredNameList = new ArrayList<>(unrequiredRequestConfigList.size());
        for (RequestConfig requestConfig : unrequiredRequestConfigList) {
            requestParameterHandlerBuilder = new RequestParameterHandlerBuilder(
                    requestConfig,
                    parameterContext);
            requestParameterHandler = requestParameterHandlerBuilder.build();
            requestParameterMap.put(requestConfig.name(), requestParameterHandler);
            unrequiredNameList.add(requestConfig.name());
        }
        //
        if(page) {
            unrequiredNameList.add("nextIndex");
            unrequiredNameList.add("nextSize");
            requestParameterMap.put("nextIndex", workerBuildContext.getNextIndexHandler());
            requestParameterMap.put("nextSize", workerBuildContext.getNextSizeHandler());
        }
        final String[] unrequiredNames = unrequiredNameList.toArray(new String[unrequiredNameList.size()]);
        this.unrequiredParameter = unrequiredNames;
        //
        List<String> requiredNameList = new ArrayList<>(requiredRequestConfigList.size());
        for (RequestConfig requestConfig : requiredRequestConfigList) {
            requestParameterHandlerBuilder = new RequestParameterHandlerBuilder(
                    requestConfig,
                    parameterContext);
            requestParameterHandler = requestParameterHandlerBuilder.build();
            requestParameterMap.put(requestConfig.name(), requestParameterHandler);
            requiredNameList.add(requestConfig.name());
        }
        final String[] requiredNames = requiredNameList.toArray(new String[requiredNameList.size()]);
        this.requiredParameter = requiredNames;
        this.requestParameterHandlerMap = requestParameterMap;
        //
        ResponseParameterHandler outputParameterHandler;
        ResponseParameterHandlerBuilder outputParameterHandlerBuilder;
        //获取返回参数
        final Map<String, ResponseParameterHandler> returnParameterMap;
        final String[] returnNames;
        if (this.responseConfigs.length > 0) {
            List<String> returnNameList = new ArrayList<>(this.responseConfigs.length);
            returnParameterMap = new HashMap<>(this.responseConfigs.length, 1);
            for (ResponseConfig parameterConfig : this.responseConfigs) {
                outputParameterHandlerBuilder = new ResponseParameterHandlerBuilder(
                        parameterConfig,
                        parameterContext);
                outputParameterHandler = outputParameterHandlerBuilder.build();
                returnParameterMap.put(parameterConfig.name(), outputParameterHandler);
                returnNameList.add(parameterConfig.name());
            }
            returnNames = returnNameList.toArray(new String[returnNameList.size()]);
        } else {
            returnParameterMap = new HashMap<>(0, 1);
            returnNames = new String[0];
        }
        this.returnParameter = returnNames;
        this.responseParameterHandlerMap = returnParameterMap;
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
    public boolean page() {
        return this.page;
    }

    @Override
    public String desc() {
        return this.desc;
    }

    @Override
    public String group() {
        return this.group;
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
}
