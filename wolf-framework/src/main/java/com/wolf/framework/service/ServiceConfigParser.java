package com.wolf.framework.service;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.data.DataHandler;
import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.data.DataTypeEnum;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.service.parameter.NumberParameterHandlerImpl;
import com.wolf.framework.service.parameter.ParameterContextBuilder;
import com.wolf.framework.service.parameter.ParameterHandler;
import com.wolf.framework.service.parameter.ParametersContext;
import com.wolf.framework.service.parameter.ParametersHandler;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.ServiceWorkerContext;
import com.wolf.framework.worker.ServiceWorkerImpl;
import com.wolf.framework.worker.workhandler.BroadcastMessageHandlerImpl;
import com.wolf.framework.worker.workhandler.CloseWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.CreateJsonMessageHandlerImpl;
import com.wolf.framework.worker.workhandler.CreatePageJsonMessageHandlerImpl;
import com.wolf.framework.worker.workhandler.DefaultWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.ExceptionWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.ImportantParameterWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.MinorParameterDefaultReplaceNullAndEmptyHandlerImpl;
import com.wolf.framework.worker.workhandler.MinorParameterDiscardEmptyHandlerImpl;
import com.wolf.framework.worker.workhandler.MinorParameterHandler;
import com.wolf.framework.worker.workhandler.MinorParameterKeepEmptyHandlerImpl;
import com.wolf.framework.worker.workhandler.MinorParameterWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.PageParameterWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.RemoveSessionWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.SaveNewSessionWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.SendMessageWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.TransactionWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.ValidateSessionWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.WorkHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;

/**
 * 负责解析annotation ServiceConfig
 *
 * @author aladdin
 */
public class ServiceConfigParser<K extends Service, T extends Entity> {

    private final ServiceWorkerContext serviceWorkerContext;
    private final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
    private final ParameterHandler pageIndexHandler;
    private final ParameterHandler pageSizeHandler;

    public ServiceConfigParser(ServiceWorkerContext serviceWorkerContext) {
        this.serviceWorkerContext = serviceWorkerContext;
        //初始化分页参数配置
        ParametersContext parametersContextBuilder = this.serviceWorkerContext.getParametersContextBuilder();
        ParameterContextBuilder parameterContextBuilder = parametersContextBuilder.getFieldContextBuilder();
        DataHandlerFactory dataHandlerFactory = parameterContextBuilder.getDataHandlerFactory();
        DataHandler intTypeHandler = dataHandlerFactory.getDataHandler(DataTypeEnum.INT);
        this.pageIndexHandler = new NumberParameterHandlerImpl(WorkHandler.PAGE_INDEX, intTypeHandler, "1", "页索引");
        this.pageSizeHandler = new NumberParameterHandlerImpl(WorkHandler.PAGE_SIZE, intTypeHandler, "15", "页大小");
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
            final String[] importantParameter = serviceConfig.importantParameter();
            final String[] minorParameter = serviceConfig.minorParameter();
            final MinorHandlerTypeEnum minorHandlerTypeEnum = serviceConfig.minorHandlerTypeEnum();
            final String[] returnParameter = serviceConfig.returnParameter();
            final Class<?>[] parametersConfigs = serviceConfig.parametersConfigs();
            final ParameterTypeEnum parameterTypeEnum = serviceConfig.parameterTypeEnum();
            final boolean page = serviceConfig.page();
            final boolean requireTransaction = serviceConfig.requireTransaction();
            final SessionHandleTypeEnum sessionHandleTypeEnum = serviceConfig.sessionHandleTypeEnum();
            final boolean response = serviceConfig.response();
            final boolean broadcast = serviceConfig.broadcast();
            final boolean validateSession = serviceConfig.validateSession();
            //获取字段处理对象集合
            final Map<String, ParameterHandler> fieldHandlerMapTemp = new HashMap<String, ParameterHandler>(2, 1);
            Set<Entry<String, ParameterHandler>> entrySet;
            Map<String, ParameterHandler> parameterHandlerMapTmp;
            //参数配置
            final ParametersContext parametersContextBuilder = this.serviceWorkerContext.getParametersContextBuilder();
            ParametersHandler parametersHandler;
            for (Class<?> parametersConfig : parametersConfigs) {
                parametersHandler = parametersContextBuilder.getParametersHandler(parametersConfig);
                //如果parametersConfig找不到，则抛出异常，停止加载
                if (parametersHandler == null) {
                    StringBuilder mesBuilder = new StringBuilder(512);
                    mesBuilder.append("There was an error parsing service worker. Cause: can not find parametersConfig handler : ").append(clazz);
                    mesBuilder.append("\n").append("error class is ").append(clazz.getName());
                    throw new RuntimeException(mesBuilder.toString());
                }
                parameterHandlerMapTmp = parametersHandler.getFieldHandlerMap();
                entrySet = parametersHandler.getFieldHandlerMap().entrySet();
                for (Entry<String, ParameterHandler> entry : entrySet) {
                    if (!fieldHandlerMapTemp.containsKey(entry.getKey())) {
                        fieldHandlerMapTemp.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            final Map<String, ParameterHandler> parameterHandlerMap = new HashMap<String, ParameterHandler>(2, 1);
            //获取必要参数处理对象
            ParameterHandler fieldHandler;
            for (String parameter : importantParameter) {
                fieldHandler = fieldHandlerMapTemp.get(parameter);
                if (fieldHandler == null) {
                    StringBuilder mesBuilder = new StringBuilder(512);
                    mesBuilder.append("There was an error parsing service worker. Cause: can not find important parameter config : ").append(parameter);
                    mesBuilder.append("\n").append("error class is ").append(clazz.getName());
                    throw new RuntimeException(mesBuilder.toString());
                }
                if (parameterHandlerMap.containsKey(parameter) == false) {
                    parameterHandlerMap.put(parameter, fieldHandler);
                }
            }
            //获取次要参数
            for (String parameter : minorParameter) {
                fieldHandler = fieldHandlerMapTemp.get(parameter);
                if (fieldHandler == null) {
                    StringBuilder mesBuilder = new StringBuilder(512);
                    mesBuilder.append("There was an error parsing service worker. Cause: can not find minor parameter config : ").append(parameter);
                    mesBuilder.append("\n").append("error class is ").append(clazz.getName());
                    throw new RuntimeException(mesBuilder.toString());
                }
                if (parameterHandlerMap.containsKey(parameter) == false) {
                    parameterHandlerMap.put(parameter, fieldHandler);
                }
            }
            //获取返回参数
            for (String parameter : returnParameter) {
                fieldHandler = fieldHandlerMapTemp.get(parameter);
                if (fieldHandler == null) {
                    StringBuilder mesBuilder = new StringBuilder(512);
                    mesBuilder.append("There was an error parsing service worker. Cause: can not find return parameter config : ").append(parameter);
                    mesBuilder.append("\n").append("error class is ").append(clazz.getName());
                    throw new RuntimeException(mesBuilder.toString());
                }
                if (parameterHandlerMap.containsKey(parameter) == false) {
                    parameterHandlerMap.put(parameter, fieldHandler);
                }
            }
            //开始生成业务处理链
            //实例化该clazz
            Service service = null;
            try {
                service = clazz.newInstance();
            } catch (Exception e) {
                this.logger.error("There was an error instancing class {}. Cause: {}", clazz.getName(), e.getMessage());
                throw new RuntimeException("There wa an error instancing class ".concat(clazz.getName()));
            }
            //注入相关对象
            Injecter injecter = this.serviceWorkerContext.getInjecter();
            injecter.parse(service);
            //包装服务类
            WorkHandler workHandler = new DefaultWorkHandlerImpl(service);
            //判断是否需要事务，如果需要则加入事务处理环节
            String compileModel = this.serviceWorkerContext.getCompileModel();
            if (requireTransaction && compileModel.equals(FrameworkConfig.SERVER)) {
                workHandler = new TransactionWorkHandlerImpl(workHandler);
            }
            //异常处理
            workHandler = new ExceptionWorkHandlerImpl(workHandler);
            //--------------------------------after------------------
            if (response || broadcast) {
                //是否有输出
                //生成消息
                if (page) {
                    workHandler = new CreatePageJsonMessageHandlerImpl(returnParameter, parameterHandlerMap, workHandler);
                } else {
                    workHandler = new CreateJsonMessageHandlerImpl(returnParameter, parameterHandlerMap, workHandler);
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
            //-----------------------before-----------------
            //是否获取分页参数
            if (page) {
                workHandler = new PageParameterWorkHandlerImpl(this.pageIndexHandler, this.pageSizeHandler, workHandler);
            }
            //判断取值验证类型,将对应处理对象加入到处理环节
            if (parameterTypeEnum == ParameterTypeEnum.PARAMETER) {
                //次要参数
                if (minorParameter.length > 0) {
                    MinorParameterHandler minorParameterHandler = null;
                    switch (minorHandlerTypeEnum) {
                        case KEEP_EMPTY:
                            minorParameterHandler = new MinorParameterKeepEmptyHandlerImpl(minorParameter, parameterHandlerMap);
                            break;
                        case DISCARD_EMPTY:
                            minorParameterHandler = new MinorParameterDiscardEmptyHandlerImpl(minorParameter, parameterHandlerMap);
                            break;
                        case DEFAULT_REPLACE_NULL_AND_EMPTY:
                            minorParameterHandler = new MinorParameterDefaultReplaceNullAndEmptyHandlerImpl(minorParameter, parameterHandlerMap);
                            break;
                    }
                    workHandler = new MinorParameterWorkHandlerImpl(minorParameterHandler, workHandler);
                }
                //重要参数
                if (importantParameter.length > 0) {
                    workHandler = new ImportantParameterWorkHandlerImpl(importantParameter, parameterHandlerMap, workHandler);
                }
            }
            //是否验证session
            if (validateSession) {
                workHandler = new ValidateSessionWorkHandlerImpl(workHandler);
            }
            //关闭连接
            workHandler = new CloseWorkHandlerImpl(workHandler);
            //创建对应的工作对象
            final ServiceWorker serviceWorker = new ServiceWorkerImpl(workHandler);
            this.serviceWorkerContext.putServiceWorker(actionName, serviceWorker, clazz.getName());
            this.logger.debug("--parse service {} finished--", clazz.getName());
        } else {
            this.logger.error("--parse service {} missing annotation ServiceConfig--", clazz.getName());
        }
    }
}
