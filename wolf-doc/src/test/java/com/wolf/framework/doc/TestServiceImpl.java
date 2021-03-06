package com.wolf.framework.doc;

import com.wolf.framework.reponse.Response;
import com.wolf.framework.request.Request;
import com.wolf.framework.service.Service;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.RequestDataType;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.service.parameter.ResponseDataType;
import com.wolf.framework.service.parameter.filter.FilterType;

/**
 *
 * @author jianying9
 */
@ServiceConfig(
        route = "/doc/test/object",
        validateSession = false,
        desc = "service类型测试服务",
        responseCodes = {},
        requestConfigs = {
            @RequestConfig(name = "name", dataType = RequestDataType.STRING, desc = "名称", max = 28, min = 2, required = true),
            @RequestConfig(name = "value", dataType = RequestDataType.STRING, desc = "值", max = 128, min = 0, required = false)
        },
        responseConfigs = {
            @ResponseConfig(name = "name", dataType = ResponseDataType.STRING, desc = ""),
            @ResponseConfig(name = "value", dataType = ResponseDataType.STRING, desc = "", filterTypes = {FilterType.ESCAPE})
        }
)
public class TestServiceImpl implements Service {

    @Override
    public void execute(Request request, Response response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
