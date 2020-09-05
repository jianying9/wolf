package com.wolf.framework.dao.elasticsearch;

import com.wolf.framework.dao.Entity;
import java.util.Map;
import org.elasticsearch.client.RestClient;

/**
 *
 * @author jianying9
 * @param <T>
 */
public interface EsContext<T extends Entity>
{

    public void putEsEntityDao(final Class<T> clazz, final EsEntityDao<T> esEntityDao, boolean multiCompile, String table);

    public EsEntityDao<T> getEsEntityDao(final Class<T> clazz);

    public Map<Class, EsEntityDao<T>> getEsEntityDao();

    public RestClient getRestClient();

    public String getIndex(boolean multiCompile, String type);

}
