package com.wolf.framework.service;

import com.wolf.framework.service.response.ListResponse;
import com.wolf.framework.service.request.ListRequest;

/**
 *
 * @author aladdin
 */
public interface ListService {

    public void execute(ListRequest listRequest, ListResponse listResponse);
}
