package com.wolf.framework.dao.redis;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.ColumnType;
import com.wolf.framework.dao.reids.annotation.ColumnConfig;
import com.wolf.framework.dao.reids.annotation.RDaoConfig;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * 测试
 *
 * @author jianying9
 */
@RDaoConfig(
        tableName = "TestRedis")
public final class TestRedisEntity extends Entity {

    @ColumnConfig(columnType = ColumnType.KEY, desc = "id")
    private String id;

    @ColumnConfig(columnType = ColumnType.INDEX, desc = "type")
    private String type;

    @ColumnConfig(desc = "name")
    private String name;
    //

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getKeyValue() {
        return this.id;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>(4, 1);
        map.put("id", this.id);
        map.put("type", this.type);
        map.put("name", this.name);
        return map;
    }
}
