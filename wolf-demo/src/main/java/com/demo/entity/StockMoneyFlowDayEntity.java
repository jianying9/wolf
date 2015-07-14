package com.demo.entity;

import com.wolf.framework.dao.ColumnType;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.cassandra.annotation.CDaoConfig;
import com.wolf.framework.dao.cassandra.annotation.ColumnConfig;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 */
@CDaoConfig(
        keyspace = "test",
        table = "stock_money_flow_day",
        counter = false
)
public class StockMoneyFlowDayEntity extends Entity {
    
    @ColumnConfig(columnType = ColumnType.KEY, desc = "股票id")
    private String id;
    //
    @ColumnConfig(columnType = ColumnType.KEY, desc = "时间:yyyy-mm-dd")
    private String day;
    //
    @ColumnConfig(desc = "名称")
    private String name;
    //
    @ColumnConfig(desc = "超大资金流入")
    private double superIn;
    //
    @ColumnConfig(desc = "大资金流出")
    private double superOut;
    //
    @ColumnConfig(desc = "大资金流入")
    private double bigIn;
    //
    @ColumnConfig(desc = "资金流出")
    private double bigOut;
    //
    @ColumnConfig(desc = "中资金流入")
    private double middleIn;
    //
    @ColumnConfig(desc = "中资金流出")
    private double middleOut;
    //
    @ColumnConfig(desc = "小资金流入")
    private double smallIn;
    //
    @ColumnConfig(desc = "小资金流出")
    private double smallOut;
    //
    @ColumnConfig(desc = "当前价格")
    private double price;
    //
    @ColumnConfig(desc = "变化比率")
    private double changeRatio;

    public String getId() {
        return id;
    }
    
    public String getDay() {
        return day;
    }
    
    public String getName() {
        return name;
    }

    public double getSuperIn() {
        return superIn;
    }

    public double getSuperOut() {
        return superOut;
    }

    public double getBigIn() {
        return bigIn;
    }

    public double getBigOut() {
        return bigOut;
    }

    public double getMiddleIn() {
        return middleIn;
    }

    public double getMiddleOut() {
        return middleOut;
    }

    public double getSmallIn() {
        return smallIn;
    }

    public double getSmallOut() {
        return smallOut;
    }

    public double getPrice() {
        return price;
    }

    public double getChangeRatio() {
        return changeRatio;
    }
    
    @Override
    public String getKeyValue() {
        return this.id + "_" + this.day;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>(16, 1);
        map.put("day", this.day);
        map.put("name", this.name);
        map.put("id", this.id);
        map.put("superIn", Double.toString(this.superIn));
        map.put("superOut", Double.toString(this.superOut));
        map.put("bigIn", Double.toString(this.bigIn));
        map.put("bigOut", Double.toString(this.bigOut));
        map.put("middleIn", Double.toString(this.middleIn));
        map.put("middleOut", Double.toString(this.middleOut));
        map.put("smallIn", Double.toString(this.smallIn));
        map.put("smallOut", Double.toString(this.smallOut));
        map.put("price", Double.toString(this.price));
        map.put("changeRatio", Double.toString(this.changeRatio));
        return map;
    }
}
