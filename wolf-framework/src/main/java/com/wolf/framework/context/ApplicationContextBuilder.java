package com.wolf.framework.context;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.EntityDaoContext;
import com.wolf.framework.dao.EntityDaoContextImpl;
import com.wolf.framework.dao.HEntityDaoContext;
import com.wolf.framework.dao.HEntityDaoContextImpl;
import com.wolf.framework.dao.REntityDaoContext;
import com.wolf.framework.dao.REntityDaoContextImpl;
import com.wolf.framework.dao.annotation.DaoConfig;
import com.wolf.framework.dao.annotation.HDaoConfig;
import com.wolf.framework.dao.annotation.RDaoConfig;
import com.wolf.framework.dao.parser.DaoConfigParser;
import com.wolf.framework.dao.parser.HDaoConfigParser;
import com.wolf.framework.dao.parser.RDaoConfigParser;
import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.data.DataHandlerFactoryImpl;
import com.wolf.framework.injecter.DaoInjecterImpl;
import com.wolf.framework.injecter.HDaoInjecterImpl;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.injecter.InjecterListImpl;
import com.wolf.framework.injecter.LocalServiceInjecterImpl;
import com.wolf.framework.injecter.RDaoInjecterImpl;
import com.wolf.framework.injecter.TaskExecutorInjecterImpl;
import com.wolf.framework.local.Local;
import com.wolf.framework.local.LocalServiceConfig;
import com.wolf.framework.local.LocalServiceConfigParser;
import com.wolf.framework.local.LocalServiceContextBuilder;
import com.wolf.framework.local.LocalServiceContextBuilderImpl;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.paser.ClassParser;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.ServiceConfigParser;
import com.wolf.framework.service.parameter.Parameter;
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
import java.util.Map;
import org.slf4j.Logger;

/**
 * 全局上下文对象构造函数抽象类
 *
 * @author aladdin
 */
public class ApplicationContextBuilder<T extends Entity, K extends Service> {

    protected final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
    protected final List<Class<T>> entityClassList = new ArrayList<Class<T>>();
    protected final List<Class<T>> hEntityClassList = new ArrayList<Class<T>>();
    protected final List<Class<T>> rEntityClassList = new ArrayList<Class<T>>();
    protected final List<Class<Parameter>> parameterClassList = new ArrayList<Class<Parameter>>();
    protected final List<Class<K>> serviceClassList = new ArrayList<Class<K>>();
    protected final List<Class<Local>> localServiceClassList = new ArrayList<Class<Local>>();
    protected final List<Class<?>> allClassList = new ArrayList<Class<?>>();
    protected EntityDaoContext<T> entityDaoContext;
    protected HEntityDaoContext<T> hEntityDaoContext;
    protected REntityDaoContext<T> rEntityDaoContext;
    protected ParametersContext parametersContext;
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
        boolean usePseudo = false;
        String compilerModel = this.getParameter(FrameworkConfig.COMPILE_MODEL);
        if (compilerModel == null) {
            compilerModel = FrameworkConfig.SERVER;
        }
        if (compilerModel.equals(FrameworkConfig.DEVELOPMENT)) {
            //开发模式，开始伪实现编译
            usePseudo = true;
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
        if (compilerModel.equals(FrameworkConfig.UNIT_TEST)) {
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
        //初始化data类型工厂对象
        final DataHandlerFactory dataHanlderFactory = new DataHandlerFactoryImpl();
        //解析derby entityDao
        if (this.entityClassList.isEmpty() == false) {
            this.logger.info("parsing annotation DaoConfig...");
            this.entityDaoContext = new EntityDaoContextImpl<T>(
                    ApplicationContext.CONTEXT,
                    taskExecutor,
                    dataHanlderFactory);
            final DaoConfigParser<T> entityConfigDaoParser = new DaoConfigParser<T>(this.entityDaoContext);
            for (Class<T> clazz : this.entityClassList) {
                entityConfigDaoParser.parse(clazz);
            }
        }
        //解析hbase EntityDao
        if (this.hEntityClassList.isEmpty() == false) {
            this.logger.info("parsing annotation HDaoConfig...");
            this.hEntityDaoContext = new HEntityDaoContextImpl<T>(
                    ApplicationContext.CONTEXT,
                    taskExecutor);
            final HDaoConfigParser<T> hEntityConfigDaoParser = new HDaoConfigParser<T>(this.hEntityDaoContext);
            for (Class<T> clazz : this.hEntityClassList) {
                hEntityConfigDaoParser.parse(clazz);
            }
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
        final LocalServiceContextBuilder localServiceContextBuilder = new LocalServiceContextBuilderImpl();
        final LocalServiceConfigParser localServiceConfigParser = new LocalServiceConfigParser(localServiceContextBuilder);
        for (Class<Local> clazz : this.localServiceClassList) {
            localServiceConfigParser.parse(clazz);
        }
        this.logger.info("parse annotation LocalServiceConfig finished.");
        //DAO注入管理对象
        final Injecter daoInjecter = new DaoInjecterImpl(this.entityDaoContext);
        //HDAO注入管理对象
        final Injecter hDaoInjecter = new HDaoInjecterImpl(this.hEntityDaoContext);
        //RDAO注入管理对象
        final Injecter rDaoInjecter = new RDaoInjecterImpl(this.rEntityDaoContext);
        //LocalService注入管理对象
        final Injecter localServiceInjecter = new LocalServiceInjecterImpl(localServiceContextBuilder);
        //TaskExecutor注入管理对象
        final Injecter taskExecutorInjecter = new TaskExecutorInjecterImpl(taskExecutor);
        //创建复合注入解析对象
        InjecterListImpl injecterListImpl = new InjecterListImpl();
        injecterListImpl.addInjecter(daoInjecter);
        injecterListImpl.addInjecter(hDaoInjecter);
        injecterListImpl.addInjecter(rDaoInjecter);
        injecterListImpl.addInjecter(localServiceInjecter);
        injecterListImpl.addInjecter(taskExecutorInjecter);
        final Injecter injecterList = injecterListImpl;
        //对LocalService进行注入
        localServiceContextBuilder.inject(injecterList);
        //解析ParametersConfig包含的FieldConfig
        final ParameterContextBuilder fieldContextBuilder = new ParameterContextBuilderImpl(dataHanlderFactory);
        this.logger.info("parsing annotation ParametersConfig start...");
        this.parametersContext = new ParametersContextImpl(fieldContextBuilder);
        final ParametersConfigParser parametersConfigParser = new ParametersConfigParser(this.parametersContext);
        for (Class<Parameter> clazz : this.parameterClassList) {
            parametersConfigParser.parse(clazz);
        }
        this.logger.info("parse annotation ParametersConfig finished.");
        //解析ServiceConfig
        this.logger.info("parsing annotation ServiceConfig...");
        injecterListImpl = new InjecterListImpl();
        injecterListImpl.addInjecter(localServiceInjecter);
        injecterListImpl.addInjecter(taskExecutorInjecter);
        this.serviceWorkerContext = new ServiceWorkerContextImpl(
                usePseudo,
                this.parametersContext,
                injecterListImpl,
                compilerModel);
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
        Class<Parameter> clazzp;
        this.allClassList.add(clazz);
        //是否是实体
        if (Entity.class.isAssignableFrom(clazz)) {
            clazzt = (Class<T>) clazz;
            if (clazzt.isAnnotationPresent(DaoConfig.class)) {
                if (this.entityClassList.contains(clazzt) == false) {
                    this.entityClassList.add(clazzt);
                    this.logger.debug("find derby entity class ".concat(className));
                }
            } else if (clazzt.isAnnotationPresent(HDaoConfig.class)) {
                if (this.hEntityClassList.contains(clazzt) == false) {
                    this.hEntityClassList.add(clazzt);
                    this.logger.debug("find hbase entity class ".concat(className));
                }
            } else if (clazzt.isAnnotationPresent(RDaoConfig.class)) {
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
        //是否是参数配置
        if (clazz.isAnnotationPresent(ParametersConfig.class) && Parameter.class.isAssignableFrom(clazz)) {
            clazzp = (Class<Parameter>) clazz;
            if (this.parameterClassList.contains(clazzp) == false) {
                this.parameterClassList.add(clazzp);
                this.logger.debug("find parameter class ".concat(className));
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
