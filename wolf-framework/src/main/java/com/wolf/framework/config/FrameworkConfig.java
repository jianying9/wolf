package com.wolf.framework.config;

/**
 *
 * @author aladdin
 */
public class FrameworkConfig {

    //配置文件
    public final static String CONFIG_FILE = "server-config.json";
    //----------------------------------------------------------
    //运行模式
    public final static String COMPILE_MODEL = "compile.model";
    //服务器模式
    public final static String SERVER = "SERVER";
    //开发模式
    public final static String DEVELOPMENT = "DEVELOPMENT";
    //单元测试模式
    public final static String UNIT_TEST = "UNIT_TEST";
    //----------------------------------------------------------
    //扫描注解包名集合
    public final static String ANNOTATION_SCAN_PACKAGES = "annotation.scan.packages";
    //任务线程空闲存活数量
    public final static String TASK_CORE_POOL_SIZE = "task.core.pool.size";
    //任务线程最大连接数量
    public final static String TASK_MAX_POOL_SIZE = "task.max.pool.size";
    //derby数据源初始化方式:JNDI,EMBEDDED,REMOTE
    public final static String DERBY_TYPE = "derby.type";
    public final static String JNDI = "JNDI";
    public final static String EMBEDDED = "EMBEDDED";
    public final static String REMOTE = "REMOTE";
    //derby jndiName
    public final static String DERBY_JNDI_NAME = "derby.jndi.name";
    //derby数据源名称
    public final static String DERBY_DATABASE_NAME = "derby.database.name";
    //derby数据库ip
    public final static String DERBY_SERVER_NAME = "derby.server.name";
    //derby数据库端口
    public final static String DERBY_SERVER_PORT = "derby.server.port";
    //redis数据库ip
    public final static String REDIS_SERVER_HOST = "redis.server.host";
    //redis数据库端口
    public final static String REDIS_SERVER_PORT = "redis.server.port";
    //redis连接池最大连接数
    public final static String REDIS_MAX_POOL_SIZE = "redis.max.pool.size";
    //redis连接池最小连接数
    public final static String REDIS_MIN_POOL_SIZE = "redis.min.pool.size";
    //http长轮询超时时间(毫秒)
    public final static String ASYNC_PUSH_TIMEOUT = "async.push.timeout";
}
