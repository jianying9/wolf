package com.wolf.framework.lucene;

import org.apache.lucene.util.FixedBitSet;

/**
 *
 * @author aladdin
 */
public interface DeleteFilterCache {

    public void putCache(String readerKey, String lastDeleteId, FixedBitSet fixedBitSet);

    public FixedBitSet getCache(String readerKey, String lastDeleteId);
}
