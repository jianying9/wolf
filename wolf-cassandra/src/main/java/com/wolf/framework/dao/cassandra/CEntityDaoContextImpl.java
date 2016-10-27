package com.wolf.framework.dao.cassandra;

import com.wolf.framework.dao.Entity;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aladdin
 * @param <T>
 */
public class CEntityDaoContextImpl<T extends Entity> implements CEntityDaoContext<T> {

    private final Map<String, String> existClassMap = new HashMap<>(8, 1);
    //entity处理类集合
    private final Map<Class<T>, CEntityDao<T>> entityDaoMap;

    public CEntityDaoContextImpl() {
        this.entityDaoMap = new HashMap<>(8, 1);
    }

    @Override
    public void putCEntityDao(Class<T> clazz, CEntityDao<T> entityDao, String entityName) {
        //判断实体是否存在
        if (this.entityDaoMap.containsKey(clazz)) {
            String existClassName = this.existClassMap.get(entityName);
            if (existClassName == null) {
                existClassName = "NULL";
            }
            StringBuilder errBuilder = new StringBuilder(1024);
            errBuilder.append("Error putting CEntityDao. Cause: entityName duplicated : ")
                    .append(entityName).append("\n").append("exist class : ").append(existClassName).append("\n")
                    .append("this class : ").append(clazz.getName());
            throw new RuntimeException(errBuilder.toString());
        }
        this.entityDaoMap.put(clazz, entityDao);
        this.existClassMap.put(entityName, clazz.getName());
    }

    @Override
    public CEntityDao getCEntityDao(Class<T> clazz) {
        return this.entityDaoMap.get(clazz);
    }

    @Override
    public Map<Class<T>, CEntityDao<T>> getCEntityDaoMap() {
        return Collections.unmodifiableMap(this.entityDaoMap);
    }

    @Override
    public boolean assertExistCEntity(Class<T> clazz) {
        return this.entityDaoMap.containsKey(clazz);
    }
}
