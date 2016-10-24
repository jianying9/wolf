package com.wolf.framework.worker;

import com.wolf.framework.service.SessionHandleType;
import com.wolf.framework.worker.context.WorkerContext;
import java.util.Map;

/**
 * 服务工作对象接口
 *
 * @author aladdin
 */
public interface ServiceWorker {

    public void doWork(WorkerContext workerContext);

    public void createInfo();

    public Map<String, String> getInfoMap();
    
    public String getGroup();
    
    public String getRoute();
    
    public String getDesc();
    
    public SessionHandleType getSessionHandleType();
}
