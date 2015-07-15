package com.demo.service;

import com.demo.entity.StockMoneyFlowEntity;
import com.demo.localservice.StockLocalService;
import com.wolf.framework.data.DataType;
import com.wolf.framework.local.InjectLocalService;
import com.wolf.framework.service.ResponseState;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.worker.context.MessageContext;
import java.util.List;

/**
 *
 * @author jianying9
 */
@ServiceConfig(
        desc = "查询排名前20的股票",
        group = "stock",
        route = "/stock/query/top",
        validateSession = false,
        validateSecurity = false,
        page = true,
        requestConfigs = {
        },
        responseConfigs = {
            @ResponseConfig(name = "sort", dataType = DataType.INTEGER, desc = "排序"),
            @ResponseConfig(name = "id", dataType = DataType.CHAR, desc = "id"),
            @ResponseConfig(name = "name", dataType = DataType.CHAR, desc = "名称"),
            @ResponseConfig(name = "score", dataType = DataType.DOUBLE, desc = "评分"),
            @ResponseConfig(name = "superIn", dataType = DataType.DOUBLE, desc = "超大资金流入"),
            @ResponseConfig(name = "superOut", dataType = DataType.DOUBLE, desc = "超大资金流入"),
            @ResponseConfig(name = "bigIn", dataType = DataType.DOUBLE, desc = "大资金流入"),
            @ResponseConfig(name = "bigOut", dataType = DataType.DOUBLE, desc = "大资金流出"),
            @ResponseConfig(name = "middleIn", dataType = DataType.DOUBLE, desc = "中资金流入"),
            @ResponseConfig(name = "middleOut", dataType = DataType.DOUBLE, desc = "中资金流出"),
            @ResponseConfig(name = "smallIn", dataType = DataType.DOUBLE, desc = "小资金流入"),
            @ResponseConfig(name = "smallOut", dataType = DataType.DOUBLE, desc = "小资金流出"),
            @ResponseConfig(name = "price", dataType = DataType.DOUBLE, desc = "当前价格"),
            @ResponseConfig(name = "changeRatio", dataType = DataType.DOUBLE, desc = "变化比率")
        },
        responseStates = {
            @ResponseState(state = "SUCCESS", desc = "查询成功")
        }
)
public class QueryTopStockServiceImpl implements Service{
    
    @InjectLocalService()
    private StockLocalService stockLocalService;

    @Override
    public void execute(MessageContext messageContext) {
        List<StockMoneyFlowEntity> entityList = this.stockLocalService.queryTopStock(10);
        messageContext.setPageSize(10);
        messageContext.setPageTotal(entityList.size());
        messageContext.setEntityListData(entityList);
        messageContext.success();
    }
}
