package com.wolf.framework.service.response;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.reponse.Response;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.service.request.ListServiceRequest;
import com.wolf.framework.utils.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class ListServiceResponseImpl<T extends Entity>  extends AbstractServiceResponse implements ListServiceResponse<T> {
    
    private final String[] returnParameter;
    private final Map<String, ResponseParameterHandler> parameterHandlerMap;
    private String nextIndex;
    private int nextSize;

    public ListServiceResponseImpl(Response response, String[] returnParameter, Map<String, ResponseParameterHandler> parameterHandlerMap, ListServiceRequest listServiceRequest) {
        super(response);
        this.returnParameter = returnParameter;
        this.parameterHandlerMap = parameterHandlerMap;
        this.nextIndex = listServiceRequest.getNextIndex();
        this.nextSize = listServiceRequest.getNextSize();
    }

    @Override
    public void setDataMapList(List<Map<String, String>> dataMapList) {
        String listMessage = JsonUtils.mapListToJSON(dataMapList, this.returnParameter, this.parameterHandlerMap);
        StringBuilder jsonBuilder = new StringBuilder(128);
        jsonBuilder.append("{\"nextIndex\":\"").append(this.nextIndex)
                .append("\",\"nextSize\":").append(this.nextSize)
                .append(",\"list\":[").append(listMessage)
                .append("]}");
        String dataMessage = jsonBuilder.toString();
        this.response.setDataMessage(dataMessage);
    }

    @Override
    public void setEntityList(List<T> tList) {
        List<Map<String, String>> dataMapList = new ArrayList<Map<String, String>>(tList.size());
        for(T t : tList) {
            dataMapList.add(t.toMap());
        }
        this.setDataMapList(dataMapList);
    }

    @Override
    public void setNextIndex(String nextIndex) {
        this.nextIndex = nextIndex;
    }

    @Override
    public void setNextSize(int nextSize) {
        this.nextSize = nextSize;
    }
}
