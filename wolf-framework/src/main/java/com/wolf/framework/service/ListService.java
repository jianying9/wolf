package com.wolf.framework.service;

import com.wolf.framework.service.request.ListServiceRequest;
import com.wolf.framework.service.response.ListServiceResponse;

/**
 *
 * @author aladdin
 */
public interface ListService {

    public void execute(ListServiceRequest listServiceRequest, ListServiceResponse listServiceResponse);
}
