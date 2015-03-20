
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jianying9
 */
public class CassandraJUnitTest {

    public CassandraJUnitTest() {
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

    private Cluster cluster;

    //
    @Test
    public void hello() {
        cluster = Cluster.builder()
                .addContactPoint("192.168.181.35")
                .withCredentials("test", "test")
                .build();
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n",
                metadata.getClusterName());
        for (Host host : metadata.getAllHosts()) {
            System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
                    host.getDatacenter(), host.getAddress(), host.getRack());
        }
        Session session = cluster.connect();
        Insert insert = QueryBuilder
                .insertInto("test", "users")
                .value("firstName", "Dwayne")
                .value("lastName", "Garcia")
                .value("email", "dwayne@example.com");
        ResultSet ResultSet = session.execute(insert);
        cluster.close();
    }
}
