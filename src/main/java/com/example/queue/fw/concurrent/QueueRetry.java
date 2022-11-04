package com.example.queue.fw.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.DelayQueue;

public class QueueRetry<T extends ConcurrentMessage> {
    private static final Logger logger = LoggerFactory.getLogger(QueueRetry.class);
    private DelayQueue<DelayMessage> queue = new DelayQueue();
    private String uniqueName;
    private long delayInterval;
    private int maxRetry;

    public QueueRetry() {
    }

    public boolean enqueue(IQueue originQueue, T message) throws MaxRetryException {
        if (message.getRetryCount() >= this.maxRetry) {
            throw new MaxRetryException("exceed.max.retry");
        } else {
            message.setRetryCount(message.getRetryCount() + 1);
            message.setInTime(new Date(), this.uniqueName);
            boolean result = this.queue.offer(new DelayMessage(originQueue, message, this.delayInterval));
            return result;
        }
    }

    public void start() {
        Thread consumerThread = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    try {
                        DelayMessage delayMsg = (DelayMessage)QueueRetry.this.queue.take();
                        ConcurrentMessage concurrentMessage = delayMsg.getData();
                        IQueue orginQueue = delayMsg.getOriginQueue();
                        concurrentMessage.setOutTime(new Date(), QueueRetry.this.uniqueName);
                        concurrentMessage.setConsumerName("RetryConsumer");
                        if (!orginQueue.enqueue(concurrentMessage)) {
                            QueueRetry.this.queue.offer(delayMsg);
                        }
                    } catch (Exception var4) {
                        QueueRetry.logger.error(String.valueOf(var4));
                    }
                }
            }
        });
        consumerThread.start();
    }

    public void setDelayInterval(long delayInterval) {
        this.delayInterval = delayInterval;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }
}
