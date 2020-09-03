package com.wolf.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.elasticsearch.EsAdminContextImpl;
import com.wolf.framework.dao.elasticsearch.EsConfig;
import com.wolf.framework.test.TestHandler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jianying9
 */
public class EsQueryBuilderJUnitTest
{

    protected static TestHandler testHandler;

    static {
        Map<String, String> parameterMap = new HashMap(8, 1);
        parameterMap.put(FrameworkConfig.BUILD_TIMESTAMP, Long.toString(System.currentTimeMillis()));
        parameterMap.put(FrameworkConfig.ANNOTATION_SCAN_PACKAGES, "com.wolf.elasticsearch");
        //
        parameterMap.put(EsConfig.ELASTICSEARCH_HOST, "106.15.34.48");
        parameterMap.put(EsConfig.ELASTICSEARCH_DATABASE, "");
        parameterMap.put(EsConfig.ELASTICSEARCH_CLUSTER_NAME, "xzlm-application");
        //
        parameterMap.put(FrameworkConfig.COMPILE_MODEL, FrameworkConfig.UNIT_TEST);
        //
        testHandler = new TestHandler(parameterMap);
    }

    public EsQueryBuilderJUnitTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    //
    @Test
    public void hello() throws JsonProcessingException
    {
        EsAdminContextImpl ctx = new EsAdminContextImpl(ApplicationContext.CONTEXT);
        //
        SearchRequestBuilder searchRequestBuilder = ctx.getTransportClient().prepareSearch("test")
                .setTypes("test")
                .setFrom(1)
                .setSize(100);
//                .setVersion(false);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        
        boolQueryBuilder.must(QueryBuilders.termQuery("aa", "33"));
        boolQueryBuilder.must(QueryBuilders.termQuery("bb", "44"));
        boolQueryBuilder.mustNot(QueryBuilders.termQuery("cc", "55"));
        boolQueryBuilder.mustNot(QueryBuilders.termQuery("dd", "66"));
        searchRequestBuilder.setQuery(boolQueryBuilder);
        
        //
        SortBuilder sortBuilder = SortBuilders.fieldSort("tt").order(SortOrder.DESC);
        searchRequestBuilder.addSort(sortBuilder);
        System.out.println(searchRequestBuilder);
    }
}
