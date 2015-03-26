package com.wolf.framework.dao.cassandra;

import com.wolf.framework.dao.DatabaseHandler;
import com.wolf.framework.dao.delete.DeleteHandler;
import com.wolf.framework.dao.insert.InsertHandler;
import com.wolf.framework.dao.update.UpdateHandler;

/**
 *
 * @author jianying9
 */
public interface CassandraHandler extends DatabaseHandler, UpdateHandler, DeleteHandler, InsertHandler{
}
