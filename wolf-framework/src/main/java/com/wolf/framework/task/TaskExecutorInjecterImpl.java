package com.wolf.framework.task;

import com.wolf.framework.injecter.AbstractInjecter;
import com.wolf.framework.injecter.Injecter;
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
        return TaskExecutor.class;
    }

    @Override
    protected Object getObject(Class key) {
        return this.taskExecutor;
    }
}
