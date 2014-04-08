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
import com.wolf.framework.service.parameter.InputConfig;
import com.wolf.framework.service.parameter.InputParameterHandler;
import com.wolf.framework.service.parameter.OutputParameterHandler;
import com.wolf.framework.service.parameter.ParameterContext;
import com.wolf.framework.service.parameter.InputParameterHandlerBuilder;
import com.wolf.framework.service.parameter.OutputConfig;
import com.wolf.framework.service.parameter.OutputParameterHandlerBuilder;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.ServiceWorkerContext;
import com.wolf.framework.worker.ServiceWorkerImpl;
import com.wolf.framework.worker.workhandler.BroadcastMessageHandlerImpl;
import com.wolf.framework.worker.workhandler.CreateJsonMessageHandlerImpl;
import com.wolf.framework.worker.workhandler.CreatePageJsonMessageHandlerImpl;
import com.wolf.framework.worker.workhandler.DefaultWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.ExceptionWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.ImportantParameterWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.MinorParameterWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.PageParameterWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.RemoveSessionWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.SaveNewSessionWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.SendMessageWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.TransactionWorkHandlerImpl;
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
 */
public class ServiceConfigParser<K extends Service, T extends Entity> {

    private final ServiceWorkerContext serviceWorkerContext;
    private final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
    private final InputParameterHandler pageIndexHandler;
    private final InputParameterHandler pageSizeHandler;

    public ServiceConfigParser(ServiceWorkerContext serviceWorkerContext) {
        this.serviceWorkerContext = serviceWorkerContext;
        //初始化分页参数配置
        ParameterContext parametersContext = this.serviceWorkerContext.getParameterContext();
        DataHandlerFactory dataHandlerFactory = parametersContext.getDataHandlerFactory();
        DataHandler intTypeHandler = dataHandlerFactory.getDataHandler(TypeEnum.INT);
        this.pageIndexHandler = new NumberParameterHandlerImpl(WorkHandler.PAGE_INDEX, intTypeHandler, "页索引");
        this.pageSizeHandler = new NumberParameterHandlerImpl(WorkHandler.PAGE_SIZE, intTypeHandler, "页大小");
    }

    /**
     * 解析方法
     *
     * @param clazz
     * @param serviceCtxBuilder
     */
    public void parse(final Class<K> clazz) {
        this.logger.debug("--parsing service {}--", clazz.getName());
        if (clazz.isAnnotationPresent(ServiceConfig.class)) {
            //1.获取注解ServiceConfig
            final ServiceConfig serviceConfig = clazz.getAnnotation(ServiceConfig.class);
            final String actionName = serviceConfig.actionName();
            final InputConfig[] importantParameter = serviceConfig.importantParameter();
            final InputConfig[] minorParameter = serviceConfig.minorParameter();
            final OutputConfig[] returnParameter = serviceConfig.returnParameter();
            final boolean page = serviceConfig.page();
            final boolean requireTransaction = serviceConfig.requireTransaction();
            final SessionHandleTypeEnum sessionHandleTypeEnum = serviceConfig.sessionHandleTypeEnum();
            final boolean response = serviceConfig.response();
            final boolean broadcast = serviceConfig.broadcast();
            final boolean validateSession = serviceConfig.validateSession();
            final String description = serviceConfig.description();
            final String group = serviceConfig.group();
            //开始生成业务处理链
            //实例化该clazz
            Service service = null;
            try {
                service = clazz.newInstance();
            } catch (Exception e) {
                this.logger.error("Error instancing class {}. Cause: {}", clazz.getName(), e.getMessage());
                throw new RuntimeException("There wa an error instancing class ".concat(clazz.getName()));
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
            OutputParameterHandler outputParameterHandler;
            OutputParameterHandlerBuilder outputParameterHandlerBuilder;
            if (response || broadcast) {
                //是否有输出
                //获取返回参数
                final Map<String, OutputParameterHandler> returnParameterMap;
                final String[] returnNames;
                if (returnParameter.length > 0) {
                    List<String> returnNameList = new ArrayList<String>(returnParameter.length);
                    returnParameterMap = new HashMap<String, OutputParameterHandler>(returnParameter.length, 1);
                    for (OutputConfig parameterConfig : returnParameter) {
                        outputParameterHandlerBuilder = new OutputParameterHandlerBuilder(
                                parameterConfig,
                                this.serviceWorkerContext.getApplicationContext(),
                                this.serviceWorkerContext.getParameterContext());
                        outputParameterHandler = outputParameterHandlerBuilder.build();
                        returnParameterMap.put(parameterConfig.name(), outputParameterHandler);
                        returnNameList.add(parameterConfig.name());
                    }
                    returnNames = returnNameList.toArray(new String[returnNameList.size()]);
                } else {
                    returnParameterMap = new HashMap<String, OutputParameterHandler>(0, 1);
                    returnNames = new String[0];
                }
                //生成消息
                if (page) {
                    workHandler = new CreatePageJsonMessageHandlerImpl(returnNames, returnParameterMap, workHandler);
                } else {
                    workHandler = new CreateJsonMessageHandlerImpl(returnNames, returnParameterMap, workHandler);
                }
                //是否响应消息
                if (response) {
                    workHandler = new SendMessageWorkHandlerImpl(workHandler);
                }
                //是否广播消息
                if (broadcast) {
                    workHandler = new BroadcastMessageHandlerImpl(workHandler);
                }
            }
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
            //是否获取分页参数
            if (page) {
                workHandler = new PageParameterWorkHandlerImpl(this.pageIndexHandler, this.pageSizeHandler, workHandler);
            }
            //判断取值验证类型,将对应处理对象加入到处理环节
            InputParameterHandler inputParameterHandler;
            InputParameterHandlerBuilder inputParameterHandlerBuilder;
            //次要参数
            if (minorParameter.length > 0) {
                //获取次要参数
                final Map<String, InputParameterHandler> minorParameterMap = new HashMap<String, InputParameterHandler>(minorParameter.length, 1);
                List<String> minorNameList = new ArrayList<String>(minorParameter.length);
                for (InputConfig parameterConfig : minorParameter) {
                    inputParameterHandlerBuilder = new InputParameterHandlerBuilder(
                            parameterConfig,
                            this.serviceWorkerContext.getApplicationContext(),
                            this.serviceWorkerContext.getParameterContext());
                    inputParameterHandler = inputParameterHandlerBuilder.build();
                    minorParameterMap.put(parameterConfig.name(), inputParameterHandler);
                    minorNameList.add(parameterConfig.name());
                }
                final String[] minorNames = minorNameList.toArray(new String[minorNameList.size()]);
                workHandler = new MinorParameterWorkHandlerImpl(minorNames, minorParameterMap, workHandler);
            }
            //重要参数
            if (importantParameter.length > 0) {
                final Map<String, InputParameterHandler> importantParameterMap = new HashMap<String, InputParameterHandler>(minorParameter.length, 1);
                List<String> importantNameList = new ArrayList<String>(importantParameter.length);
                for (InputConfig parameterConfig : importantParameter) {
                    inputParameterHandlerBuilder = new InputParameterHandlerBuilder(
                            parameterConfig,
                            this.serviceWorkerContext.getApplicationContext(),
                            this.serviceWorkerContext.getParameterContext());
                    inputParameterHandler = inputParameterHandlerBuilder.build();
                    importantParameterMap.put(parameterConfig.name(), inputParameterHandler);
                    importantNameList.add(parameterConfig.name());
                }
                final String[] importantNames = importantNameList.toArray(new String[importantNameList.size()]);
                workHandler = new ImportantParameterWorkHandlerImpl(importantNames, importantParameterMap, workHandler);
            }
            //是否验证session
            if (validateSession) {
                workHandler = new ValidateSessionWorkHandlerImpl(workHandler);
            }
            //创建对应的工作对象
            ServiceWorkerImpl serviceWorkerImpl = new ServiceWorkerImpl(workHandler);
            //INFO,开发模式才能会返回接口信息
            if (compileModel.equals(FrameworkConfig.DEVELOPMENT)) {
                serviceWorkerImpl.createInfo(actionName, group, description, importantParameter, minorParameter, returnParameter);
            }
            final ServiceWorker serviceWorker = serviceWorkerImpl;
            this.serviceWorkerContext.putServiceWorker(actionName, serviceWorker, clazz.getName());
            this.logger.debug("--parse service {} finished--", clazz.getName());
        } else {
            this.logger.error("--parse service {} missing annotation ServiceConfig--", clazz.getName());
        }
    }
}
