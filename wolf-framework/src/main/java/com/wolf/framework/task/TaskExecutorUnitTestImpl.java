package com.wolf.framework.task;

import java.util.ArrayList;
import java.util.List;
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
    
    public TaskExecutorUnitTestImpl() {
        RejectedExecutionHandler rejectedExecutionHandler = new TaskRejectedExecutionHandlerImpl();
        LinkedBlockingQueue<Runnable> linkedBlockingQueue = new LinkedBlockingQueue();
        this.threadPoolExecutor = new ThreadPoolExecutor(
                1,
                1,
                10000,
                TimeUnit.MILLISECONDS,
                linkedBlockingQueue,
                Executors.defaultThreadFactory(),
                rejectedExecutionHandler);
    }

    @Override
    public void submit(Task task) {
        this.syncSubmit(task);
    }

    @Override
    public void submit(List<Task> taskList) {
        this.syncSubmit(taskList);
    }

    @Override
    public void syncSubmit(Task task) {
        String result = "";
        Future<String> futureTask = this.threadPoolExecutor.submit(task, result);
        try {
            futureTask.get();
        } catch (InterruptedException | ExecutionException ex) {
        }
    }

    @Override
    public void syncSubmit(List<Task> taskList) {
        String result = "";
        Future<String> futureTask;
        List<Future<String>> futureTaskList = new ArrayList(taskList.size());
        for (Task task : taskList) {
            futureTask = this.threadPoolExecutor.submit(task, result);
            futureTaskList.add(futureTask);
        }
        try {
            for (Future<String> future : futureTaskList) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException ex) {
        }
    }

    @Override
    public void schedule(Task task, long delay) {
        this.syncSubmit(task);
    }
}
