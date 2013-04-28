package com.wolf.framework.context;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.EntityDaoContext;
import com.wolf.framework.dao.EntityDaoContextImpl;
import com.wolf.framework.dao.HEntityDaoContext;
import com.wolf.framework.dao.HEntityDaoContextImpl;
import com.wolf.framework.dao.annotation.DaoConfig;
import com.wolf.framework.dao.annotation.HDaoConfig;
import com.wolf.framework.dao.parser.DaoConfigParser;
import com.wolf.framework.dao.parser.HDaoConfigParser;
import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.data.DataHandlerFactoryImpl;
import com.wolf.framework.hbase.HTableHandler;
import com.wolf.framework.injecter.DaoInjecterImpl;
import com.wolf.framework.injecter.HDaoInjecterImpl;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.injecter.InjecterListImpl;
import com.wolf.framework.injecter.LocalServiceInjecterImpl;
import com.wolf.framework.local.LocalServiceConfig;
import com.wolf.framework.local.LocalServiceConfigParser;
import com.wolf.framework.local.LocalServiceContextBuilder;
import com.wolf.framework.local.LocalServiceContextBuilderImpl;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.paser.ClassParser;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.ServiceConfigParser;
import com.wolf.framework.service.parameter.ParameterContextBuilder;
import com.wolf.framework.service.parameter.ParameterContextBuilderImpl;
import com.wolf.framework.service.parameter.ParametersConfig;
import com.wolf.framework.service.parameter.ParametersConfigParser;
import com.wolf.framework.service.parameter.ParametersContext;
import com.wolf.framework.service.parameter.ParametersContextImpl;
import com.wolf.framework.task.TaskExecutor;
import com.wolf.framework.task.TaskExecutorImpl;
import com.wolf.framework.task.TaskExecutorUnitTestImpl;
import com.wolf.framework.worker.ServiceWorkerContext;
import com.wolf.framework.worker.ServiceWorkerContextImpl;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.Configuration;
import org.slf4j.Logger;

/**
 * 全局上下文对象构造函数抽象类
 *
 * @author aladdin
 */
public abstract class AbstractApplicationContextBuilder<T extends Entity, K extends Service> {

    protected final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
    protected final List<Class<T>> entityClassList = new ArrayList<Class<T>>();
    protected final List<Class<T>> hEntityClassList = new ArrayList<Class<T>>();
    protected final List<Class<?>> parameterClassList = new ArrayList<Class<?>>();
    protected final List<Class<K>> serviceClassList = new ArrayList<Class<K>>();
    protected final List<Class<?>> localServiceClassList = new ArrayList<Class<?>>();
    protected final List<Class<?>> allClassList = new ArrayList<Class<?>>();
    protected EntityDaoContext<T> entityDaoContext;
    protected HEntityDaoContext<T> hEntityDaoContext;
    protected ParametersContext parametersContext;
    protected ServiceWorkerContext serviceWorkerContext;

    protected abstract String[] getPackageNames();

    protected abstract String getAppPath();

    protected abstract HTableHandler hTableHandlerBuild();

    protected abstract String getCompileModel();

    protected abstract DataSource dataSourceBuild();

    public abstract void build();

    protected final void baseBuild() {
        String ip = null;
        try {
            //获取本地ip
            InetAddress address = InetAddress.getLocalHost();
            ip = address.getHostAddress();
        } catch (UnknownHostException ex) {
            throw new RuntimeException("get ip error...", ex);
        }
        if (ip == null || ip.equals("127.0.0.1")) {
            throw new RuntimeException("127.0.0.1 invalid ip...please change it.");
        }
        //获取运行模式
        boolean usePseudo = false;
        final String compilerModel = this.getCompileModel();
        if (compilerModel.equals(FrameworkConfig.DEVELOPMENT)) {
            //开发模式，开始伪实现编译
            usePseudo = true;
        }
        //初始化数据源
        final DataSource dataSource = this.dataSourceBuild();
        //创建缓存管理对象
        final Configuration ehcacheConfig = new Configuration();
        ehcacheConfig.setName("wolf-cache");
        ehcacheConfig.setDynamicConfig(false);
        ehcacheConfig.setUpdateCheck(false);
        ehcacheConfig.setMonitoring("OFF");
        final CacheManager cacheManager = CacheManager.create(ehcacheConfig);
        cacheManager.removalAll();
        //将缓存管理对象放入全局上下文
        ApplicationContext.CONTEXT.setCacheManager(cacheManager);
        //查找注解类
        this.logger.info("finding annotation...");
        String[] packageNames = this.getPackageNames();
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        final List<String> classNameList = new ClassParser().findClass(classloader, packageNames);
        try {
            for (String className : classNameList) {
                this.parseClass(classloader, className);
            }
        } catch (ClassNotFoundException e) {
            this.logger.error("finding class error! ", e);
        }
        this.logger.info("find annotation finished.");
        //初始化任务处理对象
        TaskExecutor taskExecutor;
        if (compilerModel.equals(FrameworkConfig.UNIT_TEST)) {
            taskExecutor = new TaskExecutorUnitTestImpl();
        } else {
            taskExecutor = new TaskExecutorImpl();
        }
        //实例化HTableFactory
        final HTableHandler hTableHandler = this.hTableHandlerBuild();
        //实例化hdfs文件管理对象
//        final FileSystem fileSystem = this.fileSystemBuild();
//        final FileSystem fileSystem = null;
        //初始化data类型工厂对象
        final DataHandlerFactory dataHanlderFactory = new DataHandlerFactoryImpl();
        //解析entityDao
        this.logger.info("parsing annotation DaoConfig...");
        this.entityDaoContext = new EntityDaoContextImpl<T>(
                ApplicationContext.CONTEXT,
                //                hTableHandler,
                cacheManager,
                //                fileSystem,
                taskExecutor,
                //                ip,
                dataSource,
                dataHanlderFactory);
        final DaoConfigParser<T> entityConfigDaoParser = new DaoConfigParser<T>(this.entityDaoContext);
        for (Class<T> clazz : this.entityClassList) {
            entityConfigDaoParser.parse(clazz);
        }
        this.logger.info("parse annotation DaoConfig finished.");
        //解析hEntityDao
        this.logger.info("parsing annotation HDaoConfig...");
        this.hEntityDaoContext = new HEntityDaoContextImpl<T>(
                ApplicationContext.CONTEXT,
                hTableHandler,
                taskExecutor);
        final HDaoConfigParser<T> hEntityConfigDaoParser = new HDaoConfigParser<T>(this.hEntityDaoContext);
        for (Class<T> clazz : this.hEntityClassList) {
            hEntityConfigDaoParser.parse(clazz);
        }
        this.logger.info("parse annotation HDaoConfig finished.");
        //解析LocalService
        this.logger.info("parsing annotation LocalServiceConfig...");
        final LocalServiceContextBuilder localServiceContextBuilder = new LocalServiceContextBuilderImpl();
        final LocalServiceConfigParser localServiceConfigParser = new LocalServiceConfigParser(localServiceContextBuilder);
        for (Class<?> clazz : this.localServiceClassList) {
            localServiceConfigParser.parse(clazz);
        }
        this.logger.info("parse annotation LocalServiceConfig finished.");
        //DAO注入管理对象
        final Injecter daoInjecter = new DaoInjecterImpl(this.entityDaoContext);
        //HDAO注入管理对象
        final Injecter hDaoInjecter = new HDaoInjecterImpl(this.hEntityDaoContext);
        //LocalService注入管理对象
        final Injecter localServiceInjecter = new LocalServiceInjecterImpl(localServiceContextBuilder);
        //创建复合注入解析对象
        InjecterListImpl injecterListImpl = new InjecterListImpl();
        injecterListImpl.addInjecter(daoInjecter);
        injecterListImpl.addInjecter(hDaoInjecter);
        injecterListImpl.addInjecter(localServiceInjecter);
        final Injecter injecterList = injecterListImpl;
        //对LocalService进行注入
        localServiceContextBuilder.inject(injecterList);
        //解析ParametersConfig包含的FieldConfig
        final ParameterContextBuilder fieldContextBuilder = new ParameterContextBuilderImpl(dataHanlderFactory);
        this.logger.info("parsing annotation ParametersConfig start...");
        this.parametersContext = new ParametersContextImpl(fieldContextBuilder);
        final ParametersConfigParser parametersConfigParser = new ParametersConfigParser(this.parametersContext);
        for (Class<?> clazz : this.parameterClassList) {
            parametersConfigParser.parse(clazz);
        }
        this.logger.info("parse annotation ParametersConfig finished.");
        //解析ServiceConfig
        this.logger.info("parsing annotation ServiceConfig...");
        this.serviceWorkerContext = new ServiceWorkerContextImpl(
                usePseudo,
                this.parametersContext,
                localServiceInjecter,
                compilerModel);
        final ServiceConfigParser<K, T> serviceConfigParser = new ServiceConfigParser<K, T>(this.serviceWorkerContext);
        for (Class<K> clazz : this.serviceClassList) {
            serviceConfigParser.parse(clazz);
        }
        ApplicationContext.CONTEXT.setServiceWorkerMap(this.serviceWorkerContext.getServiceWorkerMap());
        this.logger.info("parse annotation ServiceConfig finished.");
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
        this.allClassList.add(clazz);
        //是否是实体
        if (Entity.class.isAssignableFrom(clazz)) {
            clazzt = (Class<T>) clazz;
            if (clazzt.isAnnotationPresent(DaoConfig.class)) {
                if (this.entityClassList.contains(clazzt) == false) {
                    this.entityClassList.add(clazzt);
                    this.logger.debug("find entity class ".concat(className));
                }
            } else if (clazzt.isAnnotationPresent(HDaoConfig.class)) {
                if (this.hEntityClassList.contains(clazzt) == false) {
                    this.hEntityClassList.add(clazzt);
                    this.logger.debug("find H entity class ".concat(className));
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
        //是否是参数配置
        if (clazz.isAnnotationPresent(ParametersConfig.class)) {
            if (this.parameterClassList.contains(clazz) == false) {
                this.parameterClassList.add(clazz);
                this.logger.debug("find parameter class ".concat(className));
            }
        }
        //是否是内部服务
        if (clazz.isAnnotationPresent(LocalServiceConfig.class)) {
            if (this.localServiceClassList.contains(clazz) == false) {
                this.localServiceClassList.add(clazz);
                this.logger.debug("find local service class ".concat(className));
            }
        }
    }
}
