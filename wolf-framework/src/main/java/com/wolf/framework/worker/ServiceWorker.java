package com.wolf.framework.worker;

import com.wolf.framework.reponse.Response;
import com.wolf.framework.service.ResponseState;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.worker.context.WorkerContext;
import java.util.Map;

/**
 * 服务工作对象接口
 *
 * @author aladdin
 */
public interface ServiceWorker {

    public void doWork(WorkerContext workerContext);

    public void createInfo(String route,
            boolean page,
            boolean validateSession,
            String group,
            String description,
            RequestConfig[] requestConfigs,
            ResponseConfig[] responseConfigs,
            ResponseState[] responseStates);

    public Map<String, String> getInfoMap();
    
    public String getRoute();

    public String getGroup();

    public String getDescription();
    
    public String createResponseMessage(Response response);
}
