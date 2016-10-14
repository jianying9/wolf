package com.wolf.framework.worker.context;

import com.wolf.framework.websocket.GlobalApplication;
import com.wolf.framework.websocket.GlobalWebSocket;
import com.wolf.framework.worker.ServiceWorker;

/**
 *
 * @author aladdin
 */
public class WebSocketWorkerContextImpl extends AbstractWorkContext {

    private final GlobalApplication globalApplication;
    private final GlobalWebSocket globalWebSocket;

    public WebSocketWorkerContextImpl(GlobalApplication globalApplication, GlobalWebSocket globalWebSocket, String act, String message, ServiceWorker serviceWorker) {
        super(act, message, serviceWorker);
        this.globalApplication = globalApplication;
        this.globalWebSocket = globalWebSocket;
    }

    @Override
    public String getSessionId() {
        return this.globalWebSocket.getSessionId();
    }

    @Override
    public void saveNewSession(String newSid) {
        if (newSid != null) {
            //新session存在
            //判断当前接口是否重复登录
            String sid = this.globalWebSocket.getSessionId();
            if (sid == null) {
                //当前socket session不存在，为首次链接,保存新的session
                this.globalWebSocket.setSessionId(newSid);
                //保存socket
                this.globalApplication.putGlobalWebSocket(this.globalWebSocket);
            } else {
                //当前socket session存在，判断是和新session属于同一个session
                if (sid.equals(newSid) == false) {
                    //切换用户,改变socket session,改变socket的集合id
                    this.globalApplication.removGlobalWebSocket(sid);
                    this.globalWebSocket.setSessionId(newSid);
                    this.globalApplication.putGlobalWebSocket(this.globalWebSocket);
                }
            }
        }
    }

    @Override
    public void removeSession() {
        String sid = this.globalWebSocket.getSessionId();
        this.globalApplication.removGlobalWebSocket(sid);
        this.globalWebSocket.setSessionId(null);
    }
}
