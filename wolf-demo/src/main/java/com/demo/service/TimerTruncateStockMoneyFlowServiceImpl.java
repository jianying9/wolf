package com.demo.service;

import com.demo.localservice.StockLocalService;
import com.wolf.framework.local.InjectLocalService;
import com.wolf.framework.service.ResponseState;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.task.InjectTaskExecutor;
import com.wolf.framework.task.Task;
import com.wolf.framework.task.TaskExecutor;
import com.wolf.framework.worker.context.MessageContext;

/**
 *
 * @author jianying9
 */
@ServiceConfig(
        desc = "清空资金分钟级别数据",
        group = "stock",
        route = "/stock/moneyflow/minute/timer/truncate",
        validateSession = false,
        validateSecurity = false,
        requestConfigs = {
        },
        responseConfigs = {
        },
        responseStates = {
            @ResponseState(state = "SUCCESS", desc = "更新成功"),
            @ResponseState(state = "FAILURE", desc = "更新失败")
        }
)
public class TimerTruncateStockMoneyFlowServiceImpl implements Service {

    @InjectLocalService()
    private StockLocalService stockLocalService;
    //
    //状态：stop-正常停止,running-运行中
    private volatile String state = "stop";
    //
    @InjectTaskExecutor
    private TaskExecutor taskExecutor;

    private class TruncateMoneyFlowMinuteTaskImpl extends Task {

        private final MessageContext messageContext;

        public TruncateMoneyFlowMinuteTaskImpl(MessageContext messageContext) {
            this.messageContext = messageContext;
        }

        @Override
        public void doWhenRejected() {
        }

        @Override
        protected void execute() {
            stockLocalService.truncateStockMoneyFlowMinute();
            state = "stop";
        }
    }

    @Override
    public void execute(MessageContext messageContext) {
        Task task = new TruncateMoneyFlowMinuteTaskImpl(messageContext);
        synchronized (this) {
            if (this.state.equals("stop")) {
                this.state = "running";
                this.taskExecutor.submit(task);
            }
        }
        messageContext.success();
    }
}
