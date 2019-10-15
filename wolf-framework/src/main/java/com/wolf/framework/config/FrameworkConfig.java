package com.wolf.framework.config;

/**
 *
 * @author aladdin
 */
public class FrameworkConfig {

    //配置文件
    public final static String CONFIG_FILE = "server-config.json";
    //----------------------------------------------------------
    //打包时间
    public final static String BUILD_TIMESTAMP = "build.timestamp";
    //运行模式
    public final static String COMPILE_MODEL = "compile.model";
    //服务器模式
    public final static String SERVER = "SERVER";
    //开发模式
    public final static String DEVELOPMENT = "DEVELOPMENT";
    //单元测试模式
    public final static String UNIT_TEST = "UNIT_TEST";
    //
    public final static String OSS_TEST = "oss.test";
    //----------------------------------------------------------
    //扫描注解包名集合
    public final static String ANNOTATION_SCAN_PACKAGES = "annotation.scan.packages";
    //任务线程空闲存活数量
    public final static String TASK_CORE_POOL_SIZE = "task.core.pool.size";
    //任务线程最大连接数量
    public final static String TASK_MAX_POOL_SIZE = "task.max.pool.size";
    //
    public final static String TASK_SYNC = "task.sync";

    public final static String ASYNC_PUSH_TIMEOUT = "async.push.timeout";
    //http referer
    public final static String HTTP_REFERER = "http.referer";
    //
    public final static String HTTP_COMET = "http.comet";
}
