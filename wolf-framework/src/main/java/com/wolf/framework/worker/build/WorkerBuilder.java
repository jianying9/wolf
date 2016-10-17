package com.wolf.framework.worker.build;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.service.ListService;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import static com.wolf.framework.service.SessionHandleType.REMOVE;
import static com.wolf.framework.service.SessionHandleType.SAVE;
import com.wolf.framework.service.context.ServiceContext;
import com.wolf.framework.service.context.ServiceContextImpl;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.ServiceWorkerImpl;
import com.wolf.framework.worker.workhandler.ObjectServiceWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.ExceptionWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.ImportantParameterWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.ListServiceWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.MinorParameterWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.RemoveSessionWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.SaveNewSessionWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.TransactionWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.ValidateSessionWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.WorkHandler;
import org.slf4j.Logger;

/**
 * 负责解析annotation ServiceConfig
 *
 * @author jianying9
 */
public class WorkerBuilder {

    private final WorkerBuildContext workerBuildContext;
    private final Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);

    public WorkerBuilder(WorkerBuildContext workerBuildContext) {
        this.workerBuildContext = workerBuildContext;
    }

    /**
     * 解析方法
     *
     * @param clazz
     */
    public void build(final Class<?> clazz) {
        this.logger.debug("--parsing service {}--", clazz.getName());
        if (clazz.isAnnotationPresent(ServiceConfig.class)) {
            //1.获取注解ServiceConfig
            final ServiceConfig serviceConfig = clazz.getAnnotation(ServiceConfig.class);
            boolean page = false;
            if (Service.class.isAssignableFrom(clazz)) {
                //对象类型
                page = false;
            } else if (Service.class.isAssignableFrom(clazz)) {
                //集合类型
                page = true;
            }
            //构造服务上下文信息
            ServiceContext serviceContext = new ServiceContextImpl(serviceConfig, page, this.workerBuildContext);
            //开始生成业务处理链
            //注入相关对象
            Injecter injecter = this.workerBuildContext.getInjecter();
            //根据接口类型实例化并注入相关对象
            //包装服务类
            WorkHandler workHandler = null;
            try {
                if (Service.class.isAssignableFrom(clazz)) {
                    //对象类型
                    Service service = (Service) clazz.newInstance();
                    injecter.parse(service);
                    workHandler = new ObjectServiceWorkHandlerImpl(service, serviceContext);
                } else if (Service.class.isAssignableFrom(clazz)) {
                    //集合类型
                    ListService listService = (ListService) clazz.newInstance();
                    injecter.parse(listService);
                    workHandler = new ListServiceWorkHandlerImpl(listService, serviceContext);
                }
            } catch (InstantiationException ex) {
            } catch (IllegalAccessException ex) {
            }
            //判断是否需要事务，如果需要则加入事务处理环节
            ApplicationContext applicationContext = this.workerBuildContext.getApplicationContext();
            String compileModel = applicationContext.getParameter(FrameworkConfig.COMPILE_MODEL);
            if (serviceContext.requireTransaction() && compileModel.equals(FrameworkConfig.SERVER)) {
                workHandler = new TransactionWorkHandlerImpl(workHandler);
            }
            //异常处理
            workHandler = new ExceptionWorkHandlerImpl(workHandler);
            //--------------------------------业务执行后处理环节------------------
            //session处理
            switch (serviceContext.sessionHandleType()) {
                case SAVE:
                    workHandler = new SaveNewSessionWorkHandlerImpl(workHandler);
                    break;
                case REMOVE:
                    workHandler = new RemoveSessionWorkHandlerImpl(workHandler);
                    break;
            }
            //-----------------------业务执行前处理环节-----------------
            //判断取值验证类型,将对应处理对象加入到处理环节

            //次要参数
            if (serviceContext.minorParameter().length > 0) {
                //获取次要参数
                workHandler = new MinorParameterWorkHandlerImpl(workHandler, serviceContext);
            }
            //重要参数
            if (serviceContext.importantParameter().length > 0) {
                workHandler = new ImportantParameterWorkHandlerImpl(workHandler, serviceContext);
            }
            //是否验证session
            if (serviceContext.validateSession()) {
                workHandler = new ValidateSessionWorkHandlerImpl(workHandler);
            }
            //是否验证访问来源是否安全
            if (serviceContext.validateSecurity()) {
                //todo 验证访问来源是否安全
            }
            //创建对应的工作对象
            final ServiceWorker serviceWorker = new ServiceWorkerImpl(workHandler, serviceContext);
            //INFO,开发模式才能会返回接口信息
            if (compileModel.equals(FrameworkConfig.DEVELOPMENT)) {
                serviceWorker.createInfo();
            }
            this.workerBuildContext.putServiceWorker(serviceContext.route(), serviceWorker, clazz.getName());
            this.logger.debug("--parse service {} finished--", clazz.getName());
        } else {
            this.logger.error("--parse service {} missing annotation ServiceConfig--", clazz.getName());
        }
    }
}
