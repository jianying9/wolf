package com.wolf.framework.worker.workhandler;

import com.wolf.framework.utils.SecurityUtils;
import com.wolf.framework.worker.context.FrameworkMessageContext;
import com.wolf.framework.worker.context.WorkerContext;

/**
 * 必要参数处理
 *
 * @author aladdin
 */
public class ValidateSecurityWorkHandlerImpl implements WorkHandler {

    private final WorkHandler nextWorkHandler;
    private final String key;
    private final long error;

    public ValidateSecurityWorkHandlerImpl(
            final String key,
            final long error,
            final WorkHandler workHandler) {
        this.nextWorkHandler = workHandler;
        this.key = key;
        this.error = error;
    }

    @Override
    public void execute(FrameworkMessageContext frameworkMessageContext) {
        WorkerContext workerContext = frameworkMessageContext.getWorkerContext();
        long clientTime = 0;
        long systemTime = System.currentTimeMillis();
        //前端传递的seed为des加密后的16进制编码
        String entrySeedHex = workerContext.getParameterMap().get("seed");
        if (entrySeedHex == null) {
            //客户端没有提交时间的加密，直接阻止
            frameworkMessageContext.denied();
            String message = frameworkMessageContext.createErrorMessage();
            workerContext.sendMessage(message);
        } else {
            //16进制编码转成byte[]
            byte[] entrySeedByte = SecurityUtils.hexStringToByte(entrySeedHex);
            String seed = SecurityUtils.decryptByDes(entrySeedByte, this.key);
            try {
                //seed明文为客户端计算得出的服务端的时间
                clientTime = Long.parseLong(seed);
            } catch (NumberFormatException ex) {
            }
            long diffTime = systemTime - clientTime;
            long min = - error;
            if (diffTime >= min && diffTime <= error) {
                //时间相差在60秒以内
                //验证通过
                this.nextWorkHandler.execute(frameworkMessageContext);
            } else {
                //时间验证不过，阻止访问，返回正确的时间
                frameworkMessageContext.denied();
                frameworkMessageContext.setError(Long.toString(System.currentTimeMillis()));
                String message = frameworkMessageContext.createErrorMessage();
                workerContext.sendMessage(message);
            }
        }
    }
}
