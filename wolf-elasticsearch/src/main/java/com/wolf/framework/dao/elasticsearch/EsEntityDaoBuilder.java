package com.wolf.framework.dao.elasticsearch;

import com.wolf.framework.dao.Entity;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.client.transport.TransportClient;

/**
 * 实体数据访问对象创建类
 *
 * @author jianying9
 * @param <T>
 */
public final class EsEntityDaoBuilder<T extends Entity> {

    //表空间
    private final String index;
    //table name
    private final String type;
    //key
    private final EsColumnHandler keyHandler;
    //version
    private final EsColumnHandler versionHandler;
    //column
    private final List<EsColumnHandler> columnHandlerList;
    //实体class
    private final Class<T> clazz;

    private final EsAdminContext<T> esAdminContext;

    //
    public EsEntityDaoBuilder(
            String tableName,
            String type,
            EsColumnHandler keyHandler,
            List<EsColumnHandler> columnHandlerList,
            EsColumnHandler versionHandler,
            Class<T> clazz,
            EsAdminContext<T> esAdminContext
    ) {
        this.type = type;
        this.keyHandler = keyHandler;
        if (columnHandlerList == null) {
            this.columnHandlerList = new ArrayList(0);
        } else {
            this.columnHandlerList = columnHandlerList;
        }
        this.versionHandler = versionHandler;
        this.clazz = clazz;
        this.esAdminContext = esAdminContext;
        this.index = esAdminContext.getIndex(tableName);
    }

    public EsEntityDao<T> build() {
        if (this.index.isEmpty()) {
            throw new RuntimeException("Error when building EsEntityDao. Cause: index is empty");
        }
        if (this.type.isEmpty()) {
            throw new RuntimeException("Error when building EsEntityDao. Cause: type is empty");
        }
        if (this.clazz == null) {
            throw new RuntimeException("Error when building EsEntityDao. Cause: clazz is null");
        }
        if (this.keyHandler == null) {
            throw new RuntimeException("Error when building EsEntityDao. Cause: key is null");
        }
        TransportClient transportClient = this.esAdminContext.getTransportClient();
        //
        EsEntityDao<T> entityDao;
        if (this.versionHandler == null) {
            entityDao = new EsEntityDaoImpl(
                    transportClient,
                    this.index,
                    this.type,
                    this.keyHandler,
                    this.columnHandlerList,
                    this.clazz
            );
        } else {
            entityDao = new EsEntityDaoVersionImpl(
                    transportClient,
                    this.index,
                    this.type,
                    this.keyHandler,
                    this.columnHandlerList,
                    this.versionHandler,
                    this.clazz
            );
        }
        return entityDao;
    }
}
