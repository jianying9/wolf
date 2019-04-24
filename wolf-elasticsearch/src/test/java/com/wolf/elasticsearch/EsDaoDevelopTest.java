package com.wolf.elasticsearch;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.elasticsearch.EsAdminContextImpl;
import com.wolf.framework.dao.elasticsearch.EsConfig;
import com.wolf.framework.dao.elasticsearch.EsEntityDao;
import com.wolf.framework.test.TestHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jianying9
 */
public class EsDaoDevelopTest {

    protected static TestHandler testHandler;

    static {
        Map<String, String> parameterMap = new HashMap(8, 1);
        parameterMap.put(FrameworkConfig.BUILD_TIMESTAMP, Long.toString(System.currentTimeMillis()));
        parameterMap.put(FrameworkConfig.ANNOTATION_SCAN_PACKAGES, "com.wolf.elasticsearch");
        //
        parameterMap.put(EsConfig.ELASTICSEARCH_HOST, "106.15.34.48");
        //
        parameterMap.put(FrameworkConfig.COMPILE_MODEL, FrameworkConfig.UNIT_TEST);
        //
        testHandler = new TestHandler(parameterMap);
    }

    public EsDaoDevelopTest() {
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

    //
    @Test
    public void check() {
        EsAdminContextImpl ctx = EsAdminContextImpl.getInstance(ApplicationContext.CONTEXT);
        EsEntityDao<EsTestEntity> esTestEntityDao = ctx.getEsEntityDao(EsTestEntity.class);
        //
        Map<String, Object> map = new HashMap();
//
        List<String> voteIdList = new ArrayList();
        voteIdList.add("1");
        voteIdList.add("4");
        voteIdList.add("5");
        map.put("showId", 3);
        map.put("enabled", false);
        map.put("content", "zlw3");
        map.put("title", "haha3");
        map.put("money", 311.1);
        map.put("voteIdList", voteIdList);
        esTestEntityDao.insert(map);
        //
//        esTestEntityDao.delete(2);
        EsTestEntity esTestEntity = esTestEntityDao.inquireByKey(2);
        System.out.println(esTestEntity.getVoteIdList());

        List<EsTestEntity> esTestEntityList = esTestEntityDao.search(QueryBuilders.boolQuery());
        for (EsTestEntity etEntity : esTestEntityList) {
            System.out.println(etEntity.getShowId());
        }
    }
}
