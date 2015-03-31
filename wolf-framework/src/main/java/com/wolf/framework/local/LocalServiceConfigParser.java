package com.wolf.framework.local;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.service.Service;
import org.slf4j.Logger;

/**
 * 负责解析annotation ServiceConfig
 *
 * @author aladdin
 */
public final class LocalServiceConfigParser {

    private final LocalServiceContext localServiceContextBuilder;
    private final Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);

    public LocalServiceConfigParser(LocalServiceContext localServiceContextBuilder) {
        this.localServiceContextBuilder = localServiceContextBuilder;
    }

    /**
     * 解析方法
     *
     * @param clazz
     */
    public void parse(final Class<Local> clazz) {
        this.logger.debug("--parsing local service {}--", clazz.getName());
        if (clazz.isAnnotationPresent(LocalServiceConfig.class)) {
            //判断是否实现Local接口
            Class<? extends Local> clazzi = null;
            Class<?>[] clazzInterfaces = clazz.getInterfaces();
            for (Class<?> clazzInterface : clazzInterfaces) {
                if (Local.class.isAssignableFrom(clazzInterface)) {
                    clazzi = (Class<? extends Local>) clazzInterface;
                    break;
                }
            }
            //判断是否实现Local接口
            if (clazzi == null) {
                throw new RuntimeException("Error when parse class:" + clazz.getName() + ". Not implements Local");
            }
            //获取注解ServiceConfig
            final LocalServiceConfig localServiceConfig = clazz.getAnnotation(LocalServiceConfig.class);
            //实例化该clazz
            Local local = null;
            try {
                local = clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Error when instancing class ".concat(clazz.getName()));
            }
            //创建对应的工作对象
            this.localServiceContextBuilder.putLocalService(clazzi, local);
            this.logger.debug("--parse local service {} finished--", clazz.getName());
        } else {
            this.logger.error("--parse  local service {} missing annotation LocalServiceConfig--", clazz.getName());
        }
    }
}
