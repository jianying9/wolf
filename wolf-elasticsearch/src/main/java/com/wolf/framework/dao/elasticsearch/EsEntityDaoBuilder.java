package com.wolf.framework.dao.elasticsearch;

import com.wolf.framework.dao.Entity;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.client.RestClient;

/**
 * 实体数据访问对象创建类
 *
 * @author jianying9
 * @param <T>
 */
public final class EsEntityDaoBuilder<T extends Entity>
{

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

    private final EsContext<T> esAdminContext;

    //
    public EsEntityDaoBuilder(
            String tableName,
            String type,
            boolean multiCompile,
            EsColumnHandler keyHandler,
            List<EsColumnHandler> columnHandlerList,
            Class<T> clazz,
            EsContext<T> esAdminContext
    )
    {
        this.type = type;
        this.keyHandler = keyHandler;
        if (columnHandlerList == null) {
            this.columnHandlerList = new ArrayList(0);
        } else {
            this.columnHandlerList = columnHandlerList;
        }
        this.clazz = clazz;
        this.esAdminContext = esAdminContext;
        this.index = esAdminContext.getIndex(multiCompile, tableName);
    }

    public EsEntityDao<T> build()
    {
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
        RestClient restClient = this.esAdminContext.getRestClient();
        //
        EsEntityDao<T> entityDao = new EsEntityDaoImpl(
                restClient,
                this.index,
                this.type,
                this.keyHandler,
                this.columnHandlerList,
                this.clazz
        );;

        return entityDao;
    }
}
