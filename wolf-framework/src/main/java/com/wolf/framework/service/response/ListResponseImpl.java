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
import java.util.Collections;
import java.util.HashMap;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class ListResponseImpl<T extends Entity> extends AbstractResponse implements ListResponse<T> {

    private Long nextIndex = null;
    private final long nextSize;
    private final boolean page;
    private List<Map<String, Object>> dataMapList = null;

    public ListResponseImpl(boolean page, Response response, String[] returnParameter, Map<String, ResponseParameterHandler> responseHandlerMap, ListRequest listServiceRequest) {
        super(response, returnParameter, responseHandlerMap);
        this.page = page;
        this.nextSize = listServiceRequest.getNextSize();
    }

    @Override
    public void setDataMapList(List<Map<String, Object>> dataMapList) {
        for (Map<String, Object> map : dataMapList) {
            //检测响应参数
            this.checkAndFilterDataMap(map);
        }
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
        String dataMessage = "{}";
        List<Map<String, Object>> dataList = Collections.EMPTY_LIST;
        if (this.dataMapList != null) {
            dataList = this.dataMapList;
        }
        Map<String, Object> listMessageMap = new HashMap<>(4, 1);
        if (this.nextIndex != null && this.page) {
            listMessageMap.put("nextIndex", this.nextIndex);
            listMessageMap.put("nextSize", this.nextSize);
        }
        listMessageMap.put("list", dataList);
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
