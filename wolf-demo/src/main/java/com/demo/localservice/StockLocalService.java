package com.demo.localservice;

import com.wolf.framework.local.Local;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public interface StockLocalService extends Local{
    
    public String getSinaStockInfo(String code);
    
    public String getSinaMoneyFlowInfo(String code);
    
    public String getSinaStockCode(String id);
    
    public String getSinaStockCodes(String[] id);
    
    public void insertStock(String id, String name);
    
    public void updateStockMoneyFlow(Map<String, Object> updateMap);
    
    public void updateStockMoneyFlowList(List<Map<String, Object>> updateMapList);
}
