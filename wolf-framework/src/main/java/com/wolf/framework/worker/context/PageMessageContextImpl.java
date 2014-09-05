package com.wolf.framework.worker.context;

import com.wolf.framework.service.parameter.RequestParameterHandler;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
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
    private long pageTotal = -1;
    private long pageNum = -1;
    private List<Map<String, String>> mapListData;

    public PageMessageContextImpl(
            WorkerContext workerContext,
            String[] returnParameter,
            Map<String, ResponseParameterHandler> parameterHandlerMap,
            RequestParameterHandler pageIndexHandler,
            RequestParameterHandler pageSizeHandler) {
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
    public void setMapListData(List<Map<String, String>> parameterMapList) {
        this.mapListData = parameterMapList;
    }

    @Override
    public List<Map<String, String>> getMapListData() {
        return this.mapListData;
    }

    @Override
    public long getPageIndex() {
        return pageIndex;
    }

    @Override
    public long getPageSize() {
        return pageSize;
    }

    @Override
    public long getPageTotal() {
        return pageTotal;
    }

    @Override
    public void setPageTotal(long pageTotal) {
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
    public long getPageNum() {
        return pageNum;
    }

    @Override
    public final String createMessage() {
        StringBuilder jsonBuilder = new StringBuilder(128);
        String data = "";
        if (this.returnParameter.length > 0 && this.mapListData != null) {
            data = JsonUtils.mapListToJSON(this.mapListData, this.returnParameter, this.parameterHandlerMap);
        }
        jsonBuilder.append("{\"state\":\"").append(this.state)
                .append("\",\"act\":\"").append(this.workerContext.getAct())
                .append("\",\"data\":{")
                .append("\"pageTotal\":").append(this.pageTotal)
                .append(",\"pageIndex\":").append(this.pageIndex)
                .append(",\"pageSize\":").append(this.pageSize)
                .append(",\"pageNum\":").append(this.pageNum)
                .append(",\"list\":[").append(data).append("]}");
        if (this.newSid != null) {
            jsonBuilder.append(",\"sid\":\"").append(this.newSid).append('"');
        }
        jsonBuilder.append('}');
        return jsonBuilder.toString();
    }

    @Override
    public void setMapData(Map<String, String> parameterMap) {
        throw new UnsupportedOperationException("Not supported.Check ServiceConfig page must be false");
    }

    @Override
    public Map<String, String> getMapData() {
        throw new UnsupportedOperationException("Not supported.Check ServiceConfig page must be false");
    }
}
