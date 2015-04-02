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
        table = "user",
        counter = false,
        sets = {"tag", "friend"},
        lists = {"message"},
        maps = {"otherId"}
)
public class UserEntity extends Entity {

    @ColumnConfig(columnType = ColumnType.KEY, desc = "用户名称")
    private String userName;
    //
    @ColumnConfig(desc = "密码")
    private String password;
    //
    @ColumnConfig(desc = "创建时间")
    private long createTime;

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public long getCreateTime() {
        return createTime;
    }

    @Override
    public String getKeyValue() {
        return this.userName;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>(4, 1);
        map.put("userName", this.userName);
        map.put("password", this.password);
        map.put("createTime", Long.toString(this.createTime));
        return map;
    }
}
