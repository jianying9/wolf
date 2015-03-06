package com.wolf.framework.dao;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.injecter.Injecter;

/**
 *
 * @author jianying9
 */
public interface DaoConfigBuilder {
    
    public void init(ApplicationContext context);
    
    public Class<?> getAnnotation();
    
    public void putClazz(Class<?> clazz);
    
    public Injecter getInjecter();
    
    public void build();
}
