package com.wolf.framework.dao.elasticsearch;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.Entity;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class EsContextImpl<T extends Entity> implements EsContext<T>
{

    private static EsContextImpl INSTANCE = null;

    public static EsContextImpl getInstance(ApplicationContext applicationContext)
    {
        synchronized (EsContextImpl.class) {
            if (INSTANCE == null) {
                INSTANCE = new EsContextImpl(applicationContext);
            }
        }
        return INSTANCE;
    }

    private final Map<Class, EsEntityDao<T>> esEntityDaoMap = new HashMap(16, 1);

    private RestClient restClient = null;
    private final String host;
    private final int port = 9200;
    private final String database;
    private final String compileModel;

    private EsContextImpl(ApplicationContext applicationContext)
    {
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
        String httpCa = ApplicationContext.CONTEXT.getParameter(EsConfig.ELASTICSEARCH_HTTP_CERTIFICATE);
        if (httpCa == null) {
            httpCa = "";
        }
        //获取前缀
        String cm = ApplicationContext.CONTEXT.getParameter(FrameworkConfig.COMPILE_MODEL);
        this.compileModel = cm.toLowerCase();
        //
        try {
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            String storePassword = "";
            if (httpCa.isEmpty() == false) {
                File file = new File(httpCa);
                FileInputStream fileInputStream = new FileInputStream(file);
                trustStore.load(fileInputStream, storePassword.toCharArray());
            }
            //
            SSLContextBuilder sslBuilder = SSLContexts.custom()
                    .loadTrustMaterial(trustStore, null);
            final SSLContext sslContext = sslBuilder.build();
            //
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, passowrd));
            //
            restClient = RestClient.builder(
                    new HttpHost(host, port, "https")).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback()
                    {
                        @Override
                        public HttpAsyncClientBuilder customizeHttpClient(
                                HttpAsyncClientBuilder httpClientBuilder)
                        {
                            return httpClientBuilder.setSSLContext(sslContext).setSSLHostnameVerifier(new HostnameVerifier()
                            {
                                @Override
                                public boolean verify(String string, SSLSession ssls)
                                {
                                    //不校验ssl证书是否和hostName一致
                                    return true;
                                }
                            }).setDefaultCredentialsProvider(credentialsProvider);
                        }
                    }).build();
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | KeyManagementException ex) {
            System.err.println("elasticsearch添加节点异常");
        }
        EsResourceImpl esResourceImpl = new EsResourceImpl(this.restClient);
        applicationContext.addResource(esResourceImpl);
    }

    @Override
    public void putEsEntityDao(Class<T> clazz, EsEntityDao<T> esEntityDao, boolean multiCompile, String type)
    {
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
    public EsEntityDao<T> getEsEntityDao(Class<T> clazz)
    {
        return this.esEntityDaoMap.get(clazz);
    }

    @Override
    public Map<Class, EsEntityDao<T>> getEsEntityDao()
    {
        return Collections.unmodifiableMap(this.esEntityDaoMap);
    }

    @Override
    public String getIndex(boolean multiCompile, String type)
    {
        String index;
        if (multiCompile) {
            index = this.database + "_" + this.compileModel + "_" + type;
        } else {
            index = this.database + "_" + type;
        }
        return index;
    }

    @Override
    public RestClient getRestClient()
    {
        return this.restClient;
    }

}
