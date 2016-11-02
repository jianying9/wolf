package com.wolf.framework.doc;

import com.wolf.framework.data.DataType;
import com.wolf.framework.service.ListService;
import com.wolf.framework.service.ServiceConfig;
import com.wolf.framework.service.parameter.RequestConfig;
import com.wolf.framework.service.parameter.ResponseConfig;
import com.wolf.framework.service.parameter.filter.FilterType;
import com.wolf.framework.service.request.ListServiceRequest;
import com.wolf.framework.service.response.ListServiceResponse;

/**
 *
 * @author jianying9
 */
@ServiceConfig(
        route = "/doc/test/list",
        validateSession = false,
        desc = "list service类型测试服务",
        responseCodes = {},
        requestConfigs = {
            @RequestConfig(name = "name", dataType = DataType.STRING, desc = "名称", max = 28, min = 2, required = true),
            @RequestConfig(name = "value", dataType = DataType.STRING, desc = "值", max = 128, min = 0, required = false)
        },
        responseConfigs = {
            @ResponseConfig(name = "name", dataType = DataType.STRING, desc = ""),
            @ResponseConfig(name = "value", dataType = DataType.STRING, desc = "", filterTypes = {FilterType.ESCAPE})
        }
)
public class TestListServiceImpl implements ListService {

    @Override
    public void execute(ListServiceRequest listServiceRequest, ListServiceResponse listServiceResponse) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
