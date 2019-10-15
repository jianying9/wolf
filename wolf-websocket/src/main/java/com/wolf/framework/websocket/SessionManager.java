package com.wolf.framework.websocket;

import com.wolf.framework.logger.AccessLogger;
import com.wolf.framework.logger.AccessLoggerFactory;
import com.wolf.framework.push.PushHandler;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.Session;

/**
 *
 * @author jianying9
 */
public class SessionManager implements PushHandler {

    //保存session列表
    private final ConcurrentHashMap<String, Session> savedSessionMap = new ConcurrentHashMap<>(4096, 1);

    public Session remove(String sid) {
        return this.savedSessionMap.remove(sid);
    }

    public Session get(String sid) {
        return this.savedSessionMap.get(sid);
    }

    public Session put(String sid, Session session) {
        return this.savedSessionMap.put(sid, session);
    }

    public Collection<Session> getSessions() {
        return savedSessionMap.values();
    }

    @Override
    public boolean asyncPush(String sid, String route, String message) {
        boolean result = false;
        Session session = this.savedSessionMap.get(sid);
        if (session != null && session.isOpen()) {
            result = true;
            session.getAsyncRemote().sendText(message);
            AccessLogger accessLogger = AccessLoggerFactory.getAccessLogger();
            accessLogger.log(route, sid, "", message, -1);
        }
        return result;
    }

    @Override
    public boolean push(String sid, String route, String message) {
        boolean result = false;
        Session session = this.savedSessionMap.get(sid);
        if (session != null && session.isOpen()) {
            result = true;
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException ex) {
            }
            AccessLogger accessLogger = AccessLoggerFactory.getAccessLogger();
            accessLogger.log(route, sid, "", message, -1);
        }
        return result;
    }

    public void removSession(String sid) {
        this.savedSessionMap.remove(sid);
    }

    public synchronized void putNewSession(String sid, Session session) {
        Session other = this.savedSessionMap.get(sid);
        if (other != null && other.isOpen()) {
            try {
                other.close();
            } catch (IOException ex) {
            }
        }
        this.savedSessionMap.put(sid, session);
    }

    @Override
    public boolean contains(String sid) {
        //ConcurrentHashMap已经remove某个key,通过get方法返回null,但是通过containsKey查询key是否存在,会返回true
        boolean result = false;
        Session session = this.savedSessionMap.get(sid);
        if (session != null && session.isOpen()) {
            result = true;
        }
        return result;
    }
}
