package com.wolf.framework.local;

import com.wolf.framework.injecter.Injecter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aladdin
 * @param <L>
 */
public final class LocalServiceContextImpl<L extends Local> implements LocalServiceContext<L> {

    private final Map<Class<? extends Local>, L> localServiceMap;

    public LocalServiceContextImpl() {
        this.localServiceMap = new HashMap<Class<? extends Local>, L>(16, 1);
    }

    @Override
    public void putLocalService(Class<? extends Local> clazz, L l) {
        this.localServiceMap.put(clazz, l);
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
