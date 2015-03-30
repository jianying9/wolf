
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.in;
import static com.datastax.driver.core.querybuilder.QueryBuilder.set;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Update;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
    public void hello() throws ExecutionException, InterruptedException {
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
        Select select = QueryBuilder.select()
                .all()
                .from("test", "users");
        select.where(in("username", "1", "2"));
        System.out.println(select.toString());
//        ResultSet results = session.execute(select.toString());
        //SELECT * FROM test.users WHERE username='?';
        PreparedStatement ps = session.prepare("SELECT * FROM test.users WHERE username=?;");
        ResultSetFuture rsf = session.executeAsync(ps.bind("test"));
        ResultSet rs = rsf.get();
        Row r = rs.one();
        System.out.println(r);
        //
        Insert insert = QueryBuilder
                .insertInto("test", "users")
                .value("username", "test1")
                .value("password", "123456");
        System.out.println(insert.toString());
        //INSERT INTO addressbook.contact(firstName,lastName,email) VALUES ('Dwayne','Garcia','dwayne@example.com');
        //INSERT INTO test.users(username,password) VALUES ('test1','123456');
        ps = session.prepare("INSERT INTO test.users(username,password) VALUES (?,?);");
        rsf = session.executeAsync(ps.bind("test2", "1234567"));
        rs = rsf.get();
        r = rs.one();
        System.out.println(r);
        //
        Update update = QueryBuilder.update("addressbook", "contact");
        update.with(set("email", "dwayne.garcia@example.com"));
        update.where(eq("username", "dgarcia"));
        System.out.println(update.toString());
        //
        Delete delete = QueryBuilder.delete()
                .from("addresbook", "contact");
        delete.where(eq("username", "dgarcia"));
        System.out.println(delete.toString());
        //
        ps = session.prepare("SELECT COUNT(*) FROM test.users;");
        rsf = session.executeAsync(ps.bind());
        try {
            rs = rsf.get();
            r = rs.one();
        } catch (InterruptedException ex) {
        } catch (ExecutionException ex) {
        }
        if (r != null) {
            long num = r.getLong(0);
            System.out.println(num);
        }
        //
        ps = session.prepare("update test.users set tags = tags + ? where username = ?;");
        Set<String> sets = new HashSet<String>();
        sets.add("e");
        rsf = session.executeAsync(ps.bind(sets, "test"));
        try {
            rs = rsf.get();
        } catch (InterruptedException ex) {
        } catch (ExecutionException ex) {
        }
        //
        ps = session.prepare("select tags from test.users where username = 'test';");
        rsf = session.executeAsync(ps.bind());
        try {
            rs = rsf.get();
            r = rs.one();
        } catch (InterruptedException ex) {
        } catch (ExecutionException ex) {
        }
        if (r != null) {
            Set<String> tags = r.getSet(0, String.class);
            System.out.println(tags);
        }
        cluster.close();
    }
}
