/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
