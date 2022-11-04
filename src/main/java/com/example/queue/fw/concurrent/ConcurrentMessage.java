package com.example.queue.fw.concurrent;

import brave.Span;

import java.io.Serializable;
import java.util.Date;

public abstract class ConcurrentMessage implements Serializable {
    private static final String LOG_DATE_PATTERN = "HH:mm:ss";
    private Date inTime;
    private Date outTime;
    private String producerName;
    private String consumerName;
    private int retryCount;
    private String kpiId;
    private Span span;

    public ConcurrentMessage() {
    }

    public void setInWorkerTime(Date date, String workerName) {
    }

    public void setOutWorkerTime(Date outTime, String workerName) {
    }

    public void setInTime(Date inTime, String queueName) {
        this.inTime = inTime;
    }

    public void setOutTime(Date outTime, String queueName) {
        this.outTime = outTime;
    }

    public String getProducerName() {
        return this.producerName;
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }

    public String getConsumerName() {
        return this.consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public int getRetryCount() {
        return this.retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public String getKpiId() {
        return this.kpiId;
    }

    public void setKpiId(String kpiId) {
        this.kpiId = kpiId;
    }

    public abstract String getMessageId();

    public abstract String getTraceInfo();

    public boolean isRemainOver(Long duration) {
        return System.currentTimeMillis() - this.inTime.getTime() > duration;
    }

    public long getRemainTime() {
        return System.currentTimeMillis() - this.inTime.getTime();
    }

    public String toString() {
        long processTime = this.inTime != null && this.outTime != null ? this.outTime.getTime() - this.inTime.getTime() : 0L;
        return "Message[" + this.queueHistory() + ", pName='" + this.producerName + '\'' + ", cName='" + this.consumerName + '\'' + ", processTime=" + processTime + ", retryCount=" + this.retryCount + ']';
    }

    private String queueHistory() {
        try {
            String info = "queues[";
            info = info + "]";
            return info;
        } catch (Exception var2) {
            return "";
        }
    }

    public Span getSpan() {
        return this.span;
    }

    public void setSpan(Span span) {
        this.span = span;
    }
}
