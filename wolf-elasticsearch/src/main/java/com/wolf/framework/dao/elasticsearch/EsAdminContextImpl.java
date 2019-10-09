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
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;

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
        //
        String user = ApplicationContext.CONTEXT.getParameter(EsConfig.ELASTICSEARCH_USER);
        if (user == null) {
            user = "";
        }
        String passowrd = ApplicationContext.CONTEXT.getParameter(EsConfig.ELASTICSEARCH_PASSWORD);
        if (passowrd == null) {
            passowrd = "";
        }
        //获取前缀
        String cm = ApplicationContext.CONTEXT.getParameter(FrameworkConfig.COMPILE_MODEL);
        this.compileModel = cm.toLowerCase();
        //
        Settings.Builder builder = Settings.builder();
        if (clusterName.isEmpty() == false) {
            builder.put("cluster.name", clusterName);
        }
        //
        boolean security = false;
        if (user.isEmpty() == false && passowrd.isEmpty() == false) {
            //有配置账号密码,则需要开启ssl模式访问
            String sslKey = ApplicationContext.CONTEXT.getParameter(EsConfig.ELASTICSEARCH_SSL_KEY);
            if (sslKey == null) {
                sslKey = "";
            }
            String sslCer = ApplicationContext.CONTEXT.getParameter(EsConfig.ELASTICSEARCH_SSL_CERTIFICATE);
            if (sslCer == null) {
                sslCer = "";
            }
            String sslCa = ApplicationContext.CONTEXT.getParameter(EsConfig.ELASTICSEARCH_SSL_CERTIFICATE_AUTHORITIES);
            if (sslCa == null) {
                sslCa = "";
            }
            builder.put("xpack.security.user", user + ":" + passowrd)
                    .put("xpack.ssl.key", sslKey)
                    .put("xpack.ssl.certificate", sslCer)
                    .put("xpack.ssl.certificate_authorities", sslCa)
                    .put("xpack.ssl.verification_mode", "none")
                    .put("xpack.security.transport.ssl.enabled", true);
            security = true;
        }
        Settings settings = builder.build();
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        if (security) {
            this.transportClient = new PreBuiltXPackTransportClient(settings);
        } else {
            this.transportClient = new PreBuiltTransportClient(settings);
        }
        try {
            this.transportClient.addTransportAddress(new TransportAddress(InetAddress.getByName(this.host), this.port));
        } catch (UnknownHostException ex) {
            System.err.println("elasticsearch添加节点异常");
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
