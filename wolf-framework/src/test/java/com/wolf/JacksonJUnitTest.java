package com.wolf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author aladdin
 */
public class JacksonJUnitTest {

    public JacksonJUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

//    @Test
    public void json2Map() throws IOException {
        String json = "{\"content\":\"人工客服服务时间为:上午8:00~11:00，下午13:30~17:00。"
                + "欢迎大家使用智能客服momi！\", \"enabled\": true}";
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> parameterMap;
        JsonNode rootNode = mapper.readValue(json, JsonNode.class);
        if (rootNode != null) {
            //读数据
            parameterMap = new HashMap(8, 1);
            Map.Entry<String, JsonNode> entry;
            String name;
            Object value;
            Iterator<Map.Entry<String, JsonNode>> iterator = rootNode.fields();
            while (iterator.hasNext()) {
                entry = iterator.next();
                name = entry.getKey();
                value = entry.getValue();
                parameterMap.put(name, value);
            }
            System.out.println(parameterMap.toString());
        }
    }

    @Test
    public void Map2Json() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> parameterMap = new HashMap();
        parameterMap.put("name", "22222");
        parameterMap.put("enabled", false);
        parameterMap.put("num", 1);
        Map<String, Object> childMap = new HashMap();
        childMap.put("sub", "111");
        childMap.put("enabled", false);
        childMap.put("subNum", 2);
        parameterMap.put("child", childMap);
        //
        String[] sArray = {"a", "b", "c"};
        parameterMap.put("sArray", sArray);
        //
        List<Map<String, Object>> mapArray = new ArrayList();
        Map<String, Object> map1 = new HashMap();
        map1.put("name", "d");
        map1.put("value", "d");
        mapArray.add(map1);
        parameterMap.put("mapArray", mapArray);
        String json = mapper.writeValueAsString(parameterMap);
        System.out.println(json);
    }

}
