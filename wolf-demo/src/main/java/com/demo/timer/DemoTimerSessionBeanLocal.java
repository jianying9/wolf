package com.demo.timer;

import javax.ejb.Local;

/**
 *
 * @author jianying9
 */
@Local
public interface DemoTimerSessionBeanLocal {
    
    public void updateStockFlowMoneyMinute();
    
    public void updateStockFlowMoneyHour();
    
    public void updateStockFlowMoneyDay();
}
