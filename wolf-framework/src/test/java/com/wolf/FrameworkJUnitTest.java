package com.wolf;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.context.ApplicationContextBuilder;
import com.wolf.framework.utils.StringUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author aladdin
 */
public class FrameworkJUnitTest {

    public FrameworkJUnitTest() {
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
    public void hello() {
        Map<String, String> parameterMap = new HashMap<>(8, 1);
        parameterMap.put(FrameworkConfig.COMPILE_MODEL, FrameworkConfig.DEVELOPMENT);
        parameterMap.put(FrameworkConfig.ANNOTATION_SCAN_PACKAGES, "com.test");
        parameterMap.put(FrameworkConfig.TASK_CORE_POOL_SIZE, "10");
        parameterMap.put(FrameworkConfig.TASK_MAX_POOL_SIZE, "20");
        //
        parameterMap.put(FrameworkConfig.REDIS_SERVER_HOST, "192.168.59.49");
        parameterMap.put(FrameworkConfig.REDIS_SERVER_PORT, "6379");
        parameterMap.put(FrameworkConfig.REDIS_MAX_POOL_SIZE, "20");
        parameterMap.put(FrameworkConfig.REDIS_MIN_POOL_SIZE, "10");
        ApplicationContextBuilder applicationContextBuilder = new ApplicationContextBuilder(parameterMap);
        applicationContextBuilder.build();
    }

//    @Test
    public void testJson() throws IOException {
        String json = "{\"content\":\"人工客服服务时间为:上午8:00~11:00，下午13:30~17:00。"
                + "欢迎大家使用智能客服momi！\"}";
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        Map<String, String> parameterMap;
        JsonNode rootNode = mapper.readValue(json, JsonNode.class);
        if (rootNode != null) {
            //读数据
            parameterMap = new HashMap<>(8, 1);
            Map.Entry<String, JsonNode> entry;
            String name;
            String value;
            Iterator<Map.Entry<String, JsonNode>> iterator = rootNode.getFields();
            while (iterator.hasNext()) {
                entry = iterator.next();
                name = entry.getKey();
                value = entry.getValue().getTextValue();
                parameterMap.put(name, value);
            }
            System.out.println(parameterMap.toString());
        }
    }
}
