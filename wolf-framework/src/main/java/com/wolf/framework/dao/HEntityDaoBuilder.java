package com.wolf.framework.dao;

import com.wolf.framework.dao.parser.HColumnHandler;
import com.wolf.framework.hbase.HTableHandler;
import java.util.List;

/**
 * 实体数据访问对象创建类
 *
 * @author aladdin
 */
public final class HEntityDaoBuilder<T extends Entity> {

    //table name
    private final String tableName;
    //key
    private final HColumnHandler keyHandler;
    //column
    private final List<HColumnHandler> columnHandlerList;
    //实体class
    private final Class<T> clazz;
    //是否使用缓存
    //
    private final HEntityDaoContext<T> entityDaoContext;

    public HEntityDaoBuilder(String tableName, HColumnHandler keyHandler, List<HColumnHandler> columnHandlerList, Class<T> clazz, HEntityDaoContext<T> entityDaoContext) {
        this.tableName = "h_".concat(tableName);
        this.keyHandler = keyHandler;
        this.columnHandlerList = columnHandlerList;
        this.clazz = clazz;
        this.entityDaoContext = entityDaoContext;
    }

    public HEntityDao<T> build() {
        if (this.tableName == null || this.tableName.equals("h_")) {
            throw new RuntimeException("There was an error building H entityDao. Cause: tableName is null or empty");
        }
        if (this.clazz == null) {
            throw new RuntimeException("There was an error building H entityDao. Cause: clazz is null");
        }
        if (this.keyHandler == null) {
            throw new RuntimeException("There was an error building H entityDao. Cause: key is null");
        }
        if (this.columnHandlerList == null || this.columnHandlerList.isEmpty()) {
            throw new RuntimeException("There was an error building H entityDao. Cause: columns null or empty");
        }
        final HTableHandler hTableHandler = this.entityDaoContext.getHTableHandler();
        //检测表是否存在
        boolean exists = hTableHandler.isTableExists(this.tableName);
        if(exists == false) {
            //建表
            hTableHandler.createTable(this.tableName);
        }
        HEntityDao<T> entityDao = new HEntityDaoImpl(
                this.tableName,
                hTableHandler,
                this.clazz,
                this.keyHandler,
                this.columnHandlerList);
        return entityDao;
    }
}
