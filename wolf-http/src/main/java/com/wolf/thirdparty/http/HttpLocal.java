package com.wolf.thirdparty.http;

import com.wolf.framework.local.Local;
import java.util.Map;

/**
 * http服务
 *
 * @author jianying9
 */
public interface HttpLocal extends Local {

    public String doGet(String host, String path, Map<String, String> parameterMap);

    public String doGet(String host, String path, Map<String, String> parameterMap, Map<String, String> headerMap);

    public String doPost(String url, Map<String, String> parameterMap);

    public String doPost(String url, Map<String, String> parameterMap, Map<String, String> headerMap);
    
    public String doPost(String url, String xml);
    
    public String doPostJson(String url, String json);

}
