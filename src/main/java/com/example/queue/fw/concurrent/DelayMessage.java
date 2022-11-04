package com.example.queue.fw.concurrent;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayMessage implements Delayed {
    private IQueue originQueue;
    private ConcurrentMessage data;
    private long startTime;

    public DelayMessage(IQueue originQueue, ConcurrentMessage data, long delay) {
        this.originQueue = originQueue;
        this.data = data;
        this.startTime = System.currentTimeMillis() + delay;
    }

    public IQueue getOriginQueue() {
        return this.originQueue;
    }

    public ConcurrentMessage getData() {
        return this.data;
    }

    public long getDelay(TimeUnit unit) {
        long diff = this.startTime - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    public int compareTo(Delayed o) {
        if (this.startTime < ((DelayMessage)o).startTime) {
            return -1;
        } else {
            return this.startTime > ((DelayMessage)o).startTime ? 1 : 0;
        }
    }

    public String toString() {
        return "{data='" + this.data + '\'' + ", startTime=" + this.startTime + '}';
    }
}
