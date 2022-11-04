package com.example.queue.fw.concurrent;

import java.util.Map;

public interface IQuartzProducer {
    void process() throws Exception;

    void setQueue(IQueue var1);

    String getWorkerName();

    void setWorkerName(String var1);

    int getBatchSize();

    void setBatchSize(int var1);

    void setProperties(Class var1, Map<String, Object> var2) throws Exception;
}
