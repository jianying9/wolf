package com.wolf.framework.worker;

import com.wolf.framework.service.parameter.InputConfig;
import com.wolf.framework.service.parameter.OutputConfig;
import com.wolf.framework.worker.context.WorkerContext;

/**
 * 服务工作对象接口
 *
 * @author aladdin
 */
public interface ServiceWorker {

    public void doWork(WorkerContext workerContext);

    public void createInfo(String act,
            String group,
            String description,
            InputConfig[] importantParameter,
            InputConfig[] minorParameter,
            OutputConfig[] returnParameter);

    public String getInfo();

    public String getGroup();

    public String getDescription();
}
