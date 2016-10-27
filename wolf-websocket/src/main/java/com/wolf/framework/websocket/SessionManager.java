package com.wolf.framework.websocket;

import com.wolf.framework.comet.CometHandler;
import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.logger.LogFactory;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.Session;
import org.slf4j.Logger;

/**
 *
 * @author jianying9
 */
public class SessionManager implements CometHandler {
    
    //保存session列表
    private final ConcurrentHashMap<String, Session> savedSessionMap = new ConcurrentHashMap<>(4096, 1);
    private final Logger logger = LogFactory.getLogger(FrameworkLogger.WEBSOCKET);
    
    public Session remove(String sid) {
        return this.savedSessionMap.remove(sid);
    }
    
    public Session get(String sid) {
        return this.savedSessionMap.get(sid);
    }
    
    public Session put(String sid, Session session) {
        return this.savedSessionMap.put(sid, session);
    }

    @Override
    public boolean push(String sid, String message) {
        boolean result = false;
        Session session = this.savedSessionMap.get(sid);
        if (session != null && session.isOpen()) {
            result = true;
            session.getAsyncRemote().sendText(message);
            this.logger.debug("websocket-push message:{},{}", sid, message);
        } else {
            this.logger.debug("websocket-push message:sid:{} not exist or closed", sid);
        }
        return result;
    }
    
    public void removSession(String sid) {
        this.savedSessionMap.remove(sid);
        this.logger.debug("websocket-session remove sid:{}", sid);
    }
    
    public synchronized void putNewSession(String sid, Session session) {
        Session other = this.savedSessionMap.get(sid);
        if (other != null) {
            try {
                other.getBasicRemote().sendText("close");
                other.close();
            } catch (IOException ex) {
                this.logger.error("websocket-close sid:{} error:{}", sid, ex.getMessage());
            }
        }
        this.savedSessionMap.put(sid, session);
        this.logger.debug("websocket-session add new sid:{}", sid);
    }
}
