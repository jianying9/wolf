package com.wolf.framework.comet;

import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.injecter.Injecter;
import com.wolf.framework.logger.LogFactory;
import org.slf4j.Logger;

/**
 *
 * @author jianying9
 */
public class LeaveEventHandleBuilder {
    
    private final Injecter injecter;
    
    private final Class<LeaveEventHandler> clazz;
    
    private final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);

    public LeaveEventHandleBuilder(Injecter injecter, Class<LeaveEventHandler> clazz) {
        this.injecter = injecter;
        this.clazz = clazz;
    }
    
    public LeaveEventHandler build() {
        this.logger.debug("--parsing leave event handler {}--", clazz.getName());
        LeaveEventHandler leaveEventHandler;
        try {
            leaveEventHandler = this.clazz.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Error when instancing class ".concat(clazz.getName()));
        }
        this.injecter.parse(leaveEventHandler);
        return leaveEventHandler;
    }
}
