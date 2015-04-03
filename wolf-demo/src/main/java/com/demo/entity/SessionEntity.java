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
        table = "session",
        counter = false
)
public class SessionEntity extends Entity {

    @ColumnConfig(columnType = ColumnType.KEY, desc = "id")
    private String id;
    //
    @ColumnConfig(desc = "帐号")
    private String userName;
    //
    @ColumnConfig(desc = "创建时间")
    private long createTime;

    public String getId() {
        return id;
    }
    
    public String getUserName() {
        return userName;
    }

    public long getCreateTime() {
        return createTime;
    }

    @Override
    public String getKeyValue() {
        return this.id;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>(4, 1);
        map.put("userName", this.userName);
        map.put("id", this.id);
        map.put("createTime", Long.toString(this.createTime));
        return map;
    }
}
