package com.wolf.framework.dao;

import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.logger.LogFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

/**
 * dynamic entity dao
 *
 * @author aladdin
 */
public abstract class AbstractDaoHandler<T extends Entity> {

    protected final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.DAO);
    //实体class
    private final Class<T> clazz;

    public AbstractDaoHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 解析map数据，实例化clazz
     *
     * @param entityMap
     * @return
     */
    protected T newInstance(Map<String, String> entityMap) {
        T t;
        try {
            t = clazz.newInstance();
        } catch (Exception e) {
            logger.error("There was an error instancing  class {}.Cause: {}", clazz.getName(), e.getMessage());
            throw new RuntimeException("There was an error instancing class ".concat(clazz.getName()));
        }
        t.parseMap(entityMap);
        return t;
    }

    protected List<T> newInstance(List<Map<String, String>> entityMapList) {
        List<T> tList = new ArrayList<T>(entityMapList.size());
        T t;
        for (Map<String, String> entityMap : entityMapList) {
            t = this.newInstance(entityMap);
            tList.add(t);
        }
        return tList;
    }
}
