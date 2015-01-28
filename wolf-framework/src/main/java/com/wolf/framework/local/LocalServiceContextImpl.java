package com.wolf.framework.local;

import com.wolf.framework.injecter.Injecter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public final class LocalServiceContextImpl implements LocalServiceContext {

    private final Map<Class<? extends Local>, Local> localServiceMap;

    public LocalServiceContextImpl() {
        this.localServiceMap = new HashMap<Class<? extends Local>, Local>(8, 1);
    }

    @Override
    public void putLocalService(Class<? extends Local> clazz, Local local) {
        this.localServiceMap.put(clazz, local);
    }

    @Override
    public Map getLocalServiceMap() {
        return Collections.unmodifiableMap(this.localServiceMap);
    }

    @Override
    public Local getLocalService(Class<? extends Local> clazz) {
        return this.localServiceMap.get(clazz);
    }

    @Override
    public void inject(Injecter injecter) {
        for (Local local : localServiceMap.values()) {
            injecter.parse(local);
        }
        for (Local local : localServiceMap.values()) {
            local.init();
        }
    }
}
