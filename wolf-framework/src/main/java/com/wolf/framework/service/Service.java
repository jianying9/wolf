package com.wolf.framework.service;

import com.wolf.framework.reponse.Response;
import com.wolf.framework.request.Request;

/**
 *
 * @author aladdin
 */
public interface Service {

    public void execute(Request request, Response response);
}
