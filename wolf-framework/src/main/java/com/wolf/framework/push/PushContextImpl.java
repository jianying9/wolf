package com.wolf.framework.push;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.logger.LogFactory;
import org.slf4j.Logger;

/**
 *
 * @author jianying9
 */
public final class PushContextImpl implements PushContext {

    private final Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);

    private PushHandler pushHandler = null;

    private CometHandler cometHandler = null;

    @Override
    public void setCometHandler(CometHandler cometHandler) {
        this.logger.debug("PushContext add cometHandler:{}", cometHandler);
        this.cometHandler = cometHandler;
    }

    @Override
    public void setPushHandler(PushHandler pushHandler) {
        this.logger.debug("PushContext add pushHandler:{}", pushHandler);
        this.pushHandler = pushHandler;
    }

    @Override
    public boolean push(String sid, String message) {
        boolean result = false;
        if (this.pushHandler != null) {
            result = this.pushHandler.push(sid, message);
        }
        if (this.cometHandler != null) {
            this.cometHandler.push(sid, message);
        }
        return result;
    }

    @Override
    public boolean asyncPush(String sid, String message) {
        boolean result = false;
        if (this.pushHandler != null) {
            result = this.pushHandler.asyncPush(sid, message);
        }
        if (this.cometHandler != null) {
            this.cometHandler.asyncPush(sid, message);
        }
        return result;
    }

    @Override
    public boolean contains(String sid) {
        boolean result = false;
        if (this.pushHandler != null) {
            result = this.pushHandler.contains(sid);
        }
        return result;
    }

}
