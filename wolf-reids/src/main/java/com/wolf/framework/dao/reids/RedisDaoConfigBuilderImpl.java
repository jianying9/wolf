package com.wolf.framework.dao.reids;

import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.dao.DaoConfig;
import com.wolf.framework.dao.DaoConfigBuilder;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.reids.annotation.RDaoConfig;
import com.wolf.framework.dao.reids.injecter.RDaoInjecterImpl;
import com.wolf.framework.dao.reids.parser.RDaoConfigParser;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.logger.LogFactory;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

/**
 *
 * @author jianying9
 * @param <T>
 */
@DaoConfig()
public class RedisDaoConfigBuilderImpl<T extends Entity> implements DaoConfigBuilder {

    private final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
    private final List<Class<T>> rEntityClassList = new ArrayList<Class<T>>();
    private RedisAdminContext redisAdminContext;
    private REntityDaoContext<T> rEntityDaoContext;

    @Override
    public void init(ApplicationContext context) {
        this.redisAdminContext = new RedisAdminContextImpl(context);
        this.rEntityDaoContext = new REntityDaoContextImpl<T>(this.redisAdminContext);
        TestRedisHandler.redisAdminContext = this.redisAdminContext;
    }

    @Override
    public Class<?> getAnnotation() {
        return RDaoConfig.class;
    }

    @Override
    public void putClazz(Class<?> clazz) {
        //是否是实体
        if (Entity.class.isAssignableFrom(clazz)) {
            Class<T> clazzt = (Class<T>) clazz;
            if (clazzt.isAnnotationPresent(RDaoConfig.class)) {
                if (this.rEntityClassList.contains(clazzt) == false) {
                    this.rEntityClassList.add(clazzt);
                    this.logger.debug("find redis entity class ".concat(clazz.getName()));
                }
            }
        }
    }

    @Override
    public Injecter getInjecter() {
        return new RDaoInjecterImpl(this.rEntityDaoContext);
    }

    @Override
    public void build() {
//        解析redis EntityDao
        if (this.rEntityClassList.isEmpty() == false) {
            this.logger.info("parsing annotation RDaoConfig...");
            this.rEntityDaoContext = new REntityDaoContextImpl<T>(this.redisAdminContext);
            final RDaoConfigParser<T> rEntityConfigDaoParser = new RDaoConfigParser<T>(this.rEntityDaoContext);
            for (Class<T> clazzt : this.rEntityClassList) {
                rEntityConfigDaoParser.parse(clazzt);
            }
        }
    }
}
