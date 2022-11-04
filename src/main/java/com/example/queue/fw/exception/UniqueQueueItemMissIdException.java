package com.example.queue.fw.exception;

import com.example.queue.fw.concurrent.ConcurrentMessage;

public class UniqueQueueItemMissIdException extends RuntimeException {
    private ConcurrentMessage item;

    public UniqueQueueItemMissIdException(ConcurrentMessage item) {
        this.item = item;
    }

    public String getMessage() {
        return "MessageId of Item is null:" + this.item.toString();
    }
}
