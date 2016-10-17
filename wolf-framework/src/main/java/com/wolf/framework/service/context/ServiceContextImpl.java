package com.wolf.framework.service.context;

import com.wolf.framework.service.ResponseState;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.SessionHandleType;
import com.wolf.framework.service.parameter.ParameterContext;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.RequestParameterHandler;
import com.wolf.framework.service.parameter.RequestParameterHandlerBuilder;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.service.parameter.ResponseParameterHandlerBuilder;
import com.wolf.framework.worker.ServiceWorkerContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final String[] importantParameter;
    private final String[] minorParameter;
    private final String[] returnParameter;
    private final Map<String, RequestParameterHandler> requestParameterHandlerMap;
    private final Map<String, ResponseParameterHandler> responseParameterHandlerMap;
    private final RequestConfig[] requestConfigs;
    private final ResponseConfig[] responseConfigs;
    private final ResponseState[] responseStates;

    public ServiceContextImpl(ServiceConfig serviceConfig, boolean page, ServiceWorkerContext serviceWorkerContext) {
        this.route = serviceConfig.route();
        this.desc = serviceConfig.desc();
        this.group = serviceConfig.group();
        this.sessionHandleType = serviceConfig.sessionHandleType();
        this.page = page;
        this.requireTransaction = serviceConfig.requireTransaction();
        this.validateSession = serviceConfig.validateSession();
        this.validateSecurity = serviceConfig.validateSecurity();
        this.requestConfigs = serviceConfig.requestConfigs();
        this.responseConfigs = serviceConfig.responseConfigs();
        this.responseStates = serviceConfig.responseStates();
        //
        final List<RequestConfig> importantRequestConfigList = new ArrayList<RequestConfig>(0);
        final List<RequestConfig> minorRequestConfigList = new ArrayList<RequestConfig>(0);
        for (RequestConfig requestConfig : requestConfigs) {
            if (requestConfig.must()) {
                importantRequestConfigList.add(requestConfig);
            } else {
                minorRequestConfigList.add(requestConfig);
            }
        }
        //
        ParameterContext parameterContext = serviceWorkerContext.getParameterContext();
        RequestParameterHandler requestParameterHandler;
        RequestParameterHandlerBuilder requestParameterHandlerBuilder;
        final Map<String, RequestParameterHandler> requestParameterMap = new HashMap<String, RequestParameterHandler>(minorRequestConfigList.size(), 1);
        List<String> minorNameList = new ArrayList<String>(minorRequestConfigList.size());
        for (RequestConfig requestConfig : minorRequestConfigList) {
            requestParameterHandlerBuilder = new RequestParameterHandlerBuilder(
                    requestConfig,
                    parameterContext);
            requestParameterHandler = requestParameterHandlerBuilder.build();
            requestParameterMap.put(requestConfig.name(), requestParameterHandler);
            minorNameList.add(requestConfig.name());
        }
        final String[] minorNames = minorNameList.toArray(new String[minorNameList.size()]);
        this.minorParameter = minorNames;
        //
        List<String> importantNameList = new ArrayList<String>(importantRequestConfigList.size());
        for (RequestConfig requestConfig : importantRequestConfigList) {
            requestParameterHandlerBuilder = new RequestParameterHandlerBuilder(
                    requestConfig,
                    parameterContext);
            requestParameterHandler = requestParameterHandlerBuilder.build();
            requestParameterMap.put(requestConfig.name(), requestParameterHandler);
            importantNameList.add(requestConfig.name());
        }
        final String[] importantNames = importantNameList.toArray(new String[importantNameList.size()]);
        this.importantParameter = importantNames;
        this.requestParameterHandlerMap = requestParameterMap;
        //
        ResponseParameterHandler outputParameterHandler;
        ResponseParameterHandlerBuilder outputParameterHandlerBuilder;
        //获取返回参数
        final Map<String, ResponseParameterHandler> returnParameterMap;
        final String[] returnNames;
        if (this.responseConfigs.length > 0) {
            List<String> returnNameList = new ArrayList<String>(this.responseConfigs.length);
            returnParameterMap = new HashMap<String, ResponseParameterHandler>(this.responseConfigs.length, 1);
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
            returnParameterMap = new HashMap<String, ResponseParameterHandler>(0, 1);
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
    public String[] importantParameter() {
        return this.importantParameter;
    }

    @Override
    public String[] minorParameter() {
        return this.minorParameter;
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
    public ResponseState[] responseStates() {
        return this.responseStates;
    }
}
