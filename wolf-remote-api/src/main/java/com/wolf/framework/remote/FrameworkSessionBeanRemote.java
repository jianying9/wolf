package com.wolf.framework.remote;

import java.util.Map;
import javax.ejb.Remote;

/**
 *
 * @author aladdin
 */
@Remote
public interface FrameworkSessionBeanRemote {

    public String execute(String act, Map<String, String> parameterMap);
}
