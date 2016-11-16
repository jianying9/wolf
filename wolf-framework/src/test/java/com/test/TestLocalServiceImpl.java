package com.test;

import com.wolf.framework.local.LocalServiceConfig;

/**
 *
 * @author jianying9
 */
@LocalServiceConfig
public class TestLocalServiceImpl implements TestLocalService{

    @Override
    public void init() {
        System.out.println("test local service init.");
    }
}
