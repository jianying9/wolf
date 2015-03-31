package com.wolf.framework.module;

import com.sun.grizzly.websockets.WebSocketEngine;
import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.context.Resource;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.websocket.GlobalApplication;
import org.slf4j.Logger;

/**
 *
 * @author jianying9
 */
public class WebsocketModuleImpl implements Module {

    Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);

    @Override
    public void init(ApplicationContext context) {
        String websocket = context.getParameter(FrameworkConfig.WEBSOCKET);
        if (websocket != null && websocket.equals("on")) {
            logger.info("Start websocket...");
            String appContextPath = context.getAppContextPath();
            //开启websocket应用
            GlobalApplication app = new GlobalApplication(appContextPath);
            WebSocketEngine.getEngine().register(app);
            //注册推送服务
            context.getCometContext().addCometHandler(app);
            //资源销毁
            Resource resource = new WebsocketResourceImpl(app);
            context.addResource(resource);
        }
    }
}
