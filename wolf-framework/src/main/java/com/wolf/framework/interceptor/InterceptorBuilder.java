package com.wolf.framework.interceptor;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.logger.LogFactory;
import org.slf4j.Logger;

/**
 * 负责解析annotation InterceptorConfig
 *
 * @author jianying9
 */
public final class InterceptorBuilder {

    private final InterceptorContext InterceptorContext;
    private final Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);

    public InterceptorBuilder(InterceptorContext InterceptorContext) {
        this.InterceptorContext = InterceptorContext;
    }

    /**
     * 解析方法
     *
     * @param clazz
     */
    public void build(final Class<Interceptor> clazz) {
        this.logger.debug("--parsing Interceptor {}--", clazz.getName());
        //实例化该clazz
        Interceptor interceptor = null;
        try {
            interceptor = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Error when instancing class ".concat(clazz.getName()));
        }
        //创建对应的工作对象
        this.InterceptorContext.addInterceptor(interceptor);
        this.logger.debug("--parse Interceptor {} finished--", clazz.getName());
    }
}
