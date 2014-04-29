package com.wolf.framework.task;

/**
 *
 * @author aladdin
 */
public abstract class Task implements Runnable {

    public abstract void doWhenRejected();

    protected abstract void execute();

    @Override
    public final void run() {
        try {
            this.execute();
        } catch (RuntimeException e) {
            System.err.println(e);
        }
    }
}
