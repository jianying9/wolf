package com.demo.localservice;

import com.demo.entity.StockEntity;
import com.demo.entity.StockMoneyFlowEntity;
import com.wolf.framework.dao.cassandra.CEntityDao;
import com.wolf.framework.dao.cassandra.annotation.InjectCDao;
import com.wolf.framework.local.LocalServiceConfig;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 *
 * @author jianying9
 */
@LocalServiceConfig(description = "stock内部接口")
public class StockLocalServiceImpl implements StockLocalService {

    public final String URL_SINA_STOCK = "http://hq.sinajs.cn/list=${list}";

    public final String URL_SINA_STOCK_MONEY_FLOW = "http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/MoneyFlow.ssi_ssfx_flzjtj?daima=${list}";

    @InjectCDao(clazz = StockEntity.class)
    private CEntityDao<StockEntity> stockEntityDao;
    //

    @InjectCDao(clazz = StockMoneyFlowEntity.class)
    private CEntityDao<StockMoneyFlowEntity> stockMoneyFlowEntityDao;
    //
    private volatile HttpClient httpClient;

    @Override
    public void init() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        PoolingHttpClientConnectionManager pm = new PoolingHttpClientConnectionManager();
        pm.setMaxTotal(10);
        pm.setDefaultConnectionConfig(ConnectionConfig.DEFAULT);
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        requestConfigBuilder.setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000);
        RequestConfig requestConfig = requestConfigBuilder.build();
        httpClientBuilder.setConnectionManager(pm).setDefaultRequestConfig(requestConfig);
        this.httpClient = httpClientBuilder.build();
    }

    @Override
    public String getSinaStockInfo(String code) {
        String info = "";
        String url = this.URL_SINA_STOCK.replace("${list}", code);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        HttpGet httpGet = new HttpGet(url);
        try {
            info = this.httpClient.execute(httpGet, responseHandler);
        } catch (IOException ex) {
        }
        return info;
    }

    @Override
    public String getSinaMoneyFlowInfo(String code) {
        String info = "";
        String url = this.URL_SINA_STOCK_MONEY_FLOW.replace("${list}", code);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        HttpGet httpGet = new HttpGet(url);
        try {
            info = this.httpClient.execute(httpGet, responseHandler);
        } catch (IOException ex) {
        }
        return info;
    }

    @Override
    public String getSinaStockCode(String id) {
        String result = "";
        String prefix = id.substring(0, 3);
        if (prefix.equals("600") || prefix.equals("601") || prefix.equals("603")) {
            result = "sh" + id;
        } else if (prefix.equals("000") || prefix.equals("002") || prefix.equals("300")) {
            result = "sz" + id;
        }
        return result;
    }

    @Override
    public String getSinaStockCodes(String[] idArray) {
        StringBuilder sb = new StringBuilder();
        if (idArray.length > 0) {
            for (String id : idArray) {
                sb.append(this.getSinaStockCode(id)).append(',');
            }
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    @Override
    public void insertStock(String id, String name) {
        //新增股票
        Map<String, Object> insertMap = new HashMap<String, Object>(4, 1);
        insertMap.put("id", id);
        insertMap.put("name", name);
        insertMap.put("createTime", System.currentTimeMillis());
        this.stockEntityDao.insert(insertMap);
        //新增股票资金流向
        insertMap.put("superIn", 0.0);
        insertMap.put("superOut", 0.0);
        insertMap.put("bigIn", 0.0);
        insertMap.put("bigOut", 0.0);
        insertMap.put("middleIn", 0.0);
        insertMap.put("middleOut", 0.0);
        insertMap.put("smallIn", 0.0);
        insertMap.put("smallOut", 0.0);
        insertMap.put("price", 0.0);
        insertMap.put("changeRatio", 0.0);
        insertMap.put("lastUpdateTime", System.currentTimeMillis());
        insertMap.put("sample", "test");
        this.stockMoneyFlowEntityDao.insert(insertMap);
    }

    @Override
    public void updateStockMoneyFlow(Map<String, Object> updateMap) {
        updateMap.put("lastUpdateTime", Long.toString(System.currentTimeMillis()));
        this.stockMoneyFlowEntityDao.update(updateMap);
    }

    @Override
    public void updateStockMoneyFlowList(List<Map<String, Object>> updateMapList) {
        this.stockMoneyFlowEntityDao.batchUpdate(updateMapList);
    }
}
