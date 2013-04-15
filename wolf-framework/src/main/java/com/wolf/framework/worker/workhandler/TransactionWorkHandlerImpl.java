package com.wolf.framework.worker.workhandler;

import com.wolf.framework.worker.context.FrameworkMessageContext;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

/**
 * 事物处理类
 *
 * @author aladdin
 */
public class TransactionWorkHandlerImpl implements WorkHandler {

    private final WorkHandler nextWorkHandler;

    public TransactionWorkHandlerImpl(final WorkHandler workHandler) {
        this.nextWorkHandler = workHandler;
    }

    @Override
    public void execute(FrameworkMessageContext frameworkMessageContext) {
        UserTransaction userTransaction = null;
        try {
            InitialContext ic = new InitialContext();
            userTransaction = (UserTransaction) ic.lookup("java:comp/UserTransaction");
            userTransaction.begin();
            this.nextWorkHandler.execute(frameworkMessageContext);
            userTransaction.commit();
        } catch (Throwable t) {
            if (userTransaction != null) {
                try {
                    userTransaction.rollback();
                } catch (Throwable tt) {
                }
            }
            throw new RuntimeException(t);
        }
    }
}
