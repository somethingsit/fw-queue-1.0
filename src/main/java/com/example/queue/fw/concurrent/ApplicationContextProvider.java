package com.example.queue.fw.concurrent;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;


@Service
public class ApplicationContextProvider implements ApplicationContextAware {
    private static ApplicationContext context;

    public ApplicationContextProvider() {
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public void setApplicationContext(ApplicationContext ctx) {
        context = ctx;
    }

    public static enum HAMode {
        HA_ACTIVE_ACTIVE,
        HA_ACTIVE_STANDBY;

        private HAMode() {
        }
    }
}
