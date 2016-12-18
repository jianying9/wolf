package com.wolf.framework.context;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.dao.ColumnHandler;
import com.wolf.framework.dao.DaoConfig;
import com.wolf.framework.dao.DaoConfigBuilder;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.injecter.InjecterListImpl;
import com.wolf.framework.interceptor.Interceptor;
import com.wolf.framework.interceptor.InterceptorBuilder;
import com.wolf.framework.local.LocalServiceInjecterImpl;
import com.wolf.framework.task.TaskExecutorInjecterImpl;
import com.wolf.framework.local.Local;
import com.wolf.framework.local.LocalServiceConfig;
import com.wolf.framework.local.LocalServiceBuilder;
import com.wolf.framework.local.LocalServiceContext;
import com.wolf.framework.local.LocalServiceContextImpl;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.module.Module;
import com.wolf.framework.module.ModuleConfig;
import com.wolf.framework.parser.ClassParser;
import com.wolf.framework.interceptor.InterceptorConfig;
import com.wolf.framework.interceptor.InterceptorContext;
import com.wolf.framework.interceptor.InterceptorContextImpl;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.worker.build.WorkerBuilder;
import com.wolf.framework.task.TaskExecutor;
import com.wolf.framework.task.TaskExecutorImpl;
import com.wolf.framework.task.TaskExecutorUnitTestImpl;
import com.wolf.framework.worker.build.WorkerBuildContextImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import com.wolf.framework.worker.build.WorkerBuildContext;
import java.util.HashMap;

/**
 * 全局上下文对象构造函数抽象类
 *
 * @author jianying9
 * @param <T>
 */
public class ApplicationContextBuilder<T extends Entity> {

    protected final Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);
    protected final List<Class<T>> rEntityClassList = new ArrayList<>();
    protected final List<Class<?>> serviceClassList = new ArrayList<>();
    protected final List<Class<Interceptor>> interceptorClassList = new ArrayList<>();
    protected final List<Class<Local>> localServiceClassList = new ArrayList<>();
    protected final List<DaoConfigBuilder> daoConfigBuilderList = new ArrayList<>();
    protected WorkerBuildContext workerBuildContext;
    private final Map<String, String> parameterMap;

    public ApplicationContextBuilder(Map<String, String> parameterMap) {
        this.parameterMap = parameterMap;
    }

    public final String getParameter(String name) {
        return this.parameterMap.get(name);
    }

    private boolean checkException(Throwable e) {
        boolean result = true;
        String error = e.getMessage();
        if (error.contains("javax/servlet/")) {
            result = false;
        } else if (error.contains("com/sun/grizzly/websockets/")) {
            result = false;
        }
        return result;
    }

    private Class<?> loadClass(ClassLoader classloader, String className) {
        Class<?> clazz = null;
        try {
            this.logger.debug("locadClass:{}", className);
            clazz = classloader.loadClass(className);
        } catch (ClassNotFoundException | ClassFormatError | NoClassDefFoundError ex) {
            if (this.checkException(ex)) {
                this.logger.error(className, ex);
            }
        }

        return clazz;
    }

    public final void build() {
        //将运行参数保存至全局上下文对象
        ApplicationContext.CONTEXT.setParameterMap(this.parameterMap);
        //获取运行模式
        String compileModel = this.getParameter(FrameworkConfig.COMPILE_MODEL);
        if (compileModel == null) {
            compileModel = FrameworkConfig.SERVER;
        }
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        List<String> packageNameList = new ArrayList<>();
        //动态查找需要搜索的dao注解创建对象
        //实体信息存储对象
        final Map<Class<?>, List<ColumnHandler>> entityInfoMap = new HashMap<>(2, 1);
        this.logger.info("Finding dao annotation...");
        packageNameList.add("com.wolf.framework.dao");
        List<String> classNameList = new ClassParser().findClass(classloader, packageNameList);
        DaoConfigBuilder daoConfigBuilder;
        Class<?> clazz;
        try {
            for (String className : classNameList) {
                clazz = this.loadClass(classloader, className);
                if (clazz != null && clazz.isAnnotationPresent(DaoConfig.class) && DaoConfigBuilder.class.isAssignableFrom(clazz)) {
                    //发现DaoConfig类型,实例化
                    daoConfigBuilder = (DaoConfigBuilder) clazz.newInstance();
                    //初始化
                    daoConfigBuilder.init(ApplicationContext.CONTEXT, entityInfoMap);
                    this.daoConfigBuilderList.add(daoConfigBuilder);
                }
            }
        } catch (InstantiationException | IllegalAccessException ex) {
            this.logger.error("Error when instance DaoConfig. Cause:", ex);
        }
        ApplicationContext.CONTEXT.setEntityInfo(entityInfoMap);
        //查找注解类
        this.logger.info("Finding annotation...");
        String packages = this.getParameter(FrameworkConfig.ANNOTATION_SCAN_PACKAGES);
        packageNameList = new ArrayList<>();
        if (packages != null) {
            String[] packageNames = packages.split(",");
            packageNameList.addAll(Arrays.asList(packageNames));
        }
        //如果是开发模式,则加入接口文档接口
        if (compileModel.equals(FrameworkConfig.DEVELOPMENT) || compileModel.equals(FrameworkConfig.UNIT_TEST)) {
            packageNameList.add("com.wolf.framework.doc");
        }
        classNameList = new ClassParser().findClass(classloader, packageNameList);
        try {
            for (String className : classNameList) {
                this.parseClass(classloader, className);
            }
        } catch (ClassNotFoundException e) {
            this.logger.error("Error when find annotation. Cause:", e);
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
        //解析Dao
        for (DaoConfigBuilder dcBuilder : this.daoConfigBuilderList) {
            dcBuilder.build();
        }
        //解析LocalService
        this.logger.info("parsing annotation LocalServiceConfig...");
        final LocalServiceContext localServiceContext = new LocalServiceContextImpl();
        final LocalServiceBuilder localServiceBuilder = new LocalServiceBuilder(localServiceContext);
        for (Class<Local> clazzl : this.localServiceClassList) {
            localServiceBuilder.build(clazzl);
        }
        this.logger.info("parse annotation LocalServiceConfig finished.");
        //LocalService注入管理对象
        final Injecter localServiceInjecter = new LocalServiceInjecterImpl(localServiceContext);
        //TaskExecutor注入管理对象
        final Injecter taskExecutorInjecter = new TaskExecutorInjecterImpl(taskExecutor);
        //创建复合注入解析对象
        InjecterListImpl injecterListImpl = new InjecterListImpl();
        injecterListImpl.addInjecter(localServiceInjecter);
        injecterListImpl.addInjecter(taskExecutorInjecter);
        //DAO注入管理对象
        for (DaoConfigBuilder dcBuilder : this.daoConfigBuilderList) {
            injecterListImpl.addInjecter(dcBuilder.getInjecter());
        }
        final Injecter injecterList = injecterListImpl;
        //对LocalService进行注入
        localServiceContext.inject(injecterList);
        //
        //解析InterceptorConfig
        this.logger.info("parsing annotation InterceptorConfig...");
        final InterceptorContext interceptorContext = new InterceptorContextImpl();
        InterceptorBuilder interceptorBuilder = new InterceptorBuilder(interceptorContext);
        for (Class<Interceptor> clazzi : this.interceptorClassList) {
            interceptorBuilder.build(clazzi);
        }
        this.logger.info("parse annotation InterceptorConfig finished.");
        //对Interceptor进行注入
        interceptorContext.inject(injecterList);
        //解析ServiceConfig
        this.logger.info("parsing annotation ServiceConfig...");
        this.workerBuildContext = new WorkerBuildContextImpl(
                injecterList,
                interceptorContext,
                ApplicationContext.CONTEXT);
        final WorkerBuilder workerBuilder = new WorkerBuilder(this.workerBuildContext);
        for (Class<?> clazzs : this.serviceClassList) {
            workerBuilder.build(clazzs);
        }
        ApplicationContext.CONTEXT.setServiceWorkerMap(this.workerBuildContext.getServiceWorkerMap());
        this.logger.info("parse annotation ServiceConfig finished.");
        //load module
        packageNameList.clear();
        packageNameList.add("com.wolf.framework.module");
        classNameList = new ClassParser().findClass(classloader, packageNameList);
        Module module;
        for (String className : classNameList) {
            try {
                clazz = this.loadClass(classloader, className);
                if (clazz != null && clazz.isAnnotationPresent(ModuleConfig.class) && Module.class.isAssignableFrom(clazz)) {
                    //发现Module,实例化
                    module = (Module) clazz.newInstance();
                    //初始化
                    module.init(ApplicationContext.CONTEXT);
                }
            } catch (InstantiationException | IllegalAccessException ex) {
                this.logger.error("Error when instance ModuleConfig. Cause:", ex);
            }
        }
        //
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
        Class<?> clazz = this.loadClass(classloader, className);
        if (clazz != null) {
            Class<?> clazzs;
            Class<Local> clazzl;
            Class<Interceptor> clazzi;
            if (clazz.isAnnotationPresent(ServiceConfig.class)) {
                //是外部服务
                clazzs = clazz;
                if (this.serviceClassList.contains(clazzs) == false) {
                    this.serviceClassList.add(clazzs);
                    this.logger.debug("find service class ".concat(className));
                }
            } else if (clazz.isAnnotationPresent(InterceptorConfig.class) && Interceptor.class.isAssignableFrom(clazz)) {
                //是拦截服务
                clazzi = (Class<Interceptor>) clazz;
                if (this.interceptorClassList.contains(clazzi) == false) {
                    this.interceptorClassList.add(clazzi);
                    this.logger.debug("find interceptor class ".concat(className));
                }
            } else if (clazz.isAnnotationPresent(LocalServiceConfig.class) && Local.class.isAssignableFrom(clazz)) {
                //是内部服务
                clazzl = (Class<Local>) clazz;
                if (this.localServiceClassList.contains(clazzl) == false) {
                    this.localServiceClassList.add(clazzl);
                    this.logger.debug("find local service class ".concat(className));
                }
            } else {
                //其他注解类型
                for (DaoConfigBuilder daoConfigBuilder : this.daoConfigBuilderList) {
                    daoConfigBuilder.putClazz(clazz);
                }
            }
        }
    }
}
