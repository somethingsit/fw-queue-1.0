package com.example.queue.fw.concurrent;

import com.example.queue.fw.exception.LogicException;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;

public abstract class Consumer<T extends ConcurrentMessage> extends Worker {
    private static final org.apache.log4j.Logger logger = Logger.getLogger(Consumer.class);
    protected QueueRetry queueRetry;
    private T message;

    public Consumer() {
    }

    public void process() throws Exception {
        this.message = (T) this.queue.dequeue();
        this.logStart();
        this.message.setConsumerName(this.getWorkerName());

        try {
            this.process(this.message);
        } finally {
            this.logEnd();
        }

    }

    public abstract void process(T var1) throws Exception;

    public void markComplete(T message) {
        if (this.queue instanceof UniqueQueue) {
            ((UniqueQueue)this.queue).complete(message);
        }

    }

    public boolean enqueueRetry(T message) throws Exception {
        if (this.queueRetry == null) {
            throw new LogicException("CER001", "retry.queue.not.set", new Object[0]);
        } else {
            return this.queueRetry.enqueue(this.queue, message);
        }
    }

    public boolean pushBack(T message) {
        return this.queue.enqueue(message);
    }

    public List<T> drain(int maxElement) {
        return this.queue.drain(maxElement);
    }

    public void logStart() {
        LanguageBundleUtil.getAndResetReqId((String)null);

        try {
            if (this.message != null) {
                this.message.setInWorkerTime(new Date(), this.getWorkerName());
            }
        } catch (Exception var2) {
        }

    }

    public void logEnd() {
        try {
            if (this.message != null) {
                this.message.setOutWorkerTime(new Date(), this.getWorkerName());
            }
        } catch (Exception var2) {
        }

    }

    public void setQueueRetry(QueueRetry queueRetry) {
        this.queueRetry = queueRetry;
    }
}
