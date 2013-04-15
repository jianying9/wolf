package com.wolf.framework.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author aladdin
 */
public class TaskExecutorUnitTestImpl implements TaskExecutor {

    private final ThreadPoolExecutor threadPoolExecutor;
    private final LinkedBlockingQueue<Runnable> linkedBlockingQueue;

    public TaskExecutorUnitTestImpl() {
        RejectedExecutionHandler rejectedExecutionHandler = new TaskRejectedExecutionHandlerImpl();
        this.linkedBlockingQueue = new LinkedBlockingQueue<Runnable>(1000);
        this.threadPoolExecutor = new ThreadPoolExecutor(
                5,
                5,
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
        String result = "";
        Future<String> futureTask = this.threadPoolExecutor.submit(task, result);
        try {
            futureTask.get();
        } catch (InterruptedException ex) {
        } catch (ExecutionException ex) {
        }
    }
}
