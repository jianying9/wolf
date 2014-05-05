package com.wolf.framework.worker.context;

import com.wolf.framework.service.parameter.InputParameterHandler;
import com.wolf.framework.service.parameter.OutputParameterHandler;
import com.wolf.framework.utils.JsonUtils;
import com.wolf.framework.worker.workhandler.WorkHandler;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public final class PageMessageContextImpl extends AbstractMessageContext implements FrameworkMessageContext {

    //page
    private long pageIndex = 1;
    private long pageSize = 6;
    private long pageTotal = 0;
    private long pageNum = 0;
    private List<Map<String, String>> mapListData;

    public PageMessageContextImpl(
            WorkerContext workerContext,
            String[] returnParameter,
            Map<String, OutputParameterHandler> parameterHandlerMap,
            InputParameterHandler pageIndexHandler,
            InputParameterHandler pageSizeHandler) {
        super(workerContext, returnParameter, parameterHandlerMap);
        Map<String, String> parameterMap = workerContext.getParameterMap();
        //验证分页参数
        String errorMsg;
        String index = parameterMap.get(WorkHandler.PAGE_INDEX);
        if (index != null) {
            errorMsg = pageIndexHandler.validate(index);
            if (errorMsg.isEmpty()) {
                this.pageIndex = Long.parseLong(index);
            }
        }
        String size = parameterMap.get(WorkHandler.PAGE_SIZE);
        if (size != null) {
            errorMsg = pageSizeHandler.validate(size);
            if (errorMsg.isEmpty()) {
                this.pageSize = Long.parseLong(size);
            }
        }
    }

    @Override
    public final void setMapListData(List<Map<String, String>> parameterMapList) {
        this.mapListData = parameterMapList;
    }

    @Override
    public final long getPageIndex() {
        return pageIndex;
    }

    @Override
    public final long getPageSize() {
        return pageSize;
    }

    @Override
    public final long getPageTotal() {
        return pageTotal;
    }

    @Override
    public final void setPageTotal(long pageTotal) {
        this.pageTotal = pageTotal;
        if (this.pageTotal > 0) {
            long total = this.pageTotal;
            long num = 0;
            while (total > 0) {
                num++;
                total = total - this.pageSize;
            }
            this.pageNum = num;
        }
    }

    @Override
    public final long getPageNum() {
        return pageNum;
    }

    @Override
    public final String createMessage() {
        StringBuilder jsonBuilder = new StringBuilder(128);
        String data = "";
        if (this.returnParameter.length > 0 && this.mapListData != null) {
            data = JsonUtils.mapListToJSON(this.mapListData, this.returnParameter, this.parameterHandlerMap);
        }
        jsonBuilder.append("{\"flag\":\"").append(this.flag)
                .append("\",\"act\":\"").append(this.workerContext.getAct())
                .append("\",\"pageTotal\":").append(this.pageTotal)
                .append(",\"pageIndex\":").append(this.pageIndex)
                .append(",\"pageSize\":").append(this.pageSize)
                .append(",\"pageNum\":").append(this.pageNum)
                .append(",\"data\":[").append(data).append("]}");
        return jsonBuilder.toString();
    }

    @Override
    public void setMapData(Map<String, String> parameterMap) {
        throw new UnsupportedOperationException("Not supported.Check ServiceConfig page must be false");
    }
}
