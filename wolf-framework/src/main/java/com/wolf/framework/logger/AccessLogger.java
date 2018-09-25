package com.wolf.framework.logger;

/**
 *
 * @author aladdin
 */
public interface AccessLogger {

    public void log(String route, String sid, String request, String response);

    public void log(String sid, String type, String operate);

}
