package com.demo.service;

import com.demo.localservice.StockLocalService;
import com.wolf.framework.data.DataType;
import com.wolf.framework.local.InjectLocalService;
import com.wolf.framework.service.ResponseState;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.worker.context.MessageContext;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 */
@ServiceConfig(
        desc = "新增stock",
        group = "stock",
        route = "/stock/insert",
        validateSession = false,
        validateSecurity = false,
        requestConfigs = {
            @RequestConfig(name = "id", must = true, dataType = DataType.CHAR, max = 6, min = 6, desc = "id")
        },
        responseConfigs = {
            @ResponseConfig(name = "id", dataType = DataType.CHAR, desc = "id"),
            @ResponseConfig(name = "name", dataType = DataType.CHAR, desc = "名称")
        },
        responseStates = {
            @ResponseState(state = "SUCCESS", desc = "新增成功"),
            @ResponseState(state = "FAILURE", desc = "新增失败,非法id")
        }
)
public class InsertStockServiceImpl implements Service{
    
    @InjectLocalService()
    private StockLocalService stockLocalService;

    @Override
    public void execute(MessageContext messageContext) {
        String id = messageContext.getParameter("id");
        String code = this.stockLocalService.getSinaStockCode(id);
        if(code.isEmpty() == false) {
            String info = this.stockLocalService.getSinaStockInfo(code);
            String[] infoArray = info.split("=");
            if(infoArray.length == 2) {
                String stockInfo = infoArray[1];
                stockInfo = stockInfo.replace("\"", "");
                if(stockInfo.equals("") == false && stockInfo.equals("FAILED") == false) {
                    String[] stockInfoArray = stockInfo.split(",");
                    String name = stockInfoArray[0];
                    this.stockLocalService.insertStock(id, name);
                    Map<String, String> responseMap = new HashMap<String, String>(2, 1);
                    responseMap.put("id", id);
                    responseMap.put("name", name);
                    messageContext.setMapData(responseMap);
                    messageContext.success();
                }
            }
        }
    }
}
