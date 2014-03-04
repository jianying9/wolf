package com.wolf.framework.lucene;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.lucene.util.FixedBitSet;

/**
 *
 * @author aladdin
 */
public class DeleteFilterCacheImpl implements DeleteFilterCache {

    private final Cache cache;

    public DeleteFilterCacheImpl(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void putCache(String readerKey, String lastDeleteId, FixedBitSet fixedBitSet) {
        StringBuilder keyBuilder = new StringBuilder(readerKey.length() + lastDeleteId.length() + 1);
        keyBuilder.append(readerKey).append('_').append(lastDeleteId);
        String key = keyBuilder.toString();
        Element element = new Element(key, fixedBitSet);
        this.cache.put(element);
    }

    @Override
    public FixedBitSet getCache(String readerKey, String lastDeleteId) {
        FixedBitSet result = null;
        StringBuilder keyBuilder = new StringBuilder(readerKey.length() + lastDeleteId.length() + 1);
        keyBuilder.append(readerKey).append('_').append(lastDeleteId);
        String key = keyBuilder.toString();
        Element element = this.cache.getQuiet(key);
        if (element != null) {
            result = (FixedBitSet) element.getObjectValue();
        }
        return result;
    }
}
