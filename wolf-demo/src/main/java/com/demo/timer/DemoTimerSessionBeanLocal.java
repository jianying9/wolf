package com.demo.timer;

import javax.ejb.Local;

/**
 *
 * @author jianying9
 */
@Local
public interface DemoTimerSessionBeanLocal {
    
    public void updateStockMoneyFlowMinuteOne();
    
    public void updateStockMoneyFlowMinuteTwo();
    
    public void updateStockMoneyFlowMinuteThree();
    
    public void updateStockMoneyFlowMinuteFour();
    
    public void truncateStockMoneyFlowMinute();
    
    public void updateStockMoneyFlowDay();
    
}
