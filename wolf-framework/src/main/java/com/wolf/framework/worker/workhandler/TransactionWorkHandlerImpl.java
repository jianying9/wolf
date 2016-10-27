package com.wolf.framework.worker.workhandler;

import com.wolf.framework.worker.context.WorkerContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
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
    public void execute(WorkerContext workerContext) {
        UserTransaction userTransaction = null;
        try {
            InitialContext ic = new InitialContext();
            userTransaction = (UserTransaction) ic.lookup("java:comp/UserTransaction");
            userTransaction.begin();
            this.nextWorkHandler.execute(workerContext);
            userTransaction.commit();
        } catch (IllegalStateException | SecurityException | NamingException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException t) {
            if (userTransaction != null) {
                try {
                    userTransaction.rollback();
                } catch (IllegalStateException | SecurityException | SystemException tt) {
                }
            }
            throw new RuntimeException(t);
        }
    }
}
