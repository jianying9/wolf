package com.wolf.framework.service.response;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.reponse.Response;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import com.wolf.framework.utils.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.wolf.framework.service.request.ListRequest;
import com.wolf.framework.utils.EntityUtils;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class ListResponseImpl<T extends Entity>  extends AbstractServiceResponse implements ListResponse<T> {
    
    private final String[] returnParameter;
    private final Map<String, ResponseParameterHandler> parameterHandlerMap;
    private long nextIndex = 0;
    private final long nextSize;
    private List<Map<String, String>> dataMapList = null;

    public ListResponseImpl(Response response, String[] returnParameter, Map<String, ResponseParameterHandler> parameterHandlerMap, ListRequest listServiceRequest) {
        super(response);
        this.returnParameter = returnParameter;
        this.parameterHandlerMap = parameterHandlerMap;
        this.nextSize = listServiceRequest.getNextSize();
    }

    @Override
    public void setDataMapList(List<Map<String, String>> dataMapList) {
        this.dataMapList = dataMapList;
    }

    @Override
    public void setEntityList(List<T> tList) {
        List<Map<String, String>> dataMapListTemp = new ArrayList<>(tList.size());
        Map<String, String> tMap;
        for(T t : tList) {
            tMap = EntityUtils.getMap(t);
            dataMapListTemp.add(tMap);
        }
        this.setDataMapList(dataMapListTemp);
    }

    @Override
    public String getDataMessage() {
        String listMessage = JsonUtils.mapListToJSON(this.dataMapList, this.returnParameter, this.parameterHandlerMap);
        StringBuilder jsonBuilder = new StringBuilder(128);
        jsonBuilder.append("{\"nextIndex\":").append(this.nextIndex)
                .append(",\"nextSize\":").append(this.nextSize)
                .append(",\"list\":").append(listMessage)
                .append("}");
        String dataMessage = jsonBuilder.toString();
        return dataMessage;
    }

    @Override
    public void setNextIndex(long nextIndex) {
        this.nextIndex = nextIndex;
    }
}
