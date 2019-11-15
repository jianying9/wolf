package com.wolf.framework.cache;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfiguration.TransactionalMode;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

/**
 * ehcache default config
 *
 * @author aladdin
 */
public class EhcacheTools {

    public static Configuration getDefaultConfiguration() {
        final String name = "wolf-manager-" + Long.toString(System.currentTimeMillis());
        Configuration configuration = new Configuration();
        configuration.setName(name);
        return configuration;
    }

    public static CacheConfiguration getDefaultCacheConfiguration() {
        final String name = "wolf-cache-" + Long.toString(System.currentTimeMillis());
        final int maxEntriesLocalHeap = 100000;
        final MemoryStoreEvictionPolicy memoryStoreEvictionPolicy = MemoryStoreEvictionPolicy.LRU;
        final boolean eternal = false;
        final int timeToIdleSeconds = 86400;
        final int timeToLiveSeconds = 0;
        final boolean overflowToOffHeap = false;
        final boolean statistics = false;
        final TransactionalMode transactionalMode = TransactionalMode.OFF;
        final Strategy strategy = PersistenceConfiguration.Strategy.NONE;
        CacheConfiguration cacheConfig = new CacheConfiguration();
        final PersistenceConfiguration persistenceConfiguration = new PersistenceConfiguration();
        persistenceConfiguration.strategy(strategy);
        cacheConfig.name(name).maxEntriesLocalHeap(maxEntriesLocalHeap)
                .memoryStoreEvictionPolicy(memoryStoreEvictionPolicy).eternal(eternal)
                .timeToIdleSeconds(timeToIdleSeconds).timeToLiveSeconds(timeToLiveSeconds)
                .overflowToOffHeap(overflowToOffHeap).statistics(statistics).
                transactionalMode(transactionalMode).persistence(persistenceConfiguration);
        return cacheConfig;
    }
}
