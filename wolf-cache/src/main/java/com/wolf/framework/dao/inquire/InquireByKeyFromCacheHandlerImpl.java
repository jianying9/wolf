package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.inquire.InquireByKeyHandler;
import java.util.ArrayList;
import java.util.List;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/**
 *
 * @author aladdin
 */
public final class InquireByKeyFromCacheHandlerImpl<T extends Entity> implements InquireByKeyHandler<T> {

    private final InquireByKeyHandler<T> inquireByKeyHandler;
    private final Cache entityCache;

    public InquireByKeyFromCacheHandlerImpl(InquireByKeyHandler<T> inquireByKeyHandler, Cache entityCache) {
        this.inquireByKeyHandler = inquireByKeyHandler;
        this.entityCache = entityCache;
    }

    private T getCacheEntity(final String keyValue) {
        T t = null;
        Element element = this.entityCache.getQuiet(keyValue);
        if (element != null) {
            t = (T) element.getObjectValue();
        }
        return t;
    }

    private void putEntityCache(final T t) {
        Element element = new Element(t.getKeyValue(), t);
        this.entityCache.put(element, true);
    }

    private void putEntityCache(final List<T> tList) {
        Element element;
        for (T t : tList) {
            element = new Element(t.getKeyValue(), t);
            this.entityCache.put(element, true);
        }
    }

    @Override
    public T inquireByKey(String keyValue) {
        T t = this.getCacheEntity(keyValue);
        if (t == null) {
            t = this.inquireByKeyHandler.inquireByKey(keyValue);
            if (t != null) {
                this.putEntityCache(t);
            }
        }
        return t;
    }

    @Override
    public List<T> inquireByKeys(List<String> keyValues) {
        List<T> tList = new ArrayList<T>(keyValues.size());
        List<String> missKeyValueList = new ArrayList<String>(10);
        T t;
        for (String keyValue : keyValues) {
            t = this.getCacheEntity(keyValue);
            if (t == null) {
                missKeyValueList.add(keyValue);
            } else {
                tList.add(t);
            }
        }
        if (tList.isEmpty()) {
            //缓存命中0
            tList = this.inquireByKeyHandler.inquireByKeys(keyValues);
            this.putEntityCache(tList);
        } else if (tList.size() < keyValues.size()) {
            //缓存命中部分
            //查找遗漏的对象
            List<T> missEntityList = this.inquireByKeyHandler.inquireByKeys(missKeyValueList);
            //将遗漏对象放入
            tList.addAll(missEntityList);
            this.putEntityCache(missEntityList);
        }
        return tList;
    }
}
