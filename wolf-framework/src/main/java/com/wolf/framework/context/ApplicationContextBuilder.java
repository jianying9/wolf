package com.wolf.framework.context;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.dao.DaoConfig;
import com.wolf.framework.dao.DaoConfigBuilder;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.data.DataHandlerFactoryImpl;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.injecter.InjecterListImpl;
import com.wolf.framework.local.LocalServiceInjecterImpl;
import com.wolf.framework.service.TaskExecutorInjecterImpl;
import com.wolf.framework.local.Local;
import com.wolf.framework.local.LocalServiceConfig;
import com.wolf.framework.local.LocalServiceConfigParser;
import com.wolf.framework.local.LocalServiceContext;
import com.wolf.framework.local.LocalServiceContextImpl;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.module.Module;
import com.wolf.framework.module.ModuleConfig;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

/**
 * 全局上下文对象构造函数抽象类
 *
 * @author aladdin
 * @param <T>
 * @param <K>
 */
public class ApplicationContextBuilder<T extends Entity, K extends Service> {

    protected final Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);
    protected final List<Class<T>> rEntityClassList = new ArrayList<Class<T>>();
    protected final List<Class<K>> serviceClassList = new ArrayList<Class<K>>();
    protected final List<Class<Local>> localServiceClassList = new ArrayList<Class<Local>>();
    protected final List<DaoConfigBuilder> daoConfigBuilderList = new ArrayList<DaoConfigBuilder>();
    protected ServiceWorkerContext serviceWorkerContext;
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
        if (error.indexOf("javax/servlet/") == 0) {
            result = false;
        } else if (error.indexOf("com/sun/grizzly/websockets/") == 0) {
            result = false;
        }
        return result;
    }

    public final void build() {
        //校验密钥
        String key = this.parameterMap.get(FrameworkConfig.SEED_DES_KEY);
        if (key == null || key.length() != 8) {
            key = "wolf2014";
            this.parameterMap.put(FrameworkConfig.SEED_DES_KEY, key);
        }
        //将运行参数保存至全局上下文对象
        ApplicationContext.CONTEXT.setParameterMap(this.parameterMap);
        //获取运行模式
        String compileModel = this.getParameter(FrameworkConfig.COMPILE_MODEL);
        if (compileModel == null) {
            compileModel = FrameworkConfig.SERVER;
        }
        //检测服务器hostname的ip不能为127.0.0.1,否则提供rmi远程调用类服务时会出现异常
        if (compileModel.equals(FrameworkConfig.SERVER)) {
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
        }
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        List<String> packageNameList = new ArrayList<String>();
        //动态查找需要搜索的dao注解创建对象
        this.logger.info("Finding dao annotation...");
        packageNameList.add("com.wolf.framework.dao");
        List<String> classNameList = new ClassParser().findClass(classloader, packageNameList);
        DaoConfigBuilder daoConfigBuilder;
        Class<?> clazz;
        for (String className : classNameList) {
            try {
                clazz = classloader.loadClass(className);
                if (clazz.isAnnotationPresent(DaoConfig.class) && DaoConfigBuilder.class.isAssignableFrom(clazz)) {
                    //发现DaoConfig类型,实例化
                    daoConfigBuilder = (DaoConfigBuilder) clazz.newInstance();
                    //初始化
                    daoConfigBuilder.init(ApplicationContext.CONTEXT);
                    this.daoConfigBuilderList.add(daoConfigBuilder);
                }
            } catch (ClassNotFoundException e) {
                if (this.checkException(e)) {
                    this.logger.error("ClassNotFoundException:", e);
                }
            } catch (NoClassDefFoundError e) {
                if (this.checkException(e)) {
                    this.logger.error("NoClassDefFoundError:", e);
                }
            } catch (InstantiationException ex) {
                this.logger.error("Error when instance DaoConfig. Cause:", ex);
            } catch (IllegalAccessException ex) {
                this.logger.error("Error when instance DaoConfig. Cause:", ex);
            }

        }
        //查找注解类
        this.logger.info("Finding annotation...");
        String packages = this.getParameter(FrameworkConfig.ANNOTATION_SCAN_PACKAGES);
        if (packages != null) {
            String[] packageNames = packages.split(",");
            packageNameList = new ArrayList<String>(packageNames.length);
            packageNameList.addAll(Arrays.asList(packageNames));
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
        final LocalServiceContext localServiceContextBuilder = new LocalServiceContextImpl();
        final LocalServiceConfigParser localServiceConfigParser = new LocalServiceConfigParser(localServiceContextBuilder);
        for (Class<Local> clazzl : this.localServiceClassList) {
            localServiceConfigParser.parse(clazzl);
        }
        //将LocalService放入ApplicationContext
        ApplicationContext.CONTEXT.setLocalServiceMap(localServiceContextBuilder.getLocalServiceMap());
        this.logger.info("parse annotation LocalServiceConfig finished.");
        //LocalService注入管理对象
        final Injecter localServiceInjecter = new LocalServiceInjecterImpl(localServiceContextBuilder);
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
        localServiceContextBuilder.inject(injecterList);
        //初始化data类型工厂对象
        final DataHandlerFactory dataHanlderFactory = new DataHandlerFactoryImpl();
        ParameterContext parametersContext = new ParameterContextImpl(dataHanlderFactory, ApplicationContext.CONTEXT);
        //服务注入对象
        injecterListImpl = new InjecterListImpl();
        injecterListImpl.addInjecter(localServiceInjecter);
        injecterListImpl.addInjecter(taskExecutorInjecter);
        //解析ServiceConfig
        this.logger.info("parsing annotation ServiceConfig...");
        this.serviceWorkerContext = new ServiceWorkerContextImpl(
                injecterListImpl,
                parametersContext,
                ApplicationContext.CONTEXT);
        final ServiceConfigParser<K> serviceConfigParser = new ServiceConfigParser<K>(this.serviceWorkerContext);
        for (Class<K> clazzs : this.serviceClassList) {
            serviceConfigParser.parse(clazzs);
        }
        ApplicationContext.CONTEXT.setServiceWorkerMap(this.serviceWorkerContext.getServiceWorkerMap());
        this.logger.info("parse annotation ServiceConfig finished.");
        //load module
        packageNameList.clear();
        packageNameList.add("com.wolf.framework.module");
        classNameList = new ClassParser().findClass(classloader, packageNameList);
        Module module;
        for (String className : classNameList) {
            try {
                clazz = classloader.loadClass(className);
                if (clazz.isAnnotationPresent(ModuleConfig.class) && Module.class.isAssignableFrom(clazz)) {
                    //发现Module,实例化
                    module = (Module) clazz.newInstance();
                    //初始化
                    module.init(ApplicationContext.CONTEXT);
                }
            } catch (ClassNotFoundException e) {
                if (this.checkException(e)) {
                    this.logger.error("ClassNotFoundException:", e);
                }
            } catch (NoClassDefFoundError e) {
                if (this.checkException(e)) {
                    this.logger.error("NoClassDefFoundError:", e);
                }
            } catch (InstantiationException ex) {
                this.logger.error("Error when instance ModuleConfig. Cause:", ex);
            } catch (IllegalAccessException ex) {
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
        Class<?> clazz = classloader.loadClass(className);
        Class<K> clazzk;
        Class<Local> clazzl;
        if (Service.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(ServiceConfig.class)) {
            //是外部服务
            clazzk = (Class<K>) clazz;
            if (this.serviceClassList.contains(clazzk) == false) {
                this.serviceClassList.add(clazzk);
                this.logger.debug("find service class ".concat(className));
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
