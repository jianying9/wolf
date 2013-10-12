package com.wolf.framework.derby;

import com.wolf.framework.context.Resource;
import java.sql.SQLException;
import org.apache.derby.jdbc.EmbeddedSimpleDataSource;

/**
 *
 * @author aladdin
 */
public class DerbyResourceImpl implements Resource {

    private final EmbeddedSimpleDataSource embeddedSimpleDataSource;

    public DerbyResourceImpl(EmbeddedSimpleDataSource embeddedSimpleDataSource) {
        this.embeddedSimpleDataSource = embeddedSimpleDataSource;
    }

    @Override
    public void destory() {
        this.embeddedSimpleDataSource.setShutdownDatabase("shutdown");
        try {
            this.embeddedSimpleDataSource.getConnection();
        } catch (SQLException ex) {
        }
    }
}
