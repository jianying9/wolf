package com.wolf.framework.doc;

import com.wolf.framework.data.DataType;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.service.parameter.filter.FilterType;
import com.wolf.framework.service.request.ServiceRequest;
import com.wolf.framework.service.response.ServiceResponse;

/**
 *
 * @author jianying9
 */
@ServiceConfig(
        route = "/doc/test/object",
        group = "Test",
        validateSession = false,
        desc = "service类型测试服务",
        responseCodes = {},
        requestConfigs = {
            @RequestConfig(name = "name", dataType = DataType.CHAR, desc = "名称", max = 28, min = 2, must = true),
            @RequestConfig(name = "value", dataType = DataType.CHAR, desc = "值", max = 128, min = 0, must = false)
        },
        responseConfigs = {
            @ResponseConfig(name = "name", dataType = DataType.CHAR, desc = ""),
            @ResponseConfig(name = "value", dataType = DataType.CHAR, desc = "", filterTypes = {FilterType.ESCAPE})
        }
)
public class TestServiceImpl implements Service {

    @Override
    public void execute(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
