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
    //column
    private final List<EsColumnHandler> columnHandlerList;
    //实体class
    private final Class<T> clazz;

    private final EsAdminContext<T> esAdminContext;

    //
    public EsEntityDaoBuilder(
            String tableName,
            EsColumnHandler keyHandler,
            List<EsColumnHandler> columnHandlerList,
            Class<T> clazz,
            EsAdminContext<T> esAdminContext
    ) {
        this.type = tableName;
        this.keyHandler = keyHandler;
        if (columnHandlerList == null) {
            this.columnHandlerList = new ArrayList(0);
        } else {
            this.columnHandlerList = columnHandlerList;
        }
        this.clazz = clazz;
        this.esAdminContext = esAdminContext;
        this.index = esAdminContext.getIndex(type);
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
        EsEntityDaoImpl<T> esEntityDaoImpl = new EsEntityDaoImpl(
                transportClient,
                this.index,
                this.type,
                this.keyHandler,
                this.columnHandlerList,
                this.clazz
        );
        EsEntityDao<T> entityDao = esEntityDaoImpl;
        return entityDao;
    }
}
