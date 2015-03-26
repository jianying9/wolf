package com.wolf.framework.comet;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.logger.LogFactory;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

/**
 *
 * @author jianying9
 */
public final class CometContextImpl implements CometContext {
    
    private final Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);

    private final List<CometHandler> cometHanlderList = new ArrayList<CometHandler>(2);

    @Override
    public boolean push(String sid, String message) {
        boolean result = false;
        boolean isPush;
        for (CometHandler cometHandler : this.cometHanlderList) {
            isPush = cometHandler.push(sid, message);
            if(isPush) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public void addCometHandler(CometHandler cometHandler) {
        this.logger.debug("CometContext add cometHandler:{}", cometHandler);
        this.cometHanlderList.add(cometHandler);
    }
}
