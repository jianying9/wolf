package com.wolf.framework.interceptor;



import com.wolf.framework.reponse.WorkerResponse;
import com.wolf.framework.request.WorkerRequest;

/**
 *
 * @author jianying9
 */
public interface Interceptor {

    public boolean execute(WorkerRequest workerRequest, WorkerResponse workerResponse);
}
