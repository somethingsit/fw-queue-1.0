package com.example.queue.fw.concurrent;

public class UniqueQueueItemException extends RuntimeException {
    private ConcurrentMessage item;

    public UniqueQueueItemException(ConcurrentMessage item) {
        this.item = item;
    }

    public String getMessage() {
        return "Duplicate item in UniqueQueue with messageId:" + this.item.getMessageId() + ",data:" + this.item.getTraceInfo();
    }
}
