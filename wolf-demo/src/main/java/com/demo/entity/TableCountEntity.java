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
        table = "table_count",
        counter = true
)
public class TableCountEntity extends Entity {

    @ColumnConfig(columnType = ColumnType.KEY, desc = "表名")
    private String tableName;
    //
    @ColumnConfig(desc = "计数")
    private long count;

    @Override
    public String getKeyValue() {
        return this.tableName;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>(2, 1);
        map.put("tableName", this.tableName);
        map.put("count", Long.toString(this.count));
        return map;
    }
}
