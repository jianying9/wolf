package com.wolf.framework.task;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author aladdin
 */
public class TaskRejectedExecutionHandlerImpl implements RejectedExecutionHandler {
    
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        Task task = (Task) r;
        task.doWhenRejected();
    }
}
