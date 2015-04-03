package com.demo.localservice;

import com.demo.entity.SessionEntity;
import com.demo.entity.UserCountEntity;
import com.demo.entity.UserEntity;
import com.wolf.framework.dao.cassandra.CEntityDao;
import com.wolf.framework.dao.cassandra.annotation.InjectCDao;
import com.wolf.framework.local.InjectLocalService;
import com.wolf.framework.local.LocalServiceConfig;

/**
 *
 * @author jianying9
 */
@LocalServiceConfig(description = "用户管理内部接口")
public class UserLocalServiceImpl implements UserLocalService{
    
    @InjectCDao(clazz = UserEntity.class)
    private CEntityDao<UserEntity> userEntityDao;
    //
    @InjectCDao(clazz = UserCountEntity.class)
    private CEntityDao<UserCountEntity> userCountEntityDao;
    //
    @InjectCDao(clazz = SessionEntity.class)
    private CEntityDao<SessionEntity> sessionEntityDao;
    
    @InjectLocalService()
    private TableCountLocalService tableCountLocalService;

    @Override
    public void init() {
        //初始化
        if(this.tableCountLocalService.exist("session") == false) {
            this.tableCountLocalService.nextKey("session", 10000000);
        }
    }

    @Override
    public boolean existUser(String userName) {
        return this.userEntityDao.exist(userName);
    }
}
