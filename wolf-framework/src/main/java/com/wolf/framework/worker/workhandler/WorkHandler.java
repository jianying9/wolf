package com.wolf.framework.worker.workhandler;

import com.wolf.framework.worker.context.WorkerContext;

/**
 * 工作处理类
 *
 * @author aladdin
 */
public interface WorkHandler {

    public void execute(WorkerContext WorkerContext);
}
