package com.demo.service;

import com.demo.localservice.StockLocalService;
import com.demo.utils.StockUtils;
import com.wolf.framework.data.DataType;
import com.wolf.framework.local.InjectLocalService;
import com.wolf.framework.service.ResponseState;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.worker.context.MessageContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

/**
 *
 * @author jianying9
 */
@ServiceConfig(
        desc = "更新stock资金流入信息",
        group = "stock",
        route = "/stock/moneyflow/update",
        validateSession = false,
        validateSecurity = false,
        requestConfigs = {
            @RequestConfig(name = "id", must = true, dataType = DataType.CHAR, max = 4000, min = 6, desc = "id")
        },
        responseConfigs = {
            @ResponseConfig(name = "id", dataType = DataType.CHAR, desc = "id"),
            @ResponseConfig(name = "name", dataType = DataType.CHAR, desc = "名称")
        },
        responseStates = {
            @ResponseState(state = "SUCCESS", desc = "更新成功"),
            @ResponseState(state = "FAILURE", desc = "更新失败,非法id")
        }
)
public class UpdateStockMoneyFlowServiceImpl implements Service {

    @InjectLocalService()
    private StockLocalService stockLocalService;
    //

    @Override
    public void execute(MessageContext messageContext) {
        String id = messageContext.getParameter("id");
        String[] idArray = id.split(",");
        String code = this.stockLocalService.getSinaStockCodes(idArray);
        if (code.isEmpty() == false) {
            String info = this.stockLocalService.getSinaMoneyFlowInfo(code);
            info = info.replace("(", "[");
            info = info.replace(")", "]");
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            ArrayNode rootNode = null;
            try {
                rootNode = mapper.readValue(info, ArrayNode.class);
            } catch (IOException e) {
                System.err.println(e);
            }
            if (rootNode != null) {
                Iterator<JsonNode> iterator = rootNode.getElements();
                JsonNode jsonNode;
                Map<String, Object> moneyFlowMap;
                if (idArray.length == 1) {
                    //只有一个id
                    jsonNode = iterator.next();
                    moneyFlowMap = StockUtils.createMoneyFlowMap(jsonNode);
                    moneyFlowMap.put("id", id);
                    this.stockLocalService.updateStockMoneyFlow(moneyFlowMap);

                } else {
                    //多个id
                    List<Map<String, Object>> moneyFlowMapList = new ArrayList<Map<String, Object>>();
                    while (iterator.hasNext()) {
                        jsonNode = iterator.next();
                        moneyFlowMap = StockUtils.createMoneyFlowMap(jsonNode);
                        moneyFlowMapList.add(moneyFlowMap);
                    }
                    this.stockLocalService.updateStockMoneyFlowList(moneyFlowMapList);
                }
            }
        }
    }
}
