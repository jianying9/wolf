package com.wolf.framework.worker.context;

import com.wolf.framework.session.Session;
import com.wolf.framework.websocket.GlobalApplication;
import com.wolf.framework.websocket.GlobalWebSocket;

/**
 *
 * @author aladdin
 */
public class WebSocketWorkerContextImpl extends AbstractWorkContext {

    private final GlobalApplication globalApplication;
    private final GlobalWebSocket globalWebSocket;

    public WebSocketWorkerContextImpl(GlobalApplication globalApplication, GlobalWebSocket globalWebSocket, String act, String message) {
        super(act, message);
        this.globalApplication = globalApplication;
        this.globalWebSocket = globalWebSocket;
    }

    @Override
    public Session getSession() {
        return this.globalWebSocket.getSession();
    }

    @Override
    public void sendMessage() {
        this.globalWebSocket.send(this.responseMessage);
    }

    @Override
    public void close() {
        this.globalWebSocket.close();
    }

    @Override
    public void saveNewSession(Session newSession) {
        if (newSession != null) {
            //新session存在
            //判断当前接口是否重复登录
            Session session = this.globalWebSocket.getSession();
            if (session == null) {
                //当前socket session不存在，为首次链接,保存新的session
                this.globalWebSocket.setSession(newSession);
                //保存socket
                this.globalApplication.putGlobalWebSocket(this.globalWebSocket);
            } else {
                //当前socket session存在，判断是和新session属于同一个用户
                String sid = session.getSid();
                String newSid = newSession.getSid();
                //socketUserId == userId:重复登录,无须任何操作
                if (sid.equals(newSid) == false) {
                    //切换用户,改变socket session,改变socket的集合id
                    this.globalApplication.removGlobalWebSocket(this.globalWebSocket);
                    this.globalWebSocket.setSession(newSession);
                    this.globalApplication.putGlobalWebSocket(this.globalWebSocket);
                }
            }
        }
    }

    @Override
    public void removeSession() {
        Session session = this.globalWebSocket.getSession();
        if (session != null) {
            this.globalApplication.removGlobalWebSocket(this.globalWebSocket);
        }
        this.globalWebSocket.setSession(null);
    }
}
