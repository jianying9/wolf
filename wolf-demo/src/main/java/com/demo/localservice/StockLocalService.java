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
    
    public String getSinaStockCodes(String... id);
    
    public void insertStock(String id, String name);
    
    public List<String> getStockIdAll();
    
    public void insertStockMoneyFlowList(List<Map<String, Object>> updateMapList);
    
    public void insertStockMoneyFlowMinute(Map<String, Object> insertMap);
    
    public void truncateStockMoneyFlowMinute();
    
    public void insertStockMoneyFlowDay(Map<String, Object> insertMap);
}
