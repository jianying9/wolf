package com.demo.timer;

import com.demo.config.LoggerType;
import com.wolf.framework.logger.LogFactory;
import com.wolf.framework.timer.AbstractTimer;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Schedule;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import org.slf4j.Logger;

/**
 *
 * @author jianying9
 */
@Stateless
@Startup
public class DemoTimerSessionBean extends AbstractTimer implements DemoTimerSessionBeanLocal{
    
    Logger logger = LogFactory.getLogger(LoggerType.TIMER);
    
    @Schedule(minute = "25-59", second = "0", dayOfMonth = "*", month = "*", year = "*", hour = "9", dayOfWeek = "Mon,Tue,Wed,Thu,Fri", persistent = false)
    @Override
    public void updateStockMoneyFlowMinuteOne() {
        logger.debug("timer:{}", "/stock/moneyflow/timer/update:minute更新");
        Map<String, String> parameterMap = new HashMap<String, String>(2, 1);
        parameterMap.put("type", "minute");
        String result = this.executeService("/stock/moneyflow/timer/update", parameterMap);
        logger.debug("result:{}", result);
    }
    
    @Schedule(minute = "*", second = "0", dayOfMonth = "*", month = "*", year = "*", hour = "10", dayOfWeek = "Mon,Tue,Wed,Thu,Fri", persistent = false)
    @Override
    public void updateStockMoneyFlowMinuteTwo() {
        logger.debug("timer:{}", "/stock/moneyflow/timer/update:minute更新");
        Map<String, String> parameterMap = new HashMap<String, String>(2, 1);
        parameterMap.put("type", "minute");
        String result = this.executeService("/stock/moneyflow/timer/update", parameterMap);
        logger.debug("result:{}", result);
    }
    
    @Schedule(minute = "0-30", second = "0", dayOfMonth = "*", month = "*", year = "*", hour = "11", dayOfWeek = "Mon,Tue,Wed,Thu,Fri", persistent = false)
    @Override
    public void updateStockMoneyFlowMinuteThree() {
        logger.debug("timer:{}", "/stock/moneyflow/timer/update:minute更新");
        Map<String, String> parameterMap = new HashMap<String, String>(2, 1);
        parameterMap.put("type", "minute");
        String result = this.executeService("/stock/moneyflow/timer/update", parameterMap);
        logger.debug("result:{}", result);
    }

    @Schedule(minute = "*", second = "0", dayOfMonth = "*", month = "*", year = "*", hour = "13-14", dayOfWeek = "Mon,Tue,Wed,Thu,Fri", persistent = false)
    @Override
    public void updateStockMoneyFlowMinuteFour() {
        logger.debug("timer:{}", "/stock/moneyflow/timer/update:minute更新");
        Map<String, String> parameterMap = new HashMap<String, String>(2, 1);
        parameterMap.put("type", "minute");
        String result = this.executeService("/stock/moneyflow/timer/update", parameterMap);
        logger.debug("result:{}", result);
    }
    
    @Schedule(minute = "55", second = "0", dayOfMonth = "*", month = "*", year = "*", hour = "8", dayOfWeek = "*", persistent = false)
    @Override
    public void truncateStockMoneyFlowMinute() {
        logger.debug("timer:{}", "/stock/moneyflow/timer/truncate:minute更新");
        Map<String, String> parameterMap = new HashMap<String, String>(2, 1);
        String result = this.executeService("/stock/moneyflow/minute/timer/truncate", parameterMap);
        logger.debug("result:{}", result);
    }

    @Schedule(minute = "5", second = "0", dayOfMonth = "*", month = "*", year = "*", hour = "15", dayOfWeek = "Mon,Tue,Wed,Thu,Fri", persistent = false)
    @Override
    public void updateStockMoneyFlowDay() {
        logger.debug("timer:{}", "/stock/moneyflow/timer/update:day更新");
        Map<String, String> parameterMap = new HashMap<String, String>(2, 1);
        parameterMap.put("type", "day");
        String result = this.executeService("/stock/moneyflow/timer/update", parameterMap);
        logger.debug("result:{}", result);
    }
}
