package com.wolf.framework.worker.workhandler;

import com.wolf.framework.worker.context.FrameworkMessageContext;

/**
 * 次要参数处理
 *
 * @author aladdin
 */
public interface MinorParameterHandler {

    public String execute(FrameworkMessageContext frameworkMessageContext);
}
