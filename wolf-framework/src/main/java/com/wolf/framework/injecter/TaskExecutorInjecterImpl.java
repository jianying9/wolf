package com.wolf.framework.injecter;

import com.wolf.framework.task.InjectTaskExecutor;
import com.wolf.framework.task.TaskExecutor;
import java.lang.reflect.Field;

/**
 *
 * @author aladdin
 */
public class TaskExecutorInjecterImpl extends AbstractInjecter<InjectTaskExecutor> implements Injecter {

    private final TaskExecutor taskExecutor;

    public TaskExecutorInjecterImpl(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Override
    protected Class<InjectTaskExecutor> getAnnotation() {
        return InjectTaskExecutor.class;
    }

    @Override
    protected Class<?> getObjectKey(Field field) {
        return InjectTaskExecutor.class;
    }

    @Override
    protected Object getObject(Class key) {
        return this.taskExecutor;
    }
}
