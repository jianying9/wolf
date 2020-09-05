package com.wolf.framework.dao.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.Entity;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class EsAdminContextImpl<T extends Entity> implements EsAdminContext<T>
{

    private final EsContext esContext;

    private final TransportClient transportClient;
    private final String host;
    private final int port = 9300;

    public EsAdminContextImpl(ApplicationContext applicationContext)
    {
        //获取elasticsearch host
        this.esContext = EsContextImpl.getInstance(applicationContext);
        String searchHost = ApplicationContext.CONTEXT.getParameter(EsConfig.ELASTICSEARCH_HOST);
        this.host = searchHost;
        //
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
    }

    @Override
    public EsEntityDao<T> getEsEntityDao(Class<T> clazz)
    {
        return this.esContext.getEsEntityDao(clazz);
    }

    @Override
    public Map<Class, EsEntityDao<T>> getEsEntityDao()
    {
        return this.esContext.getEsEntityDao();
    }

    private String checkEsEntityDao(EsEntityDao<T> esEntityDao)
    {
        String result = "";
        //创建index
        String index = esEntityDao.getIndex();
        String type = esEntityDao.getType();
        IndicesExistsResponse indicesExistsResponse = this.transportClient.admin().indices().prepareExists(index).get();
        if (indicesExistsResponse.isExists() == false) {
            System.out.println("创建index:" + index);
            transportClient.admin().indices().prepareCreate(index).get();
        }
        //
        Map<String, Object> typeMap = new HashMap();
        //构造属性
        Map<String, Object> propertyMap = new HashMap();
        EsColumnHandler keyHandler = esEntityDao.getKeyHandler();
        Map<String, Object> keyMap = esEntityDao.getFieldMap(keyHandler);
        propertyMap.put(keyHandler.getColumnName(), keyMap);
        //
        Map<String, Object> fieldMap;
        for (EsColumnHandler esColumnHandler : esEntityDao.getColumnHandlerList()) {
            fieldMap = esEntityDao.getFieldMap(esColumnHandler);
            propertyMap.put(esColumnHandler.getColumnName(), fieldMap);
        }
        typeMap.put("properties", propertyMap);
        //关闭_all
        Map<String, Object> allMap = new HashMap(2, 1);
        allMap.put("enabled", false);
        typeMap.put("_all", allMap);
        //
        Map<String, Object> indexMap = new HashMap();
        indexMap.put(type, typeMap);
        //
        ObjectMapper mapper = new ObjectMapper();
        String json = "{}";
        try {
            json = mapper.writeValueAsString(indexMap);
        } catch (IOException ex) {
        }
        System.out.println("更新index:" + index);
        System.out.println(json);
        AcknowledgedResponse response = this.transportClient.admin().indices().preparePutMapping(index)
                .setType(type)
                .setSource(json, XContentType.JSON)
                .get();
        return result;
    }

    @Override
    public String check(Set<String> tableNameSet)
    {
        String result = "";
        Map<String, EsEntityDao> cEntityDaomap = this.esContext.getEsEntityDao();
        for (EsEntityDao esEntityDao : cEntityDaomap.values()) {
            for (String tableName : tableNameSet) {
                if (esEntityDao.getIndex().endsWith(tableName)) {
                    result = this.checkEsEntityDao(esEntityDao);
                    break;
                } 
            }
        }
        return result;
    }

    public TransportClient getTransportClient()
    {
        return transportClient;
    }

}
