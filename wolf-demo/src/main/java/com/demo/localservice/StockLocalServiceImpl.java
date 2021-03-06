package com.demo.localservice;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.demo.entity.StockEntity;
import com.demo.entity.StockMoneyFlowEntity;
import com.demo.entity.StockMoneyFlowDayEntity;
import com.demo.entity.StockMoneyFlowMinuteEntity;
import com.demo.utils.StockUtils;
import com.wolf.framework.dao.cassandra.CEntityDao;
import com.wolf.framework.dao.cassandra.annotation.InjectCDao;
import com.wolf.framework.local.LocalServiceConfig;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private final String CqlInquireStockIdAll = "select id from test.stock;";

    private final String CqlTruncateStockMoneyFlowMinute = "truncate test.stock_money_flow_minute;";
    
    private final String CqlQueryTopStock = "select * from test.stock_money_flow where sample='test' and sort < ?;";

    public final String URL_SINA_STOCK = "http://hq.sinajs.cn/list=${list}";

    public final String URL_SINA_STOCK_MONEY_FLOW = "http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/MoneyFlow.ssi_ssfx_flzjtj?daima=${list}";

    @InjectCDao(clazz = StockEntity.class)
    private CEntityDao<StockEntity> stockEntityDao;
    //
    @InjectCDao(clazz = StockMoneyFlowEntity.class)
    private CEntityDao<StockMoneyFlowEntity> stockMoneyFlowEntityDao;
    //
    @InjectCDao(clazz = StockMoneyFlowMinuteEntity.class)
    private CEntityDao<StockMoneyFlowMinuteEntity> stockMoneyFlowMinuteEntityDao;
    //
    @InjectCDao(clazz = StockMoneyFlowDayEntity.class)
    private CEntityDao<StockMoneyFlowDayEntity> stockMoneyFlowHistoryEntityDao;
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
    public String getSinaStockCodes(String... idArray) {
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
        insertMap.put("sample", "test");
        insertMap.put("id", id);
        insertMap.put("name", name);
        insertMap.put("createTime", System.currentTimeMillis());
        this.stockEntityDao.insert(insertMap);
    }

    @Override
    public List<StockMoneyFlowEntity> queryTopStock(long topNum) {
        if(topNum > 100) {
            topNum = 100;
        }
        return this.stockMoneyFlowEntityDao.query(this.CqlQueryTopStock, topNum);
    }

    private class StockScoreComparator implements Comparator<Map<String, Object>> {

        @Override
        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
            int result = 1;
            double s1 = (Double) o1.get("score");
            double s2 = (Double) o2.get("score");
            if (s1 > s2) {
                result = -1;
            }
            return result;
        }
    }

    private final StockScoreComparator stockScoreComparator = new StockScoreComparator();

    @Override
    public void insertStockMoneyFlowList(List<Map<String, Object>> updateMapList) {
        //计算得分
        double score;
        double superIn;
        double superOut;
        double changeRatio;
        double s1;
        double s2;
        double s3;
        for (Map<String, Object> updateMap : updateMapList) {
            superIn = (Double) updateMap.get("superIn");
            superOut = (Double) updateMap.get("superOut");
            changeRatio = (Double) updateMap.get("changeRatio");
            s1 = (superIn - superOut) / 10000000;
            s2 = superOut / 10000000 * 2;
            
            if (superIn - superOut > 0) {
                s3 = (0.1 - changeRatio) * 100;
            } else {
                s3 = 0;
            }
            score = s1 - s2 + s3;
            score = Double.parseDouble(StockUtils.formatDouble(score));
            if (score > 0) {
                System.out.println(updateMap.get("id") + " " + updateMap.get("name") + " " + score + " " + s1 + " " + s2 + " " + s3);
            }
            updateMap.put("score", score);
        }
        //排序
        Collections.sort(updateMapList, this.stockScoreComparator);
        //设置排序号
        for (int index = 0; index < updateMapList.size(); index++) {
            updateMapList.get(index).put("sort", (long) index);
        }
        this.stockMoneyFlowEntityDao.batchInsert(updateMapList);
    }

    @Override
    public List<String> getStockIdAll() {
        List<String> result = Collections.EMPTY_LIST;
        ResultSet rs = this.stockEntityDao.execute(this.CqlInquireStockIdAll);
        List<Row> rList = rs.all();
        if (rList.isEmpty() == false) {
            result = new ArrayList<String>(rList.size());
            for (Row r : rList) {
                result.add(r.getString(0));
            }
        }
        return result;
    }

    @Override
    public void insertStockMoneyFlowMinute(Map<String, Object> insertMap) {
        this.stockMoneyFlowMinuteEntityDao.insert(insertMap);
    }

    @Override
    public void truncateStockMoneyFlowMinute() {
        this.stockMoneyFlowMinuteEntityDao.execute(this.CqlTruncateStockMoneyFlowMinute);
    }

    @Override
    public void insertStockMoneyFlowDay(Map<String, Object> insertMap) {
        this.stockMoneyFlowHistoryEntityDao.insert(insertMap);
    }
}
