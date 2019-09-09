package com.wolf.framework.dao.elasticsearch;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.Entity;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class EsAdminContextImpl<T extends Entity> implements EsAdminContext<T> {

    private static EsAdminContextImpl INSTANCE = null;

    public static EsAdminContextImpl getInstance(ApplicationContext applicationContext) {
        synchronized (EsAdminContextImpl.class) {
            if (INSTANCE == null) {
                INSTANCE = new EsAdminContextImpl(applicationContext);
            }
        }
        return INSTANCE;
    }

    private final Map<Class, EsEntityDao<T>> esEntityDaoMap = new HashMap(16, 1);

    private TransportClient transportClient = null;
    private final String host;
    private final int port = 9300;
    private final String database;
    private final String compileModel;

    private EsAdminContextImpl(ApplicationContext applicationContext) {
        //获取elasticsearch host
        String searchHost = ApplicationContext.CONTEXT.getParameter(EsConfig.ELASTICSEARCH_HOST);
        this.host = searchHost;
        //
        String db = ApplicationContext.CONTEXT.getParameter(EsConfig.ELASTICSEARCH_DATABASE);
        if (db == null) {
            db = "";
        }
        this.database = db;
        String clusterName = ApplicationContext.CONTEXT.getParameter(EsConfig.ELASTICSEARCH_CLUSTER_NAME);
        if (clusterName == null) {
            clusterName = "";
        }
        //获取前缀
        String cm = ApplicationContext.CONTEXT.getParameter(FrameworkConfig.COMPILE_MODEL);
        this.compileModel = cm.toLowerCase();
        //
        Settings settings = Settings.EMPTY;
        if (clusterName.isEmpty() == false) {
            settings = Settings.builder().put("cluster.name", clusterName).build();
        }
        try {
            System.setProperty("es.set.netty.runtime.available.processors", "false");
            this.transportClient = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(this.host), this.port));
        } catch (UnknownHostException ex) {
            System.err.println("elasticsearch 初始化异常");
        }
        EsResourceImpl esResourceImpl = new EsResourceImpl(this.transportClient);
        applicationContext.addResource(esResourceImpl);
    }

    @Override
    public void putEsEntityDao(Class<T> clazz, EsEntityDao<T> esEntityDao, boolean multiCompile, String type) {
        if (this.esEntityDaoMap.containsKey(clazz)) {
            StringBuilder errBuilder = new StringBuilder(1024);
            String index = this.getIndex(multiCompile, type);
            errBuilder.append("Error putting EsEntityDao. Cause: index duplicated : ")
                    .append('(').append(index).append(")\n");
            throw new RuntimeException(errBuilder.toString());
        }
        this.esEntityDaoMap.put(clazz, esEntityDao);
    }

    @Override
    public EsEntityDao<T> getEsEntityDao(Class<T> clazz) {
        return this.esEntityDaoMap.get(clazz);
    }

    @Override
    public TransportClient getTransportClient() {
        return transportClient;
    }

    @Override
    public Map<Class, EsEntityDao<T>> getEsEntityDao() {
        return Collections.unmodifiableMap(this.esEntityDaoMap);
    }

    @Override
    public String getIndex(boolean multiCompile, String type) {
        String index;
        if (multiCompile) {
            index = this.database + "_" + this.compileModel + "_" + type;
        } else {
            index = this.database + "_" + type;
        }
        return index;
    }

}
