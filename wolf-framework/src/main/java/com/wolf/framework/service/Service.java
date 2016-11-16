package com.wolf.framework.service;

import com.wolf.framework.service.response.ObjectResponse;
import com.wolf.framework.service.request.ObjectRequest;

/**
 *
 * @author aladdin
 */
public interface Service {

    public void execute(ObjectRequest objectRequest, ObjectResponse objectResponse);
}
