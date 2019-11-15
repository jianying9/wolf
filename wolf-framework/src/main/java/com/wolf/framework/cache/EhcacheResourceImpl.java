package com.wolf.framework.cache;

import com.wolf.framework.context.Resource;
import net.sf.ehcache.CacheManager;

/**
 *
 * @author jianying9
 */
public class EhcacheResourceImpl implements Resource {

    private final CacheManager cacheManager;

    public EhcacheResourceImpl(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void destory() {
        this.cacheManager.shutdown();
    }

}
