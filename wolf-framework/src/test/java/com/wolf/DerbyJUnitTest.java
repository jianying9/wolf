package com.wolf;

import com.wolf.framework.context.ApplicationContextBuilder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.sql.DataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author aladdin
 */
public class DerbyJUnitTest {

    public DerbyJUnitTest() {
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
    
    public void testOne(DataSource dataSource) {
        Connection conn = null;
        List<Map<String, String>> resultMapList;
        String sql = "SELECT * FROM \"t_Employee\" WHERE \"tag\" LIKE ? ";
        System.out.println(sql);
        try {
            conn = dataSource.getConnection();
            try (PreparedStatement stat = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                stat.setString(1, "%java%");
                try (ResultSet rs = stat.executeQuery()) {
                    int rowCount = 0;
                    while (rs.next()) {
                        rowCount++;
                    }
                    if (rowCount > 0) {
                        Map<String, String> resultMap;
                        resultMapList = new ArrayList(rowCount);
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int columnCount = rsmd.getColumnCount();
                        rs.beforeFirst();
                        while (rs.next()) {
                            resultMap = new HashMap(columnCount, 1);
                            for (int index = 1; index <= columnCount; index++) {
                                resultMap.put(rsmd.getColumnLabel(index), rs.getString(index));
                            }
                            resultMapList.add(resultMap);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            Throwable throwable = null;
            for (Throwable t : e) {
                throwable = t;
                break;
            }
            throw new RuntimeException("111111");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }
    
    public void testTwo(DataSource dataSource) {
        Connection conn = null;
        List<Map<String, String>> resultMapList;
        String sql = "SELECT * FROM \"t_Employee\" WHERE \"tag\" LIKE '%java%'";
        System.out.println(sql);
        try {
            conn = dataSource.getConnection();
            try (PreparedStatement stat = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet rs = stat.executeQuery()) {
                int rowCount = 0;
                while (rs.next()) {
                    rowCount++;
                }
                if (rowCount > 0) {
                    Map<String, String> resultMap;
                    resultMapList = new ArrayList(rowCount);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int columnCount = rsmd.getColumnCount();
                    rs.beforeFirst();
                    while (rs.next()) {
                        resultMap = new HashMap(columnCount, 1);
                        for (int index = 1; index <= columnCount; index++) {
                            resultMap.put(rsmd.getColumnLabel(index), rs.getString(index));
                        }
                        resultMapList.add(resultMap);
                    }
                }
            }
        } catch (SQLException e) {
            Throwable throwable = null;
            for (Throwable t : e) {
                throwable = t;
                break;
            }
            throw new RuntimeException("111111");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }
    
    private final Pattern valueFilterPattern = Pattern.compile("'|\"");

//    @Test
    public void derbyClientTest() throws SQLException {
        Properties configProperties = new Properties();
        configProperties.setProperty("appPath", "/search");
        configProperties.setProperty("packageName", "com.search");
        configProperties.setProperty("hbaseZookeeperQuorum", "192.168.19.221");
//        configProperties.setProperty("fsDefaultName", "hdfs://192.168.64.50:9000/");
//        configProperties.setProperty("dataBaseType", "EMBEDDED");
//        configProperties.setProperty("dataBaseName", "/data/derby/spider");
        configProperties.setProperty("dataBaseType", "CLIENT");
        configProperties.setProperty("dataBaseName", "search");
        configProperties.setProperty("dataServerName", "192.168.19.218");
        configProperties.setProperty("dataServerPort", "1527");
        ApplicationContextBuilder applicationContextBuilder = new ApplicationContextBuilder(configProperties);
        //
//        this.testTwo(dataSource);
        //
//        this.testOne(dataSource);
        String a = "'dees\"wd'";
        String b = this.valueFilterPattern.matcher(a).replaceAll("");
        System.out.println(b);
    }
}
