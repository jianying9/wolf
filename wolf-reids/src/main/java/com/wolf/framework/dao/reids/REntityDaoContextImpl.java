package com.wolf.framework.dao.reids;

import com.wolf.framework.dao.Entity;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aladdin
 * @param <T>
 */
public class REntityDaoContextImpl<T extends Entity> implements REntityDaoContext<T> {

    private final Map<String, String> existClassMap = new HashMap<String, String>(8, 1);
    //entity处理类集合
    private final Map<Class<T>, REntityDao<T>> entityDaoMap;

    public REntityDaoContextImpl() {
        this.entityDaoMap = new HashMap<Class<T>, REntityDao<T>>(8, 1);
    }

    @Override
    public void putREntityDao(Class clazz, REntityDao entityDao, String entityName) {
        //判断实体是否存在
        if (this.entityDaoMap.containsKey(clazz)) {
            String existClassName = this.existClassMap.get(entityName);
            if (existClassName == null) {
                existClassName = "NULL";
            }
            StringBuilder errBuilder = new StringBuilder(1024);
            errBuilder.append("Error putting REntityDao. Cause: entityName duplicated : ")
                    .append(entityName).append("\n").append("exist class : ").append(existClassName).append("\n")
                    .append("this class : ").append(clazz.getName());
            throw new RuntimeException(errBuilder.toString());
        }
        this.entityDaoMap.put(clazz, entityDao);
        this.existClassMap.put(entityName, clazz.getName());
    }

    @Override
    public REntityDao getREntityDao(Class<T> clazz) {
        return this.entityDaoMap.get(clazz);
    }

    @Override
    public Map<Class<T>, REntityDao<T>> getREntityDaoMap() {
        return Collections.unmodifiableMap(this.entityDaoMap);
    }

    @Override
    public boolean assertExistREntity(Class<T> clazz) {
        return this.entityDaoMap.containsKey(clazz);
    }
}
