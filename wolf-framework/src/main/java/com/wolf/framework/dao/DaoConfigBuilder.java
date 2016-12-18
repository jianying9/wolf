package com.wolf.framework.dao;

import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.injecter.Injecter;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public interface DaoConfigBuilder {
    
    public void init(ApplicationContext context, Map<Class<?>, List<ColumnHandler>> entityInfoMap);
    
    public Class<?> getAnnotation();
    
    public void putClazz(Class<?> clazz);
    
    public Injecter getInjecter();
    
    public void build();
}
