package com.wolf.framework.task;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author aladdin
 */
public class TaskExecutorImpl implements TaskExecutor {

    private final ThreadPoolExecutor threadPoolExecutor;
    private final LinkedBlockingQueue<Runnable> linkedBlockingQueue;

    public TaskExecutorImpl(int corePoolSize) {
        if(corePoolSize < 0 || corePoolSize > 500) {
            corePoolSize = 500;
        }
        RejectedExecutionHandler rejectedExecutionHandler = new TaskRejectedExecutionHandlerImpl();
        this.linkedBlockingQueue = new LinkedBlockingQueue<Runnable>(1000);
        this.threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                500,
                60000,
                TimeUnit.MILLISECONDS,
                this.linkedBlockingQueue,
                Executors.defaultThreadFactory(),
                rejectedExecutionHandler);
    }

    @Override
    public void shutdown() {
        this.threadPoolExecutor.shutdown();
    }

    @Override
    public void submet(Task task) {
        this.threadPoolExecutor.execute(task);
    }

    @Override
    public void get() {
    }
}
