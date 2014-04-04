package com.wolf.framework.context;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.REntityDaoContext;
import com.wolf.framework.dao.REntityDaoContextImpl;
import com.wolf.framework.dao.annotation.RDaoConfig;
import com.wolf.framework.dao.parser.RDaoConfigParser;
import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.data.DataHandlerFactoryImpl;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.injecter.InjecterListImpl;
import com.wolf.framework.injecter.LocalServiceInjecterImpl;
import com.wolf.framework.injecter.RDaoInjecterImpl;
import com.wolf.framework.injecter.TaskExecutorInjecterImpl;
import com.wolf.framework.local.Local;
import com.wolf.framework.local.LocalServiceConfig;
import com.wolf.framework.local.LocalServiceConfigParser;
import com.wolf.framework.local.LocalServiceContext;
import com.wolf.framework.local.LocalServiceContextImpl;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.paser.ClassParser;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.ServiceConfigParser;
import com.wolf.framework.service.parameter.ParameterContext;
import com.wolf.framework.service.parameter.ParameterContextImpl;
import com.wolf.framework.task.TaskExecutor;
import com.wolf.framework.task.TaskExecutorImpl;
import com.wolf.framework.task.TaskExecutorUnitTestImpl;
import com.wolf.framework.worker.ServiceWorkerContext;
import com.wolf.framework.worker.ServiceWorkerContextImpl;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

/**
 * 全局上下文对象构造函数抽象类
 *
 * @author aladdin
 */
public class ApplicationContextBuilder<T extends Entity, K extends Service> {

    protected final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
    protected final List<Class<T>> rEntityClassList = new ArrayList<Class<T>>();
    protected final List<Class<K>> serviceClassList = new ArrayList<Class<K>>();
    protected final List<Class<Local>> localServiceClassList = new ArrayList<Class<Local>>();
    protected final List<Class<?>> allClassList = new ArrayList<Class<?>>();
    protected REntityDaoContext<T> rEntityDaoContext;
    protected ServiceWorkerContext serviceWorkerContext;
    private final Map<String, String> parameterMap;

    public ApplicationContextBuilder(Map<String, String> parameterMap) {
        this.parameterMap = parameterMap;
    }

    public final String getParameter(String name) {
        return this.parameterMap.get(name);
    }

    public final void build() {
        //将运行参数保存至全局上下文对象
        ApplicationContext.CONTEXT.setParameterMap(this.parameterMap);
        //检测服务器hostname的ip不能为127.0.0.1,否则提供rmi远程调用类服务时会出现异常
        String ip = null;
        try {
            //获取本地ip
            InetAddress address = InetAddress.getLocalHost();
            ip = address.getHostAddress();
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
        if (ip == null || ip.equals("127.0.0.1")) {
            throw new RuntimeException("Error. 127.0.0.1 invalid hostname-ip...please change it.");
        }
        //获取运行模式
        String compileModel = this.getParameter(FrameworkConfig.COMPILE_MODEL);
        if (compileModel == null) {
            compileModel = FrameworkConfig.SERVER;
        }
        //查找注解类
        this.logger.info("Finding annotation...");
        String packages = this.getParameter(FrameworkConfig.ANNOTATION_SCAN_PACKAGES);
        if (packages != null) {
            String[] packageNames = packages.split(",");
            final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            final List<String> classNameList = new ClassParser().findClass(classloader, packageNames);
            try {
                for (String className : classNameList) {
                    this.parseClass(classloader, className);
                }
            } catch (ClassNotFoundException e) {
                this.logger.error("Error when find annotation. Cause:", e);
            }
        }
        //初始化任务处理对象
        this.logger.info("Start task executer...");
        TaskExecutor taskExecutor;
        if (compileModel.equals(FrameworkConfig.UNIT_TEST)) {
            taskExecutor = new TaskExecutorUnitTestImpl();
        } else {
            int corePoolSize;
            String corePoolSizeStr = this.getParameter(FrameworkConfig.TASK_CORE_POOL_SIZE);
            if (corePoolSizeStr == null) {
                corePoolSize = 10;
            } else {
                corePoolSize = Integer.parseInt(corePoolSizeStr);
            }
            int maxPoolSize;
            String maxPoolSizeStr = this.getParameter(FrameworkConfig.TASK_MAX_POOL_SIZE);
            if (maxPoolSizeStr == null) {
                maxPoolSize = 10;
            } else {
                maxPoolSize = Integer.parseInt(maxPoolSizeStr);
            }
            taskExecutor = new TaskExecutorImpl(corePoolSize, maxPoolSize);
        }
        //解析redis EntityDao
        if (this.rEntityClassList.isEmpty() == false) {
            this.logger.info("parsing annotation RDaoConfig...");
            this.rEntityDaoContext = new REntityDaoContextImpl<T>(ApplicationContext.CONTEXT);
            final RDaoConfigParser<T> rEntityConfigDaoParser = new RDaoConfigParser<T>(this.rEntityDaoContext);
            for (Class<T> clazz : this.rEntityClassList) {
                rEntityConfigDaoParser.parse(clazz);
            }
        }
        //解析LocalService
        this.logger.info("parsing annotation LocalServiceConfig...");
        final LocalServiceContext localServiceContextBuilder = new LocalServiceContextImpl();
        final LocalServiceConfigParser localServiceConfigParser = new LocalServiceConfigParser(localServiceContextBuilder);
        for (Class<Local> clazz : this.localServiceClassList) {
            localServiceConfigParser.parse(clazz);
        }
        this.logger.info("parse annotation LocalServiceConfig finished.");
        //RDAO注入管理对象
        final Injecter rDaoInjecter = new RDaoInjecterImpl(this.rEntityDaoContext);
        //LocalService注入管理对象
        final Injecter localServiceInjecter = new LocalServiceInjecterImpl(localServiceContextBuilder);
        //TaskExecutor注入管理对象
        final Injecter taskExecutorInjecter = new TaskExecutorInjecterImpl(taskExecutor);
        //创建复合注入解析对象
        InjecterListImpl injecterListImpl = new InjecterListImpl();
        injecterListImpl.addInjecter(rDaoInjecter);
        injecterListImpl.addInjecter(localServiceInjecter);
        injecterListImpl.addInjecter(taskExecutorInjecter);
        final Injecter injecterList = injecterListImpl;
        //对LocalService进行注入
        localServiceContextBuilder.inject(injecterList);
        //初始化data类型工厂对象
        final DataHandlerFactory dataHanlderFactory = new DataHandlerFactoryImpl();
        ParameterContext parametersContext = new ParameterContextImpl(dataHanlderFactory, ApplicationContext.CONTEXT);
        //解析ServiceConfig
        this.logger.info("parsing annotation ServiceConfig...");
        injecterListImpl = new InjecterListImpl();
        injecterListImpl.addInjecter(localServiceInjecter);
        injecterListImpl.addInjecter(taskExecutorInjecter);
        this.serviceWorkerContext = new ServiceWorkerContextImpl(
                injecterListImpl,
                parametersContext,
                ApplicationContext.CONTEXT);
        final ServiceConfigParser<K, T> serviceConfigParser = new ServiceConfigParser<K, T>(this.serviceWorkerContext);
        for (Class<K> clazz : this.serviceClassList) {
            serviceConfigParser.parse(clazz);
        }
        ApplicationContext.CONTEXT.setServiceWorkerMap(this.serviceWorkerContext.getServiceWorkerMap());
        this.logger.info("parse annotation ServiceConfig finished.");
        ApplicationContext.CONTEXT.ready();
    }

    /**
     * 获取具有annotation的class,并放入特定的队列
     *
     * @param classloader
     * @param className
     * @throws ClassNotFoundException
     */
    private void parseClass(final ClassLoader classloader, final String className) throws ClassNotFoundException {
        Class<?> clazz = classloader.loadClass(className);
        Class<T> clazzt;
        Class<K> clazzk;
        Class<Local> clazzl;
        this.allClassList.add(clazz);
        //是否是实体
        if (Entity.class.isAssignableFrom(clazz)) {
            clazzt = (Class<T>) clazz;
            if (clazzt.isAnnotationPresent(RDaoConfig.class)) {
                if (this.rEntityClassList.contains(clazzt) == false) {
                    this.rEntityClassList.add(clazzt);
                    this.logger.debug("find redis entity class ".concat(className));
                }
            }
        }
        //是否是服务
        if (Service.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(ServiceConfig.class)) {
            clazzk = (Class<K>) clazz;
            if (this.serviceClassList.contains(clazzk) == false) {
                this.serviceClassList.add(clazzk);
                this.logger.debug("find service class ".concat(className));
            }
        }
        //是否是内部服务
        if (clazz.isAnnotationPresent(LocalServiceConfig.class) && Local.class.isAssignableFrom(clazz)) {
            clazzl = (Class<Local>) clazz;
            if (this.localServiceClassList.contains(clazzl) == false) {
                this.localServiceClassList.add(clazzl);
                this.logger.debug("find local service class ".concat(className));
            }
        }
    }
}