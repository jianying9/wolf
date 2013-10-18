package com.wolf.framework.worker;

import com.wolf.framework.worker.context.FrameworkMessageContext;

/**
 * 服务工作对象接口
 *
 * @author aladdin
 */
public interface ServiceWorker {

    public void doWork(FrameworkMessageContext frameworkMessageContext);

    public String getInfo();
    
    public String getGroup();
    
    public String getDescription();
}
