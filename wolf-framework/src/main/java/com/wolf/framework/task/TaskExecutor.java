package com.wolf.framework.task;

/**
 *
 * @author aladdin
 */
public interface TaskExecutor {

    public void shutdown();
    
    public void submet(Task task);
}
