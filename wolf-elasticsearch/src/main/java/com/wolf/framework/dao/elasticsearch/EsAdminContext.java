package com.wolf.framework.dao.elasticsearch;

import com.wolf.framework.dao.Entity;
import java.util.Map;
import org.elasticsearch.client.transport.TransportClient;

/**
 *
 * @author jianying9
 * @param <T>
 */
public interface EsAdminContext<T extends Entity> {

    public void putEsEntityDao(final Class<T> clazz, final EsEntityDao<T> esEntityDao, String table);

    public EsEntityDao<T> getEsEntityDao(final Class<T> clazz);

    public Map<Class, EsEntityDao<T>> getEsEntityDao();

    public TransportClient getTransportClient();
    
    public String getIndex(String type);

}
