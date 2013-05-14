package com.wolf.framework.local;

import com.wolf.framework.injecter.Injecter;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface LocalServiceContextBuilder {

    public void putLocalService(final Class<? extends Local> clazz, final Local local);

    public Local getLocalService(Class<? extends Local> clazz);

    public Map<Class<? extends Local>, Local> getLocalServiceMap();
    
    public void inject(Injecter injecter);
}
