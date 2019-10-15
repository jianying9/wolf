package com.wolf.thirdparty.http;

import com.wolf.framework.local.LocalServiceConfig;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author jianying9
 */
@LocalServiceConfig
public class HttpLocalImpl implements HttpLocal {

    private final CloseableHttpClient client;

    public HttpLocalImpl() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        this.client = builder.build();
    }

    @Override
    public void init() {
    }

    private String buildUrl(String host, String path, Map<String, String> parameterMap) throws UnsupportedEncodingException {
        StringBuilder sbUrl = new StringBuilder(200);
        sbUrl.append(host).append(path);
        if (null != parameterMap) {
            StringBuilder sbQuery = new StringBuilder(100);
            String key;
            String value;
            for (Map.Entry<String, String> query : parameterMap.entrySet()) {
                key = query.getKey();
                value = query.getValue();
                if (value != null && value.isEmpty() == false) {
                    value = URLEncoder.encode(value, "utf-8");
                    sbQuery.append(key).append("=").append(value).append("&");
                }
            }
            if (0 < sbQuery.length()) {
                sbQuery.setLength(sbQuery.length() - 1);
                sbUrl.append("?").append(sbQuery);
            }
        }
        return sbUrl.toString();
    }

    @Override
    public String doGet(String host, String path, Map<String, String> parameterMap) {
        return this.doGet(host, path, parameterMap, Collections.EMPTY_MAP);
    }

    private Map<String, String> initHeader() {
        Map<String, String> headerMap = new HashMap();
        headerMap.put("Accept", "application/json, text/javascript, */*; q=0.01");
        headerMap.put("Accept-Encoding", "gzip, deflate");
        headerMap.put("Accept-Language", "zh,en-US;q=0.9,en;q=0.8,zh-CN;q=0.7,zh-TW;q=0.6");
        headerMap.put("Cache-Control", "no-cache");
        headerMap.put("Connection", "keep-alive");
        headerMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        headerMap.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.75 Safari/537.36");
        return headerMap;
    }

    @Override
    public String doGet(String host, String path, Map<String, String> parameterMap, Map<String, String> headerMap) {
        String responseBody = "";
        Map<String, String> initHeaderMap = this.initHeader();
        initHeaderMap.putAll(headerMap);
        try {
            String url = this.buildUrl(host, path, parameterMap);
            HttpGet request = new HttpGet(url);
            for (Map.Entry<String, String> entry : initHeaderMap.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            responseBody = this.client.execute(request, responseHandler);
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return responseBody;
    }

    @Override
    public String doPost(String url, Map<String, String> parameterMap) {
        return this.doPost(url, parameterMap, null);
    }

    @Override
    public String doPost(String url, Map<String, String> parameterMap, Map<String, String> headerMap) {
        String responseBody = "";
        Map<String, String> initHeaderMap = this.initHeader();
        initHeaderMap.putAll(headerMap);
        try {
            HttpPost request = new HttpPost(url);
            for (Map.Entry<String, String> entry : initHeaderMap.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
            //
            String key;
            String value;
            List<NameValuePair> urlParameterList = new ArrayList(parameterMap.size());
            for (Map.Entry<String, String> query : parameterMap.entrySet()) {
                key = query.getKey();
                value = query.getValue();
                if (value != null && value.isEmpty() == false) {
                    urlParameterList.add(new BasicNameValuePair(key, value));
                }
            }
            //
            HttpEntity postBodyEntity = new UrlEncodedFormEntity(urlParameterList, "UTF-8");
            request.setEntity(postBodyEntity);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            responseBody = this.client.execute(request, responseHandler);
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return responseBody;
    }
}
