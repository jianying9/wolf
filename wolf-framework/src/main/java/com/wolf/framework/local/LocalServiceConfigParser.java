package com.wolf.framework.local;

import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.logger.LogFactory;
import org.slf4j.Logger;

/**
 * 负责解析annotation ServiceConfig
 *
 * @author aladdin
 */
public final class LocalServiceConfigParser {

    private final LocalServiceContextBuilder localServiceContextBuilder;
    private final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);

    public LocalServiceConfigParser(LocalServiceContextBuilder localServiceContextBuilder) {
        this.localServiceContextBuilder = localServiceContextBuilder;
    }

    /**
     * 解析方法
     *
     * @param clazz
     */
    public void parse(final Class<?> clazz) {
        this.logger.debug("--parsing local service {}--", clazz.getName());
        if (clazz.isAnnotationPresent(LocalServiceConfig.class)) {
            //1.获取注解ServiceConfig
            final LocalServiceConfig localServiceConfig = clazz.getAnnotation(LocalServiceConfig.class);
            final Class<?> clazzi = localServiceConfig.interfaceInfo();
            //实例化该clazz
            Object object = null;
            try {
                object = clazz.newInstance();
            } catch (Exception e) {
                this.logger.error("There was an error instancing class {}. Cause: {}", clazz.getName(), e.getMessage());
                throw new RuntimeException("There wa an error instancing class ".concat(clazz.getName()));
            }
            //创建对应的工作对象
            this.localServiceContextBuilder.putLocalService(clazzi, object);
            this.logger.debug("--parse local service {} finished--", clazz.getName());
        } else {
            this.logger.error("--parse  local service {} missing annotation LocalServiceConfig--", clazz.getName());
        }
    }
}
