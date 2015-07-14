package com.demo.timer;

import javax.ejb.Local;

/**
 *
 * @author jianying9
 */
@Local
public interface DemoTimerSessionBeanLocal {
    
    public void updateStockMoneyFlowMinute();
    
    public void truncateStockMoneyFlowMinute();
    
    public void updateStockMoneyFlowDay();
    
}
