package com.example.queue.fw.concurrent;

import org.springframework.stereotype.Service;

@Service
public interface CheckQueueService {
    void setSysOverload(boolean var1);

    boolean isSysOverload();

    void check() throws Exception;
}
