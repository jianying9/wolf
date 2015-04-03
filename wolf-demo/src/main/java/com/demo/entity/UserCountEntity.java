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
        table = "user_count",
        counter = true
)
public class UserCountEntity extends Entity {

    @ColumnConfig(columnType = ColumnType.KEY, desc = "帐号")
    private String userName;
    //
    @ColumnConfig(desc = "累计登录次数")
    private long login;

    @Override
    public String getKeyValue() {
        return this.userName;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>(2, 1);
        map.put("userName", this.userName);
        map.put("login", Long.toString(this.login));
        return map;
    }
}
