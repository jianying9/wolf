package com.wolf.framework.dao.update;

import com.wolf.framework.dao.AbstractDaoHandler;
import com.wolf.framework.dao.parser.ColumnHandler;
import com.wolf.framework.dao.update.UpdateHandler;
import com.wolf.framework.derby.DerbyHandler;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public class UpdateDerbyHandlerImpl extends AbstractDaoHandler implements UpdateHandler {

    private final DerbyHandler derbyHandler;
    private final ColumnHandler keyHandler;

    public UpdateDerbyHandlerImpl(DerbyHandler derbyHandler, Class clazz, ColumnHandler keyHandler) {
        super(clazz);
        this.derbyHandler = derbyHandler;
        this.keyHandler = keyHandler;
    }

    @Override
    public String update(Map<String, String> entityMap) {
        final String keyName = this.keyHandler.getColumnName();
        String keyValue = entityMap.get(keyName);
        this.derbyHandler.update(entityMap);
        return keyValue;
    }

    @Override
    public void batchUpdate(List<Map<String, String>> entityMapList) {
        this.derbyHandler.batchUpdate(entityMapList);
    }
}
