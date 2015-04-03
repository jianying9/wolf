package com.demo.localservice;

import com.demo.entity.TableCountEntity;
import com.wolf.framework.dao.cassandra.CEntityDao;
import com.wolf.framework.dao.cassandra.annotation.InjectCDao;
import com.wolf.framework.local.LocalServiceConfig;

/**
 *
 * @author jianying9
 */
@LocalServiceConfig(description = "计数管理内部接口")
public class TableCountLocalServiceImpl implements TableCountLocalService {

    @InjectCDao(clazz = TableCountEntity.class)
    private CEntityDao<TableCountEntity> tableCountEntityDao;
    
    @Override
    public void init() {
    }

    @Override
    public long nextKey(String tableName, long num) {
        return this.tableCountEntityDao.increase("count", num, tableName);
    }

    @Override
    public boolean exist(String tableName) {
        return this.tableCountEntityDao.exist(tableName);
    }
}
