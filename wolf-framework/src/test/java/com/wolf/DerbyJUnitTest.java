package com.wolf;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.derby.jdbc.ClientDataSource;
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

    @Test
    public void derbyClientTest() throws SQLException {
        ClientDataSource clientDataSource = new ClientDataSource();
        clientDataSource.setCreateDatabase("create");
        clientDataSource.setDatabaseName("spider");
        clientDataSource.setServerName("192.168.19.218");
        clientDataSource.setPortNumber(1527);
        Connection con = clientDataSource.getConnection();
        con.close();
    }
}
