package com.wolf.framework.local;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.injecter.Injecter;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface LocalServiceContextBuilder<T extends Entity> {

    public void putLocalService(final Class<?> clazz, final Object object);

    public Object getLocalService(Class<?> clazz);

    public Map<Class<?>, Object> getLocalServiceMap();
    
    public void inject(Injecter injecter);
}
