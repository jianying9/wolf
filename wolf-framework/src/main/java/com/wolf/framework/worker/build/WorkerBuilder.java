package com.wolf.framework.worker.build;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import static com.wolf.framework.service.SessionHandleType.REMOVE;
import static com.wolf.framework.service.SessionHandleType.SAVE;
import com.wolf.framework.service.context.ServiceContext;
import com.wolf.framework.service.context.ServiceContextImpl;
import com.wolf.framework.worker.ServiceWorker;
import com.wolf.framework.worker.ServiceWorkerImpl;
import com.wolf.framework.worker.workhandler.DefaultServiceWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.RequiredParameterWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.InterceptorWorkHandlerImpl;
import com.wolf.framework.worker.workhandler.UnrequiredParameterWorkHandlerImpl;
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
    public void build(final Class<Service> clazz) {
        this.logger.debug("--parsing service {}--", clazz.getName());
        if (clazz.isAnnotationPresent(ServiceConfig.class)) {
            //1.获取注解ServiceConfig
            final ServiceConfig serviceConfig = clazz.getAnnotation(ServiceConfig.class);
            //构造服务上下文信息
            ServiceContext serviceContext = new ServiceContextImpl(serviceConfig, this.workerBuildContext);
            //开始生成业务处理链
            //注入相关对象
            Injecter injecter = this.workerBuildContext.getInjecter();
            //根据接口类型实例化并注入相关对象
            //包装服务类
            WorkHandler workHandler = null;
            //对象类型
            try {
                Service service = clazz.newInstance();
                injecter.parse(service);
                workHandler = new DefaultServiceWorkHandlerImpl(service);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
            //判断是否加入拦截环节
            if (this.workerBuildContext.getInterceptorList().isEmpty() == false) {
                workHandler = new InterceptorWorkHandlerImpl(workHandler, this.workerBuildContext.getInterceptorList());
            }
            //判断是否需要事务，如果需要则加入事务处理环节
            ApplicationContext applicationContext = this.workerBuildContext.getApplicationContext();
            String compileModel = applicationContext.getParameter(FrameworkConfig.COMPILE_MODEL);
            if (serviceContext.requireTransaction() && compileModel.equals(FrameworkConfig.SERVER)) {
                workHandler = new TransactionWorkHandlerImpl(workHandler);
            }
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
            if (serviceContext.unrequiredParameter().length > 0) {
                //获取次要参数
                workHandler = new UnrequiredParameterWorkHandlerImpl(workHandler, serviceContext);
            }
            //重要参数
            if (serviceContext.requiredParameter().length > 0) {
                workHandler = new RequiredParameterWorkHandlerImpl(workHandler, serviceContext);
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
            if (compileModel.equals(FrameworkConfig.DEVELOPMENT) || compileModel.equals(FrameworkConfig.UNIT_TEST)) {
                serviceWorker.createInfo();
            }
            this.workerBuildContext.putServiceWorker(serviceContext.route(), serviceWorker, clazz.getName());
            this.logger.debug("--parse service {} finished--", clazz.getName());
        } else {
            this.logger.error("--parse service {} missing annotation ServiceConfig--", clazz.getName());
        }
    }
}
