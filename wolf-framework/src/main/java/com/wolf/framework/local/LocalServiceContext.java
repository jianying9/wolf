package com.wolf.framework.local;

import com.wolf.framework.injecter.Injecter;
import java.util.Map;

/**
 *
 * @author aladdin
 * @param <L>
 */
public interface LocalServiceContext<L extends Local> {

    public void putLocalService(final Class<? extends Local> clazz, final L l);

    public Local getLocalService(Class<? extends Local> clazz);

    public Map<Class<? extends Local>, L> getLocalServiceMap();
    
    public void inject(Injecter injecter);
}
