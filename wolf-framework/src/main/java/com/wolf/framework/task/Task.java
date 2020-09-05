package com.wolf.framework.task;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.logger.LogFactory;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author aladdin
 */
public abstract class Task implements Runnable {

    private final Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);

    public abstract void doWhenRejected();

    protected abstract void execute();

    @Override
    public final void run() {
        try {
            this.execute();
        } catch (RuntimeException e) {
            this.logger.error(this.getClass().getName(), e);
        }
    }
}
