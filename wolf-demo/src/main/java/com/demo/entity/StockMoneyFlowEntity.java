package com.demo.entity;

import com.demo.utils.StockUtils;
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
        table = "stock_money_flow",
        counter = false
)
public class StockMoneyFlowEntity extends Entity {
    
    @ColumnConfig(columnType = ColumnType.KEY, desc = "取样类型")
    private String sample;
    
    @ColumnConfig(columnType = ColumnType.KEY, desc = "排序")
    private long sort;
    //
    @ColumnConfig(desc = "评分")
    private double score;
    //
    @ColumnConfig(desc = "股票id")
    private String id;
    //
    @ColumnConfig(desc = "名称")
    private String name;
    //
    @ColumnConfig(desc = "超大资金流入")
    private double superIn;
    //
    @ColumnConfig(desc = "超大资金流出")
    private double superOut;
    //
    @ColumnConfig(desc = "大资金流入")
    private double bigIn;
    //
    @ColumnConfig(desc = "大资金流出")
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
    //
    @ColumnConfig(desc = "最后更新时间")
    private long lastUpdateTime;

    public String getSample() {
        return sample;
    }

    public long getSort() {
        return sort;
    }

    public double getScore() {
        return score;
    }
    
    public String getId() {
        return id;
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

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    @Override
    public String getKeyValue() {
        return this.sample + "_" + this.id;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>(16, 1);
        map.put("sample", this.sample);
        map.put("sort", Long.toString(this.sort));
        map.put("score", StockUtils.formatDouble(this.score));
        map.put("sample", this.sample);
        map.put("name", this.name);
        map.put("id", this.id);
        map.put("superIn", StockUtils.formatDouble(this.superIn));
        map.put("superOut", StockUtils.formatDouble(this.superOut));
        map.put("bigIn", StockUtils.formatDouble(this.bigIn));
        map.put("bigOut", StockUtils.formatDouble(this.bigOut));
        map.put("middleIn", StockUtils.formatDouble(this.middleIn));
        map.put("middleOut", StockUtils.formatDouble(this.middleOut));
        map.put("smallIn", StockUtils.formatDouble(this.smallIn));
        map.put("smallOut", StockUtils.formatDouble(this.smallOut));
        map.put("price", StockUtils.formatDouble(this.price));
        map.put("changeRatio", StockUtils.formatDouble(this.changeRatio));
        map.put("lastUpdateTime", Long.toString(this.lastUpdateTime));
        return map;
    }
}
