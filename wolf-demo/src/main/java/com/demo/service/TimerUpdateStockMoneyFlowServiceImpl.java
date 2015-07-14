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
import com.wolf.framework.task.InjectTaskExecutor;
import com.wolf.framework.task.Task;
import com.wolf.framework.task.TaskExecutor;
import com.wolf.framework.utils.TimeUtils;
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
        desc = "按时间更新资金数据",
        group = "stock",
        route = "/stock/moneyflow/timer/update",
        validateSession = false,
        validateSecurity = false,
        requestConfigs = {
            @RequestConfig(name = "type", must = true, dataType = DataType.CHAR, max = 6, min = 3, desc = "类型:day,minute")
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
public class TimerUpdateStockMoneyFlowServiceImpl implements Service {

    @InjectLocalService()
    private StockLocalService stockLocalService;
    //
    //状态：stop-正常停止,running-运行中
    private volatile String state = "stop";
    //
    @InjectTaskExecutor
    private TaskExecutor taskExecutor;

    private abstract class AbstractTask extends Task {

        protected final MessageContext messageContext;
        protected final List<String> idList;

        public AbstractTask(MessageContext messageContext, List<String> idList) {
            this.messageContext = messageContext;
            this.idList = idList;
        }
        
        protected abstract void save(ArrayNode arrayNode);

        @Override
        public void doWhenRejected() {
        }

        @Override
        protected final void execute() {
            String code = stockLocalService.getSinaStockCodes(this.idList.toArray(new String[this.idList.size()]));
            String info = stockLocalService.getSinaMoneyFlowInfo(code);
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
                this.save(rootNode);
            }
            //
            state = "stop";
        }
    }

    private class UpdateMoneyFlowHistoryTaskImpl extends AbstractTask {

        public UpdateMoneyFlowHistoryTaskImpl(MessageContext messageContext, List<String> idList) {
            super(messageContext, idList);
        }

        @Override
        protected void save(ArrayNode arrayNode) {
            Iterator<JsonNode> iterator = arrayNode.getElements();
            JsonNode jsonNode;
            Map<String, Object> moneyFlowMap;
            String day = TimeUtils.getDateFotmatYYMMDD();
            if (this.idList.size() == 1) {
                //只有一个id
                jsonNode = iterator.next();
                moneyFlowMap = StockUtils.createMoneyFlowMap(jsonNode);
                moneyFlowMap.put("id", this.idList.get(0));
                moneyFlowMap.put("day", day);
                stockLocalService.insertStockMoneyFlowDay(moneyFlowMap);
            } else {
                //多个id
                while (iterator.hasNext()) {
                    jsonNode = iterator.next();
                    moneyFlowMap = StockUtils.createMoneyFlowMap(jsonNode);
                    moneyFlowMap.put("day", day);
                    stockLocalService.insertStockMoneyFlowDay(moneyFlowMap);
                }
            }
        }

    }

    private class UpdateMoneyFlowMinuteTaskImpl extends AbstractTask {


        public UpdateMoneyFlowMinuteTaskImpl(MessageContext messageContext, List<String> idList) {
            super(messageContext, idList);
        }

        @Override
        protected void save(ArrayNode arrayNode) {
            Iterator<JsonNode> iterator = arrayNode.getElements();
            JsonNode jsonNode;
            Map<String, Object> moneyFlowMap;
            String minute = TimeUtils.getDateFotmatHHmm();
            if (this.idList.size() == 1) {
                //只有一个id
                jsonNode = iterator.next();
                moneyFlowMap = StockUtils.createMoneyFlowMap(jsonNode);
                moneyFlowMap.put("id", this.idList.get(0));
                stockLocalService.updateStockMoneyFlow(moneyFlowMap);
                moneyFlowMap.put("minute", minute);
                stockLocalService.insertStockMoneyFlowMinute(moneyFlowMap);
            } else {
                //多个id
                List<Map<String, Object>> moneyFlowMapList = new ArrayList<Map<String, Object>>();
                while (iterator.hasNext()) {
                    jsonNode = iterator.next();
                    moneyFlowMap = StockUtils.createMoneyFlowMap(jsonNode);
                    moneyFlowMap.put("minute", minute);
                    moneyFlowMapList.add(moneyFlowMap);
                }
                stockLocalService.updateStockMoneyFlowList(moneyFlowMapList);
                //
                for (Map<String, Object> moneyFlowMinute : moneyFlowMapList) {
                    stockLocalService.insertStockMoneyFlowMinute(moneyFlowMinute);
                }
            }
        }
    }

    @Override
    public void execute(MessageContext messageContext) {
        String type = messageContext.getParameter("type");
        List<String> idList = this.stockLocalService.getStockIdAll();
        Task task;
        if (type.equals("day")) {
            task = new UpdateMoneyFlowHistoryTaskImpl(messageContext, idList);
        } else if (type.equals("minute")) {
            task = new UpdateMoneyFlowMinuteTaskImpl(messageContext, idList);
        } else {
            task = new UpdateMoneyFlowHistoryTaskImpl(messageContext, idList);
        }
        synchronized (this) {
            if (this.state.equals("stop")) {
                this.state = "running";
                this.taskExecutor.submit(task);
            }
        }
        messageContext.success();
    }
}
