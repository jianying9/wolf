package com.demo.localservice;

import com.wolf.framework.local.Local;

/**
 *
 * @author jianying9
 */
public interface UserLocalService extends Local{
    
    
    public boolean existUser(String userName);
    
}
