package com.wolf.framework.service;

import com.wolf.framework.worker.context.MessageContext;

/**
 *
 * @author aladdin
 */
public interface Service {

    public void execute(MessageContext messageContext);
}
