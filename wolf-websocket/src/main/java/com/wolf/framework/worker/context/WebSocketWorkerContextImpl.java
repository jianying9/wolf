package com.wolf.framework.worker.context;

import com.wolf.framework.websocket.SessionManager;
import com.wolf.framework.worker.ServiceWorker;
import java.io.IOException;
import javax.websocket.Session;

/**
 *
 * @author jianying9
 */
public class WebSocketWorkerContextImpl extends AbstractWorkContext {

    private final SessionManager sessionManager;
    private final Session session;

    public WebSocketWorkerContextImpl(SessionManager sessionManager, Session session, String route, String message, ServiceWorker serviceWorker) {
        super(route, message, serviceWorker);
        this.sessionManager = sessionManager;
        this.session = session;
    }

    @Override
    public String getSessionId() {
        String sid = null;
        Object o = this.session.getUserProperties().get("sid");
        if (o != null) {
            sid = (String) o;
        }
        return sid;
    }

    @Override
    public void saveNewSession(String newSid) {
        if (newSid != null) {
            //新session存在
            //判断当前接口是否重复登录
            String sid = this.getSessionId();
            if (sid == null) {
                //当前socket session不存在，为首次链接,保存新的session
                this.session.getUserProperties().put("sid", newSid);
                //保存socket
                this.sessionManager.putNewSession(newSid, this.session);
            } else //当前socket session存在，判断是和新session属于同一个session
            {
                if (sid.equals(newSid) == false) {
                    //切换用户,改变socket session,改变socket的集合id
                    this.sessionManager.removSession(sid);
                    this.session.getUserProperties().put("sid", newSid);
                    this.sessionManager.putNewSession(newSid, this.session);
                }
            }
        }
    }

    @Override
    public void removeSession() {
        String sid = this.getSessionId();
        if (sid != null) {
            this.sessionManager.removSession(sid);
            this.session.getUserProperties().remove("sid");
        }
    }

    @Override
    public void closeSession(String otherSid) {
        String sid = this.getSessionId();
        if (sid == null || sid.equals(otherSid) == false) {
            Session otherSession = this.sessionManager.get(sid);
            otherSession.getUserProperties().remove("sid");
            this.sessionManager.removSession(sid);
            try {
                otherSession.close();
            } catch (IOException ex) {
            }
        }
    }
}
