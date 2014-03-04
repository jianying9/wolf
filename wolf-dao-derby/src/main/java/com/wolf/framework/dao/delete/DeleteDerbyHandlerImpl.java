package com.wolf.framework.dao.delete;

import com.wolf.framework.dao.delete.DeleteHandler;
import com.wolf.framework.derby.DerbyHandler;
import java.util.List;

/**
 *
 * @author aladdin
 */
public class DeleteDerbyHandlerImpl implements DeleteHandler {
    
    private final DerbyHandler derbyHandler;

    public DeleteDerbyHandlerImpl(DerbyHandler derbyHandler) {
        this.derbyHandler = derbyHandler;
    }
    
    @Override
    public void delete(String keyValue) {
        this.derbyHandler.delete(keyValue);
    }

    @Override
    public void batchDelete(List<String> keyValues) {
        if(keyValues.isEmpty() == false) {
            this.derbyHandler.batchDelete(keyValues);
        }
    }
}
