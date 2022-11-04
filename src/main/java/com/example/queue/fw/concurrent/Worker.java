package com.example.queue.fw.concurrent;

import com.example.queue.fw.client.ZkProcess;
import com.example.queue.fw.utils.DataUtil;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public abstract class Worker extends Thread implements IWorker {
    private static final Logger logger = LoggerFactory.getLogger(Worker.class);
    private String workerName;
    protected ApplicationContext context;
    protected IQueue queue;
    private long sleepInterval = 0L;
    private int batchSize;
    private Map<String, Object> contextParamMap;
    private boolean activeStandbyMode;

    public Worker() {
    }

    public void init() throws Exception {
    }

    public void run() {
        String serverName = (String) DataUtil.defaultIfNull(System.getenv("SERVER_NAME"), "WORKER");
        ThreadContext.put("server.name", serverName);
        this.setName(this.getWorkerName());

        while(true) {
            while(true) {
                try {
                    if (this.activeStandbyMode) {
                        if (!ZkProcess.isDie()) {
                            this.process();
                        }
                    } else {
                        this.process();
                    }
                } catch (Throwable var11) {
                    logger.error(var11.getMessage(), var11);
                } finally {
                    if (this.sleepInterval > 0L) {
                        try {
                            Thread.sleep(this.sleepInterval);
                        } catch (Exception var10) {
                            logger.error(var10.getMessage(), var10);
                        }
                    }

                }
            }
        }
    }

    public abstract void logStart();

    public abstract void logEnd();

    public void setSleepInterval(long sleepInterval) {
        this.sleepInterval = sleepInterval;
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setQueue(IQueue queue) {
        this.queue = queue;
    }

    public Map<String, Object> getContextParamMap() {
        return this.contextParamMap;
    }

    public String getWorkerName() {
        return this.workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public void setActiveStandbyMode(boolean activeStandbyMode) {
        this.activeStandbyMode = activeStandbyMode;
    }

    public void setProperties(Class subClass, Map<String, Object> contextParamMap) throws Exception {
        this.contextParamMap = contextParamMap;
        DataUtil.setProperties(this, subClass, contextParamMap);
    }

}
