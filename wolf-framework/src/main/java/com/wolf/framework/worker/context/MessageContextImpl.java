package com.wolf.framework.worker.context;

import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.session.Session;
import com.wolf.framework.utils.JsonUtils;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public final class MessageContextImpl extends AbstractMessageContext {

    private Map<String, String> mapData;

    public MessageContextImpl(WorkerContext workerContext, String[] returnParameter, Map<String, ResponseParameterHandler> parameterHandlerMap) {
        super(workerContext, returnParameter, parameterHandlerMap);
    }

    @Override
    public String createMessage() {
        StringBuilder jsonBuilder = new StringBuilder(64);
        String data = "{}";
        if (this.returnParameter.length > 0 && this.mapData != null) {
            data = JsonUtils.mapToJSON(this.mapData, this.returnParameter, this.parameterHandlerMap);
        }
        jsonBuilder.append("{\"state\":\"").append(this.state)
                .append("\",\"act\":\"").append(this.workerContext.getAct())
                .append("\",\"data\":").append(data);
        Session session = this.getNewSession();
        if(session != null) {
            jsonBuilder.append(",\"sid\":\"").append(session.getSid()).append('"');
        }
        jsonBuilder.append('}');
        return jsonBuilder.toString();
    }

    @Override
    public void setMapData(Map<String, String> parameterMap) {
        this.mapData = parameterMap;
    }
    
    @Override
    public Map<String, String> getMapData() {
        return this.mapData;
    }

    @Override
    public final long getPageIndex() {
        throw new UnsupportedOperationException("Not supported.Check ServiceConfig page must be true");
    }

    @Override
    public long getPageSize() {
        throw new UnsupportedOperationException("Not supported.Check ServiceConfig page must be true");
    }

    @Override
    public long getPageTotal() {
        throw new UnsupportedOperationException("Not supported.Check ServiceConfig page must be true");
    }

    @Override
    public long getPageNum() {
        throw new UnsupportedOperationException("Not supported.Check ServiceConfig page must be true");
    }

    @Override
    public void setPageTotal(long pageTotal) {
        throw new UnsupportedOperationException("Not supported.Check ServiceConfig page must be true");
    }

    @Override
    public void setMapListData(List<Map<String, String>> parameterMapList) {
        throw new UnsupportedOperationException("Not supported.Check ServiceConfig page must be true");
    }

    @Override
    public List<Map<String, String>> getMapListData() {
        throw new UnsupportedOperationException("Not supported.Check ServiceConfig page must be true");
    }
}
