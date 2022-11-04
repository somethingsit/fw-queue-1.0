package com.example.queue.fw.concurrent;

import java.util.Map;

public interface IWorker {
    void setActiveStandbyMode(boolean var1);

    void setWorkerName(String var1);

    String getWorkerName();

    void process() throws Exception;

    void start();

    void setQueue(IQueue var1);

    void setSleepInterval(long var1);

    void setBatchSize(int var1);

    Map<String, Object> getContextParamMap();

    void init() throws Exception;

    void setProperties(Class var1, Map<String, Object> var2) throws Exception;
}
