package com.wolf.framework.service;

import com.wolf.framework.service.request.ServiceRequest;
import com.wolf.framework.service.response.ServiceResponse;

/**
 *
 * @author aladdin
 */
public interface Service {

    public void execute(ServiceRequest serviceRequest, ServiceResponse serviceResponse);
}
