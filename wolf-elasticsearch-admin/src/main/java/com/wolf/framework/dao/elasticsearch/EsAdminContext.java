package com.wolf.framework.dao.elasticsearch;

import com.wolf.framework.dao.Entity;
import java.util.Map;
import java.util.Set;
import org.elasticsearch.client.transport.TransportClient;

/**
 *
 * @author jianying9
 * @param <T>
 */
public interface EsAdminContext<T extends Entity>
{

    public EsEntityDao<T> getEsEntityDao(final Class<T> clazz);

    public Map<Class, EsEntityDao<T>> getEsEntityDao();
    
    public String check(Set<String> tableNameSet);
    
    public TransportClient getTransportClient();

}
