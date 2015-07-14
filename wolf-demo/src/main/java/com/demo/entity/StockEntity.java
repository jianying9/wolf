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
        table = "stock",
        counter = false
)
public class StockEntity extends Entity {
    
    @ColumnConfig(columnType = ColumnType.KEY, desc = "取样类型")
    private String sample;

    @ColumnConfig(columnType = ColumnType.KEY, desc = "id")
    private String id;
    //
    @ColumnConfig(columnType = ColumnType.INDEX, desc = "名称")
    private String name;
    //
    @ColumnConfig(desc = "创建时间")
    private long createTime;

    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public long getCreateTime() {
        return createTime;
    }

    @Override
    public String getKeyValue() {
        return this.sample + "_" + this.id;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>(4, 1);
        map.put("sample", this.sample);
        map.put("name", this.name);
        map.put("id", this.id);
        map.put("createTime", Long.toString(this.createTime));
        return map;
    }
}
