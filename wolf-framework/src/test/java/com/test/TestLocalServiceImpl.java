package com.test;

import com.wolf.framework.local.LocalServiceConfig;
import com.wolf.framework.reponse.Response;
import com.wolf.framework.request.Request;
import com.wolf.framework.service.Service;

/**
 *
 * @author jianying9
 */
@LocalServiceConfig(
        description = "redis table主键值管理")
public class TestLocalServiceImpl implements Service, TestLocalService{

    @Override
    public void init() {
        System.out.println("test local service init.");
    }

    @Override
    public void execute(Request request, Response response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
