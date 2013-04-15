package com.wolf.framework.local;

import com.wolf.framework.injecter.Injecter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public final class LocalServiceContextBuilderImpl implements LocalServiceContextBuilder {

    private final Map<Class<?>, Object> localServiceMap;

    public LocalServiceContextBuilderImpl() {
        this.localServiceMap = new HashMap<Class<?>, Object>(16, 1);
    }

    @Override
    public void putLocalService(Class clazz, Object object) {
        this.localServiceMap.put(clazz, object);
    }

    @Override
    public Map getLocalServiceMap() {
        return Collections.unmodifiableMap(this.localServiceMap);
    }

    @Override
    public Object getLocalService(Class clazz) {
        return this.localServiceMap.get(clazz);
    }

    @Override
    public void inject(Injecter injecter) {
        for (Object object : localServiceMap.values()) {
            injecter.parse(object);
        }
    }
}
