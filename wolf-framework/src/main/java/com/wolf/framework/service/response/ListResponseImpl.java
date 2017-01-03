package com.wolf.framework.service.response;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.reponse.Response;
import com.wolf.framework.service.parameter.ResponseParameterHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.wolf.framework.service.request.ListRequest;
import com.wolf.framework.utils.EntityUtils;
import java.io.IOException;
import java.util.HashMap;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class ListResponseImpl<T extends Entity> extends AbstractResponse implements ListResponse<T> {

    private final String[] returnParameter;
    private final Map<String, ResponseParameterHandler> parameterHandlerMap;
    private Long nextIndex = null;
    private final long nextSize;
    private final boolean page;
    private List<Map<String, Object>> dataMapList = null;

    public ListResponseImpl(boolean page, Response response, String[] returnParameter, Map<String, ResponseParameterHandler> parameterHandlerMap, ListRequest listServiceRequest) {
        super(response);
        this.page = page;
        this.returnParameter = returnParameter;
        this.parameterHandlerMap = parameterHandlerMap;
        this.nextSize = listServiceRequest.getNextSize();
    }

    @Override
    public void setDataMapList(List<Map<String, Object>> dataMapList) {
        this.dataMapList = dataMapList;
    }

    @Override
    public void setEntityList(List<T> tList) {
        List<Map<String, Object>> dataMapListTemp = new ArrayList<>(tList.size());
        Map<String, Object> tMap;
        for (T t : tList) {
            tMap = EntityUtils.getMap(t);
            dataMapListTemp.add(tMap);
        }
        this.setDataMapList(dataMapListTemp);
    }

    @Override
    public String getDataMessage() {
        String dataMessage = null;
        if (this.dataMapList != null) {
            Map<String, Object> dataMap;
            Object paraValue;
            ResponseParameterHandler responseParameterHandler;
            for (int i = 0; i < this.dataMapList.size(); i++) {
                dataMap = this.dataMapList.get(i);
                if (dataMap != null) {
                    for (String paraName : this.returnParameter) {
                        paraValue = dataMap.get(paraName);
                        if (paraValue != null) {
                            responseParameterHandler = this.parameterHandlerMap.get(paraName);
                            paraValue = responseParameterHandler.getResponseValue(paraValue);
                            dataMap.put(paraName, paraValue);
                        }
                    }
                }
            }
        }
        Map<String, Object> listMessageMap = new HashMap<>(4, 1);
        if (this.nextIndex != null && this.page) {
            listMessageMap.put("nextIndex", this.nextIndex);
            listMessageMap.put("nextSize", this.nextSize);
        }
        listMessageMap.put("list", this.dataMapList);
        //输出json
        ObjectMapper mapper = new ObjectMapper();
        try {
            dataMessage = mapper.writeValueAsString(listMessageMap);
        } catch (IOException ex) {
        }
        return dataMessage;
    }

    @Override
    public void setNextIndex(long nextIndex) {
        this.nextIndex = nextIndex;
    }
}
