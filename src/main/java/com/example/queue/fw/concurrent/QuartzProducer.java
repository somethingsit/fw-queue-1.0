package com.example.queue.fw.concurrent;

import com.example.queue.fw.utils.DataUtil;
import org.apache.log4j.Logger;

import java.util.Map;

public abstract class QuartzProducer<T extends ConcurrentMessage> implements IQuartzProducer  {
    private static final Logger logger = Logger.getLogger(QuartzProducer.class);
    protected IQueue queue;
    private String workerName;
    private int batchSize;

    public QuartzProducer() {
    }

    public abstract void process() throws Exception;

    public boolean enqueue(T msg) {
        msg.setProducerName(this.getWorkerName());
        return this.queue.enqueue(msg);
    }

    public void setQueue(IQueue queue) {
        this.queue = queue;
    }

    public String getWorkerName() {
        return this.workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setProperties(Class subClass, Map<String, Object> contextParamMap) throws Exception {
        DataUtil.setProperties(this, subClass, contextParamMap);
    }
}
