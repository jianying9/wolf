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

    private static LocalServiceContextImpl INSTANCE = null;

    public static LocalServiceContextImpl getInstance() {
        synchronized (LocalServiceContextImpl.class) {
            if (INSTANCE == null) {
                INSTANCE = new LocalServiceContextImpl();
            }
        }
        return INSTANCE;
    }

    private boolean init = true;

    public static LocalServiceContextImpl getINSTANCE() {
        return INSTANCE;
    }

    public static void setINSTANCE(LocalServiceContextImpl INSTANCE) {
        LocalServiceContextImpl.INSTANCE = INSTANCE;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    private final Map<Class<? extends Local>, Local> localServiceMap;

    private LocalServiceContextImpl() {
        this.localServiceMap = new HashMap(8, 1);
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
        if (this.init) {
            for (Local local : localServiceMap.values()) {
                local.init();
            }
        }
    }
}
