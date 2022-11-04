package com.example.queue.fw.concurrent;

public class MaxRetryException extends Exception {
    public MaxRetryException(String message) {
        super(message);
    }
}
