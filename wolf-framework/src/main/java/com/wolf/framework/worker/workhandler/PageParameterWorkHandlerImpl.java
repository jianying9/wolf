package com.wolf.framework.worker.workhandler;

import com.wolf.framework.service.parameter.InputParameterHandler;
import com.wolf.framework.worker.context.FrameworkMessageContext;

/**
 * 分页参数处理
 *
 * @author aladdin
 */
public class PageParameterWorkHandlerImpl implements WorkHandler {

    private final WorkHandler nextWorkHandler;
    private final InputParameterHandler pageIndexHandler;
    private final InputParameterHandler pageSizeHandler;

    public PageParameterWorkHandlerImpl(
            InputParameterHandler pageIndexHandler,
            InputParameterHandler pageSizeHandler,
            final WorkHandler workHandler) {
        this.nextWorkHandler = workHandler;
        this.pageIndexHandler = pageIndexHandler;
        this.pageSizeHandler = pageSizeHandler;
    }

    @Override
    public void execute(FrameworkMessageContext frameworkMessageContext) {
        String errorMsg;
        String pageIdnex = frameworkMessageContext.getParameter(WorkHandler.PAGE_INDEX);
        if (pageIdnex == null) {
            pageIdnex = "1";
        } else {
            errorMsg = this.pageIndexHandler.validate(pageIdnex);
            if (errorMsg.isEmpty() == false) {
                pageIdnex = "1";
            }
            frameworkMessageContext.removeParameter(WorkHandler.PAGE_INDEX);
        }
        frameworkMessageContext.setPageIndex(Integer.parseInt(pageIdnex));
        String pageSize = frameworkMessageContext.getParameter(WorkHandler.PAGE_SIZE);
        if (pageSize == null) {
            pageSize = "6";
        } else {
            errorMsg = this.pageSizeHandler.validate(pageSize);
            if (errorMsg.isEmpty() == false) {
                pageSize = "6";
            }
            frameworkMessageContext.removeParameter(WorkHandler.PAGE_SIZE);
        }
        frameworkMessageContext.setPageSize(Integer.parseInt(pageSize));
        this.nextWorkHandler.execute(frameworkMessageContext);
    }
}
