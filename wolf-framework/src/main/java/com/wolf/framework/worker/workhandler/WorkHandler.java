package com.wolf.framework.worker.workhandler;

import com.wolf.framework.worker.context.WorkerContext;

/**
 * 工作处理类
 *
 * @author aladdin
 */
public interface WorkHandler {

    public String NULL_MESSAGE = " is null";
    
    public void execute(WorkerContext WorkerContext);
}
