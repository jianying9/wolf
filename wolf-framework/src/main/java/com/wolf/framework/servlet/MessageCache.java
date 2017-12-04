package com.wolf.framework.servlet;

import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author jianying9
 */
public class MessageCache {

    private final Queue<String> cacheQuere = new LinkedList();

    private long lastUpdateTime = 0;

    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public String poll() {
        return this.cacheQuere.poll();
    }

    public void offer(String msg) {
        this.cacheQuere.offer(msg);
        this.lastUpdateTime = System.currentTimeMillis();
    }

}
