package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private Map<String, T> getCacheEntity(final List<String> keyValues) {
        Map<String, T> resultMap = new HashMap<String, T>(keyValues.size(), 1);
        T t;
        Element element;
        for (String keyValue : keyValues) {
            element = this.entityCache.getQuiet(keyValue);
            if (element != null) {
                t = (T) element.getObjectValue();
                resultMap.put(keyValue, t);
            }
        }
        return resultMap;
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

    private List<T> inquireByKeyList(List<String> keyValues) {
        List<T> tList;
        T t;
        Map<String, T> cacheMap = this.getCacheEntity(keyValues);
        if (cacheMap.isEmpty()) {
            //缓存命中0
            tList = this.inquireByKeyHandler.inquireByKeys(keyValues);
            this.putEntityCache(tList);
        } else {
            tList = new ArrayList<T>(keyValues.size());
            if (cacheMap.size() == keyValues.size()) {
                //缓存命中100%
                for (String keyValue : keyValues) {
                    t = cacheMap.get(keyValue);
                    tList.add(t);
                }
            } else {
                //缓存命中部分
                List<String> missKeyValueList = new ArrayList<String>(keyValues.size() - cacheMap.size());
                for (String keyValue : keyValues) {
                    if (!cacheMap.containsKey(keyValue)) {
                        //将未找到的Key放入集合
                        missKeyValueList.add(keyValue);
                    }
                }
                //查找遗漏的对象
                List<T> missEntityList = this.inquireByKeyHandler.inquireByKeys(missKeyValueList);
                //将遗漏对象放入缓存
                for (T tMiss : missEntityList) {
                    cacheMap.put(tMiss.getKeyValue(), tMiss);
                    this.putEntityCache(tMiss);
                }
                //构造查询返回集合
                for (String keyValue : keyValues) {
                    t = cacheMap.get(keyValue);
                    if (t != null) {
                        tList.add(t);
                    }
                }
            }
        }
        return tList;
    }

    @Override
    public List<T> inquireByKeys(List<String> keyValues) {
        List<T> tList;
        switch (keyValues.size()) {
            case 0:
                //key值集合为空
                tList = new ArrayList<T>(0);
                break;
            case 1:
                //key数量为1
                tList = new ArrayList<T>(1);
                T t = this.inquireByKey(keyValues.get(0));
                tList.add(t);
                break;
            default:
                //key数量大于1
                tList = this.inquireByKeyList(keyValues);
        }
        return tList;
    }

    @Override
    public Map<String, String> inquireMapByKey(String keyValue) {
        return this.inquireByKeyHandler.inquireMapByKey(keyValue);
    }

    @Override
    public List<Map<String, String>> inquireMapByKeys(List<String> keyValues) {
        return this.inquireByKeyHandler.inquireMapByKeys(keyValues);
    }
}
