package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.AbstractDaoHandler;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.derby.DerbyHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public final class InquireByKeyFromDataHandlerImpl<T extends Entity> extends AbstractDaoHandler<T> implements InquireByKeyHandler<T> {

    private final DerbyHandler derbyHandler;

    public InquireByKeyFromDataHandlerImpl(DerbyHandler derbyHandler, Class<T> clazz) {
        super(clazz);
        this.derbyHandler = derbyHandler;
    }

    @Override
    public T inquireByKey(String keyValue) {
        T t = null;
        Map<String, String> entityMap = this.derbyHandler.inquireByKey(keyValue);
        if (entityMap != null) {
            t = this.newInstance(entityMap);
        }
        return t;
    }

    @Override
    public List<T> inquireByKeys(List<String> keyValues) {
        List<T> tList;
        List<Map<String, String>> entityMapList = this.derbyHandler.inquireBykeys(keyValues);
        if (keyValues.isEmpty() == false) {
            tList = this.newInstance(entityMapList);
        } else {
            tList = new ArrayList<T>(0);
        }
        return tList;
    }

    @Override
    public Map<String, String> inquireMapByKey(String keyValue) {
        return this.derbyHandler.inquireByKey(keyValue);
    }

    @Override
    public List<Map<String, String>> inquireMapByKeys(List<String> keyValues) {
        return this.derbyHandler.inquireBykeys(keyValues);
    }
}
