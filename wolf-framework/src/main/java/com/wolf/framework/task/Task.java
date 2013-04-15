package com.wolf.framework.task;

/**
 *
 * @author aladdin
 */
public interface Task extends Runnable {
    
    public void doWhenRejected();
}
