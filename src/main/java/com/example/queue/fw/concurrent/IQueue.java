package com.example.queue.fw.concurrent;

import java.util.List;

public interface IQueue<T extends  ConcurrentMessage> {
    boolean enqueue(T var1);

    T dequeue() throws InterruptedException;

    List<T> drain(int var1);

    boolean contain(T var1);

    boolean isOverload();

    List<T> getAllRemain();
}
