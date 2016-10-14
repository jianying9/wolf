package com.wolf.framework.worker.workhandler;

import com.wolf.framework.worker.context.WorkerContext;

/**
 * 工作处理类
 *
 * @author aladdin
 */
public interface WorkHandler {

    public String INFO = "INFO";
    public String SERVICES = "SERVICES";
    public String NULL_MESSAGE = " is null";
    public String EMPTY_MESSAGE = " is empty";

    public void execute(WorkerContext WorkerContext);
}
