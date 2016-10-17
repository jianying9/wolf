package com.test;

import com.wolf.framework.local.LocalServiceConfig;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.request.ServiceRequest;
import com.wolf.framework.service.response.ServiceResponse;

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
    public void execute(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
