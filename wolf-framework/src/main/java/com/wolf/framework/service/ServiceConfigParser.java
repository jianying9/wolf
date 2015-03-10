package com.wolf.framework.service;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.data.DataHandler;
import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.data.TypeEnum;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.service.parameter.NumberParameterHandlerImpl;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.RequestParameterHandler;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.service.parameter.ParameterContext;
import com.wolf.framework.service.parameter.RequestParameterHandlerBuilder;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.service.parameter.ResponseParameterHandlerBuilder;
import com.wolf.framework.worker.PageServiceWorkerImpl;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.ServiceWorkerContext;
import com.wolf.framework.worker.ServiceWorkerImpl;
import com.wolf.framework.worker.workhandler.DefaultWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.ExceptionWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.ImportantParameterWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.MinorParameterWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.RemoveSessionWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.SaveNewSessionWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.TransactionWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.ValidateSecurityWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.ValidateSessionWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.WorkHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

/**
 * 负责解析annotation ServiceConfig
 *
 * @author aladdin
 * @param <K>
 */
public class ServiceConfigParser<K extends Service> {

    private final ServiceWorkerContext serviceWorkerContext;
    private final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
    private final RequestParameterHandler pageIndexHandler;
    private final RequestParameterHandler pageSizeHandler;

    public ServiceConfigParser(ServiceWorkerContext serviceWorkerContext) {
        this.serviceWorkerContext = serviceWorkerContext;
        //初始化分页参数配置
        ParameterContext parametersContext = this.serviceWorkerContext.getParameterContext();
        DataHandlerFactory dataHandlerFactory = parametersContext.getDataHandlerFactory();
        DataHandler intTypeHandler = dataHandlerFactory.getDataHandler(TypeEnum.INT);
        this.pageIndexHandler = new NumberParameterHandlerImpl(WorkHandler.PAGE_INDEX, intTypeHandler);
        this.pageSizeHandler = new NumberParameterHandlerImpl(WorkHandler.PAGE_SIZE, intTypeHandler);
    }

    /**
     * 解析方法
     *
     * @param clazz
     */
    public void parse(final Class<K> clazz) {
        this.logger.debug("--parsing service {}--", clazz.getName());
        if (clazz.isAnnotationPresent(ServiceConfig.class)) {
            //1.获取注解ServiceConfig
            final ServiceConfig serviceConfig = clazz.getAnnotation(ServiceConfig.class);
            final String route = serviceConfig.route();
            final RequestConfig[] requestConfigs = serviceConfig.requestConfigs();
            final List<RequestConfig> importantRequestConfigList = new ArrayList<RequestConfig>(requestConfigs.length);
            final List<RequestConfig> minorRequestConfigList = new ArrayList<RequestConfig>(requestConfigs.length);
            for (RequestConfig requestConfig : requestConfigs) {
                if (requestConfig.must()) {
                    importantRequestConfigList.add(requestConfig);
                } else {
                    minorRequestConfigList.add(requestConfig);
                }
            }
            final ResponseConfig[] reponseConfigs = serviceConfig.responseConfigs();
            final boolean page = serviceConfig.page();
            final boolean requireTransaction = serviceConfig.requireTransaction();
            final SessionHandleTypeEnum sessionHandleTypeEnum = serviceConfig.sessionHandleTypeEnum();
            final boolean validateSession = serviceConfig.validateSession();
            final boolean validateSecurity = serviceConfig.validateSecurity();
            final String desc = serviceConfig.desc();
            final String group = serviceConfig.group();
            final ResponseState[] responseStates = serviceConfig.responseStates();
            //开始生成业务处理链
            //实例化该clazz
            Service service = null;
            try {
                service = clazz.newInstance();
            } catch (InstantiationException e) {
                this.logger.error("Error when instancing class {}. Cause: {}", clazz.getName(), e.getMessage());
                throw new RuntimeException("InstantiationException when instancing class ".concat(clazz.getName()));
            } catch (IllegalAccessException e) {
                this.logger.error("Error when instancing class {}. Cause: {}", clazz.getName(), e.getMessage());
                throw new RuntimeException("IllegalAccessException when instancing class ".concat(clazz.getName()));
            }
            //注入相关对象
            Injecter injecter = this.serviceWorkerContext.getInjecter();
            injecter.parse(service);
            //包装服务类
            WorkHandler workHandler = new DefaultWorkHandlerImpl(service);
            //判断是否需要事务，如果需要则加入事务处理环节
            ApplicationContext applicationContext = this.serviceWorkerContext.getApplicationContext();
            String compileModel = applicationContext.getParameter(FrameworkConfig.COMPILE_MODEL);
            if (requireTransaction && compileModel.equals(FrameworkConfig.SERVER)) {
                workHandler = new TransactionWorkHandlerImpl(workHandler);
            }
            //异常处理
            workHandler = new ExceptionWorkHandlerImpl(workHandler);
            //--------------------------------业务执行后处理环节------------------
            //session处理
            switch (sessionHandleTypeEnum) {
                case SAVE:
                    workHandler = new SaveNewSessionWorkHandlerImpl(workHandler);
                    break;
                case REMOVE:
                    workHandler = new RemoveSessionWorkHandlerImpl(workHandler);
                    break;
            }
            //-----------------------业务执行前处理环节-----------------
            //判断取值验证类型,将对应处理对象加入到处理环节
            RequestParameterHandler requestParameterHandler;
            RequestParameterHandlerBuilder requestParameterHandlerBuilder;
            //次要参数
            if (minorRequestConfigList.isEmpty() == false) {
                //获取次要参数
                final Map<String, RequestParameterHandler> minorParameterMap = new HashMap<String, RequestParameterHandler>(minorRequestConfigList.size(), 1);
                List<String> minorNameList = new ArrayList<String>(minorRequestConfigList.size());
                for (RequestConfig requestConfig : minorRequestConfigList) {
                    requestParameterHandlerBuilder = new RequestParameterHandlerBuilder(
                            requestConfig,
                            this.serviceWorkerContext.getApplicationContext(),
                            this.serviceWorkerContext.getParameterContext());
                    requestParameterHandler = requestParameterHandlerBuilder.build();
                    minorParameterMap.put(requestConfig.name(), requestParameterHandler);
                    minorNameList.add(requestConfig.name());
                }
                final String[] minorNames = minorNameList.toArray(new String[minorNameList.size()]);
                workHandler = new MinorParameterWorkHandlerImpl(minorNames, minorParameterMap, workHandler);
            }
            //重要参数
            if (importantRequestConfigList.isEmpty() == false) {
                final Map<String, RequestParameterHandler> importantParameterMap = new HashMap<String, RequestParameterHandler>(importantRequestConfigList.size(), 1);
                List<String> importantNameList = new ArrayList<String>(importantRequestConfigList.size());
                for (RequestConfig requestConfig : importantRequestConfigList) {
                    requestParameterHandlerBuilder = new RequestParameterHandlerBuilder(
                            requestConfig,
                            this.serviceWorkerContext.getApplicationContext(),
                            this.serviceWorkerContext.getParameterContext());
                    requestParameterHandler = requestParameterHandlerBuilder.build();
                    importantParameterMap.put(requestConfig.name(), requestParameterHandler);
                    importantNameList.add(requestConfig.name());
                }
                final String[] importantNames = importantNameList.toArray(new String[importantNameList.size()]);
                workHandler = new ImportantParameterWorkHandlerImpl(importantNames, importantParameterMap, workHandler);
            }
            //是否验证session
            if (validateSession) {
                workHandler = new ValidateSessionWorkHandlerImpl(workHandler);
            }
            //是否验证访问来源是否安全
            if (validateSecurity) {
                String key = ApplicationContext.CONTEXT.getParameter(FrameworkConfig.SEED_DES_KEY);
                long error = 180000;
                String errorText = ApplicationContext.CONTEXT.getParameter(FrameworkConfig.SEED_ERROR);
                if (errorText != null) {
                    error = Long.parseLong(errorText);
                }
                workHandler = new ValidateSecurityWorkHandlerImpl(key, error, workHandler);
            }
            ResponseParameterHandler outputParameterHandler;
            ResponseParameterHandlerBuilder outputParameterHandlerBuilder;
            //获取返回参数
            final Map<String, ResponseParameterHandler> returnParameterMap;
            final String[] returnNames;
            if (reponseConfigs.length > 0) {
                List<String> returnNameList = new ArrayList<String>(reponseConfigs.length);
                returnParameterMap = new HashMap<String, ResponseParameterHandler>(reponseConfigs.length, 1);
                for (ResponseConfig parameterConfig : reponseConfigs) {
                    outputParameterHandlerBuilder = new ResponseParameterHandlerBuilder(
                            parameterConfig,
                            this.serviceWorkerContext.getApplicationContext(),
                            this.serviceWorkerContext.getParameterContext());
                    outputParameterHandler = outputParameterHandlerBuilder.build();
                    returnParameterMap.put(parameterConfig.name(), outputParameterHandler);
                    returnNameList.add(parameterConfig.name());
                }
                returnNames = returnNameList.toArray(new String[returnNameList.size()]);
            } else {
                returnParameterMap = new HashMap<String, ResponseParameterHandler>(0, 1);
                returnNames = new String[0];
            }
            //创建对应的工作对象
            final ServiceWorker serviceWorker;
            if (page) {
                serviceWorker = new PageServiceWorkerImpl(this.pageIndexHandler, this.pageSizeHandler, returnNames, returnParameterMap, workHandler);
            } else {
                serviceWorker = new ServiceWorkerImpl(returnNames, returnParameterMap, workHandler);
            }
            //INFO,开发模式才能会返回接口信息
            if (compileModel.equals(FrameworkConfig.DEVELOPMENT) || compileModel.equals(FrameworkConfig.UNIT_TEST)) {
                serviceWorker.createInfo(route, page, validateSession, group, desc, requestConfigs, reponseConfigs, responseStates);
            }
            this.serviceWorkerContext.putServiceWorker(route, serviceWorker, clazz.getName());
            this.logger.debug("--parse service {} finished--", clazz.getName());
        } else {
            this.logger.error("--parse service {} missing annotation ServiceConfig--", clazz.getName());
        }
    }
}
