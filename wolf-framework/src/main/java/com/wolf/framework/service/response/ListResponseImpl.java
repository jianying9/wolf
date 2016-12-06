package com.wolf.framework.service.response;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.reponse.Response;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.utils.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.wolf.framework.service.request.ListRequest;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class ListResponseImpl<T extends Entity>  extends AbstractServiceResponse implements ListResponse<T> {
    
    private final String[] returnParameter;
    private final Map<String, ResponseParameterHandler> parameterHandlerMap;
    private String nextIndex;
    private int nextSize;
    private List<Map<String, String>> dataMapList = null;

    public ListResponseImpl(Response response, String[] returnParameter, Map<String, ResponseParameterHandler> parameterHandlerMap, ListRequest listServiceRequest) {
        super(response);
        this.returnParameter = returnParameter;
        this.parameterHandlerMap = parameterHandlerMap;
        this.nextIndex = listServiceRequest.getNextIndex();
        this.nextSize = listServiceRequest.getNextSize();
    }

    @Override
    public void setDataMapList(List<Map<String, String>> dataMapList) {
        this.dataMapList = dataMapList;
    }

    @Override
    public void setEntityList(List<T> tList) {
        List<Map<String, String>> dataMapListTemp = new ArrayList<>(tList.size());
        for(T t : tList) {
            dataMapListTemp.add(t.toMap());
        }
        this.setDataMapList(dataMapListTemp);
    }

    @Override
    public void setNextIndex(String nextIndex) {
        this.nextIndex = nextIndex;
    }

    @Override
    public void setNextSize(int nextSize) {
        this.nextSize = nextSize;
    }

    @Override
    public String getDataMessage() {
        String listMessage = JsonUtils.mapListToJSON(this.dataMapList, this.returnParameter, this.parameterHandlerMap);
        StringBuilder jsonBuilder = new StringBuilder(128);
        jsonBuilder.append("{\"nextIndex\":\"").append(this.nextIndex)
                .append("\",\"nextSize\":").append(this.nextSize)
                .append(",\"list\":").append(listMessage)
                .append("}");
        String dataMessage = jsonBuilder.toString();
        return dataMessage;
    }
}
