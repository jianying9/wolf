package com.wolf.framework.task;

import java.util.List;

/**
 *
 * @author aladdin
 */
public interface TaskExecutor {

    public void submit(Task task);

    public void submit(List<Task> taskList);

    public void syncSubmit(Task task);

    public void syncSubmit(List<Task> taskList);
}
