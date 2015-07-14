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

    @Schedule(minute = "*", second = "0", dayOfMonth = "*", month = "*", year = "*", hour = "[9,14]", dayOfWeek = "{\"Mon\", \"Tue\", \"Wed\", \"Thu\", \"Fri\"}", persistent = false)
    @Override
    public void updateStockFlowMoneyMinute() {
        logger.debug("timer:{}", "/stock/moneyflow/timer/update:minute更新");
        Map<String, String> parameterMap = new HashMap<String, String>(2, 1);
        parameterMap.put("type", "minute");
        String result = this.executeService("/stock/moneyflow/timer/update", parameterMap);
        logger.debug("result:{}", result);
    }

    @Schedule(minute = "0", second = "0", dayOfMonth = "*", month = "*", year = "*", hour = "[9,15]", dayOfWeek = "{\"Mon\", \"Tue\", \"Wed\", \"Thu\", \"Fri\"}", persistent = false)
    @Override
    public void updateStockFlowMoneyHour() {
        logger.debug("timer:{}", "/stock/moneyflow/timer/update:hour更新");
        Map<String, String> parameterMap = new HashMap<String, String>(2, 1);
        parameterMap.put("type", "hour");
        String result = this.executeService("/stock/moneyflow/timer/update", parameterMap);
        logger.debug("result:{}", result);
    }

    @Schedule(minute = "5", second = "0", dayOfMonth = "*", month = "*", year = "*", hour = "15", dayOfWeek = "{\"Mon\", \"Tue\", \"Wed\", \"Thu\", \"Fri\"}", persistent = false)
    @Override
    public void updateStockFlowMoneyDay() {
        logger.debug("timer:{}", "/stock/moneyflow/timer/update:day更新");
        Map<String, String> parameterMap = new HashMap<String, String>(2, 1);
        parameterMap.put("type", "day");
        String result = this.executeService("/stock/moneyflow/timer/update", parameterMap);
        logger.debug("result:{}", result);
    }
}
