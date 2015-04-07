package com.demo.localservice;

import com.demo.entity.UserEntity;
import com.wolf.framework.local.Local;

/**
 *
 * @author jianying9
 */
public interface UserLocalService extends Local {

    public boolean existUser(String userName);

    public void insertUser(String userName, String password);
    
    public UserEntity inquireByUserName(String userName);
    
    public void countLogin(String userName);
    
    public void insertSession(String userName, String sid);
    
    public void deleteSession(String sid);
}
