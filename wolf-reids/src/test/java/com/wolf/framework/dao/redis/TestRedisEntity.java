package com.wolf.framework.dao.redis;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.annotation.ColumnType;
import com.wolf.framework.dao.reids.annotation.RColumnConfig;
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

    @RColumnConfig(columnType = ColumnType.KEY, desc = "id")
    private String id;

    @RColumnConfig(columnType = ColumnType.INDEX, desc = "type")
    private String type;

    @RColumnConfig(desc = "name")
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

    @Override
    protected void parseMap(Map<String, String> entityMap) {
        this.id = entityMap.get("id");
        this.type = entityMap.get("type");
        this.name = entityMap.get("name");
    }
}
