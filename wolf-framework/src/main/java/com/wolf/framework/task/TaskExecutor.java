package com.wolf.framework.task;

import java.util.List;

/**
 *
 * @author aladdin
 */
public interface TaskExecutor {

    public void shutdown();
    
    public void submet(Task task);
    
    public void get();
}
