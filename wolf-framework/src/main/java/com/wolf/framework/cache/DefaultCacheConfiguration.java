package com.wolf.framework.cache;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfiguration.TransactionalMode;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

/**
 * ehcache default config
 *
 * @author aladdin
 */
public class DefaultCacheConfiguration {

    private final String name = "";
    private final int maxEntriesLocalHeap = 10000;
    private final MemoryStoreEvictionPolicy memoryStoreEvictionPolicy = MemoryStoreEvictionPolicy.LRU;
    private final boolean eternal = false;
    private final int timeToIdleSeconds = 300;
    private final int timeToLiveSeconds = 3600;
    private final boolean overflowToOffHeap = false;
    private final boolean statistics = false;
    private final TransactionalMode transactionalMode = TransactionalMode.OFF;
    private final Strategy strategy = PersistenceConfiguration.Strategy.NONE;

    public CacheConfiguration getDefault() {
        CacheConfiguration cacheConfig = new CacheConfiguration();
        final PersistenceConfiguration persistenceConfiguration = new PersistenceConfiguration();
        persistenceConfiguration.strategy(this.strategy);
        cacheConfig.name(this.name).maxEntriesLocalHeap(this.maxEntriesLocalHeap)
                .memoryStoreEvictionPolicy(this.memoryStoreEvictionPolicy).eternal(this.eternal)
                .timeToIdleSeconds(this.timeToIdleSeconds).timeToLiveSeconds(this.timeToLiveSeconds)
                .overflowToOffHeap(this.overflowToOffHeap).statistics(this.statistics).
                transactionalMode(this.transactionalMode).persistence(persistenceConfiguration);
        return cacheConfig;
    }
}
