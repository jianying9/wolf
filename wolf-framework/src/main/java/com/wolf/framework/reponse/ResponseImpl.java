package com.wolf.framework.reponse;

import com.wolf.framework.config.ResponseCodeConfig;
import com.wolf.framework.service.ResponseCode;
import com.wolf.framework.service.SessionHandleType;
import com.wolf.framework.service.context.ServiceContext;
import com.wolf.framework.utils.SecurityUtils;
import com.wolf.framework.worker.context.WorkerContext;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author jianying9
 */
public class ResponseImpl implements WorkerResponse {

    private final WorkerContext workerContext;
    //message
    private String error = "";
    private String dataMessage = "{}";
    private String responseMessage = "";
    private String code = ResponseCodeConfig.SUCCESS;
    private String newSessionId = null;
    private String pushId = null;
    private final Set<String> customCodeSet;

    public ResponseImpl(WorkerContext workerContext) {
        this.workerContext = workerContext;
        ServiceContext serviceContext = this.workerContext.getServiceWorker().getServiceContext();
        ResponseCode[] responseCodes = serviceContext.responseCodes();
        this.customCodeSet = new HashSet<>(responseCodes.length);
        for (ResponseCode responseCode : responseCodes) {
            this.customCodeSet.add(responseCode.code());
        }
    }

    public final WorkerContext getWorkerContext() {
        return this.workerContext;
    }

    @Override
    public final String getCode() {
        return this.code;
    }

    @Override
    public final void denied() {
        this.code = ResponseCodeConfig.DENIED;
    }

    @Override
    public final void invalid() {
        this.code = ResponseCodeConfig.INVALID;
    }

    @Override
    public final void unlogin() {
        this.code = ResponseCodeConfig.UNLOGIN;
    }
    
    @Override
    public final void timeout() {
        this.code = ResponseCodeConfig.TIMEOUT;
    }

    @Override
    public final void success() {
        this.code = ResponseCodeConfig.SUCCESS;
    }
    
    @Override
    public final void exception() {
        this.code = ResponseCodeConfig.EXCEPTION;
    }
    
    @Override
    public final void unsupport() {
        this.code = ResponseCodeConfig.UNKNOWN;
    }

    @Override
    public final void setCode(String code) {
        if(this.customCodeSet.contains(code)) {
            this.code = code;
        } else {
            this.code = ResponseCodeConfig.UNKNOWN;
        }
    }

    @Override
    public final void setError(String error) {
        this.error = error;
    }

    private void createResponseMessage(boolean isPush) {
        ServiceContext serviceContext = this.workerContext.getServiceWorker().getServiceContext();
        StringBuilder jsonBuilder = new StringBuilder(this.dataMessage.length() + 256);
        String md5 = workerContext.getMd5();
        String msg = this.dataMessage;
        if(md5 != null) {
            //判断数据是否有变化
            String newMd5 = SecurityUtils.encryptByMd5(this.dataMessage);
            if(md5.equals(newMd5)) {
                //数据没有变化
                this.code = ResponseCodeConfig.UNMODIFYED;
                msg = "{}";
            } else {
                md5 = newMd5;
            }
        }
        jsonBuilder.append("{\"code\":\"").append(this.code)
                .append("\",\"route\":\"").append(this.workerContext.getRoute());
        if(md5 != null) {
            jsonBuilder.append("\",\"md5\":\"").append(md5);
        }
        if (this.newSessionId != null && serviceContext.sessionHandleType() == SessionHandleType.SAVE) {
            jsonBuilder.append("\",\"sid\":\"").append(this.newSessionId);
        }
        if (this.error.isEmpty() == false) {
            jsonBuilder.append("\",\"error\":\"").append(this.error);
        }
        String callback = workerContext.getCallback();
        if (callback != null && isPush == false) {
            jsonBuilder.append("\",\"callback\":\"").append(callback);
        }
        if (isPush && this.pushId != null) {
            jsonBuilder.append("\",\"pushId\":\"").append(this.pushId);
        }
        jsonBuilder.append("\",\"data\":").append(msg).append("}");
        this.responseMessage = jsonBuilder.toString();
    }


    @Override
    public final String getResponseMessage() {
        this.createResponseMessage(false);
        return this.responseMessage;
    }
    
    @Override
    public String getPushMessage() {
        this.createResponseMessage(true);
        return this.responseMessage;
    }

    @Override
    public String getDataMessage() {
        return this.dataMessage;
    }

    @Override
    public void setDataMessage(String dataMessage) {
        this.dataMessage = dataMessage;
    }

    @Override
    public String getNewSessionId() {
        return this.newSessionId;
    }

    @Override
    public void setNewSessionId(String newSessionId) {
        this.newSessionId = newSessionId;
    }

    @Override
    public void closeOtherSession(String otherSid) {
        this.workerContext.closeSession(otherSid);
    }

    @Override
    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    @Override
    public String getPushId() {
        return this.pushId;
    }
}
