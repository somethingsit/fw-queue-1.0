package com.example.queue.fw.concurrent;

import com.example.queue.fw.utils.DataUtil;
import com.example.queue.fw.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DefaulCheckQueueServiceImpl implements CheckQueueService {
    private static final Logger logger = LoggerFactory.getLogger(DefaulCheckQueueServiceImpl.class);
    private boolean sysOverload;

    public DefaulCheckQueueServiceImpl() {
    }

    public synchronized void setSysOverload(boolean overload) {
        this.sysOverload = overload;
    }

    public synchronized boolean isSysOverload() {
        return this.sysOverload;
    }

    public Map<String, Queue> getQueueMap() {
        return ApplicationContextProvider.getApplicationContext().getBeansOfType(Queue.class);
    }

    public void check() throws Exception {
        boolean anyQueueOverload = false;
        Map<String, Queue> queueMap = ApplicationContextProvider.getApplicationContext().getBeansOfType(Queue.class);
        Iterator var3 = queueMap.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<String, Queue> entry = (Map.Entry)var3.next();
            Queue queue = (Queue)entry.getValue();
            String message = null;
            if (queue.isOverload()) {
                message = queue.toString() + " is overload at " + DateUtil.dateTime2String(new Date());
                if (queue.isJoinSysOverload()) {
                    anyQueueOverload = true;
                }
            } else if (queue.isWarning()) {
                message = queue.toString() + " is high concurrent at " + DateUtil.dateTime2String(new Date());
                logger.warn(message);
            }

            List<ConcurrentMessage> remainLst = queue.getTopRemain(5);
            if (!remainLst.isEmpty()) {
                message = (DataUtil.isNullOrEmpty(message) ? queue.toString() : "") + " remain over items at";
                logger.warn(message);
            }
        }

        this.setSysOverload(anyQueueOverload);
    }
}
