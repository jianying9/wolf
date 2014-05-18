package com.wolf.framework.worker.context;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public interface Response {

    public long getPageIndex();

    public long getPageSize();

    public long getPageTotal();

    public long getPageNum();

    public String getFlag();

    public Map<String, String> getMapData();

    public List<Map<String, String>> getMapListData();
    
    public String getResponseMessage();
    
    public String getResponseMessage(boolean useCache);
}
