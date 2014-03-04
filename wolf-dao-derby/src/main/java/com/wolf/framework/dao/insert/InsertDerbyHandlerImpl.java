package com.wolf.framework.dao.insert;

import com.wolf.framework.dao.AbstractDaoHandler;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.insert.InsertHandler;
import com.wolf.framework.dao.parser.ColumnHandler;
import com.wolf.framework.data.DataHandler;
import com.wolf.framework.derby.DerbyHandler;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public class InsertDerbyHandlerImpl<T extends Entity> extends AbstractDaoHandler<T> implements InsertHandler<T> {
    
    private final DerbyHandler derbyHandler;
    private final ColumnHandler keyHandler;
    
    public InsertDerbyHandlerImpl(DerbyHandler derbyHandler, Class<T> clazz, ColumnHandler keyHandler) {
        super(clazz);
        this.derbyHandler = derbyHandler;
        this.keyHandler = keyHandler;
    }
    
    @Override
    public String insert(Map<String, String> entityMap) {
        final String keyName = this.keyHandler.getColumnName();
        String keyValue = entityMap.get(keyName);
        if (keyValue == null) {
            keyValue = this.keyHandler.getDataHandler().getNextValue();
            entityMap.put(keyName, keyValue);
        }
        this.derbyHandler.insert(entityMap);
        return keyValue;
    }
    
    @Override
    public void batchInsert(List<Map<String, String>> entityMapList) {
        final String keyName = this.keyHandler.getColumnName();
        String keyValue;
        DataHandler keyDataHandler = this.keyHandler.getDataHandler();
        for (Map<String, String> entityMap : entityMapList) {
            keyValue = entityMap.get(keyName);
            if (keyValue == null) {
                keyValue = keyDataHandler.getNextValue();
                entityMap.put(keyName, keyValue);
            }
        }
        this.derbyHandler.batchInsert(entityMapList);
    }
}
