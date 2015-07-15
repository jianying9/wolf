package com.demo.utils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.JsonNode;

/**
 *
 * @author jianying9
 */
public class StockUtils {
    
    private final static DecimalFormat DF_DOUBLE = new DecimalFormat("#.000");
    
    public static Map<String, Object> createMoneyFlowMap(JsonNode jsonNode) {
        Map<String, Object> moneyFlowMap = new HashMap<String, Object>(16, 1);
        String value;
        //
        if (jsonNode.get("symbol") != null) {
            value = jsonNode.get("symbol").getTextValue();
            moneyFlowMap.put("id", value.substring(2));
        }
        //
        value = jsonNode.get("name").getTextValue();
        moneyFlowMap.put("name", value);
        //
        value = jsonNode.get("r0_in").getTextValue();
        moneyFlowMap.put("superIn", Double.parseDouble(value));
        //
        value = jsonNode.get("r0_out").getTextValue();
        moneyFlowMap.put("superOut", Double.parseDouble(value));
        //
        value = jsonNode.get("r1_in").getTextValue();
        moneyFlowMap.put("bigIn", Double.parseDouble(value));
        //
        value = jsonNode.get("r1_out").getTextValue();
        moneyFlowMap.put("bigOut", Double.parseDouble(value));
        //
        value = jsonNode.get("r2_in").getTextValue();
        moneyFlowMap.put("middleIn", Double.parseDouble(value));
        //
        value = jsonNode.get("r2_out").getTextValue();
        moneyFlowMap.put("middleOut", Double.parseDouble(value));
        //
        value = jsonNode.get("r3_in").getTextValue();
        moneyFlowMap.put("smallIn", Double.parseDouble(value));
        //
        value = jsonNode.get("r3_out").getTextValue();
        moneyFlowMap.put("smallOut", Double.parseDouble(value));
        //
        value = jsonNode.get("trade").getTextValue();
        moneyFlowMap.put("price", Double.parseDouble(value));
        //
        value = jsonNode.get("changeratio").getTextValue();
        moneyFlowMap.put("changeRatio", Double.parseDouble(value));
        //
        moneyFlowMap.put("lastUpdateTime", System.currentTimeMillis());
        moneyFlowMap.put("sample", "test");
        return moneyFlowMap;
    }
    
    public static String formatDouble(double money) {
        return DF_DOUBLE.format(money);
    }
}
