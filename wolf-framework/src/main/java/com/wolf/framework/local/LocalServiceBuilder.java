package com.wolf.framework.local;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.logger.LogFactory;
import org.apache.logging.log4j.Logger;

/**
 * 负责解析annotation LocalServiceConfig
 *
 * @author aladdin
 */
public final class LocalServiceBuilder {

    private final LocalServiceContext localServiceContext;
    private final Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);

    public LocalServiceBuilder(LocalServiceContext localServiceContext) {
        this.localServiceContext = localServiceContext;
    }

    /**
     * 解析方法
     *
     * @param clazz
     */
    public void build(final Class<Local> clazz) {
        this.logger.debug("--parsing local service {}--", clazz.getName());
        //判断是否实现Local接口
        Class<? extends Local> clazzi = null;
        Class<?>[] clazzInterfaces = clazz.getInterfaces();
        for (Class<?> clazzInterface : clazzInterfaces) {
            if (Local.class.isAssignableFrom(clazzInterface)) {
                clazzi = (Class<? extends Local>) clazzInterface;
                break;
            }
        }
        //实例化该clazz
        Local local = null;
        try {
            local = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Error when instancing class ".concat(clazz.getName()));
        }
        //创建对应的工作对象
        this.localServiceContext.putLocalService(clazzi, local);
        this.logger.debug("--parse local service {} finished--", clazz.getName());

    }
}
