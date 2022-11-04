package com.example.queue.fw.concurrent;

import com.example.queue.fw.client.ZkProcess;
import com.example.queue.fw.service.SystemConfigService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
public class QuartzProducerRunner extends QuartzJobBean implements Job {
    private static final Logger logger = LoggerFactory.getLogger(Worker.class);
    private boolean activeStandbyMode;
    private IQuartzProducer producer;

    public QuartzProducerRunner() {
    }

    protected void executeInternal(JobExecutionContext ctx) throws JobExecutionException {
        if (SystemConfigService.isEnableAllProducer()) {
            if (this.activeStandbyMode) {
                if (!ZkProcess.isDie()) {
                    this.process();
                }
            } else {
                this.process();
            }
        }

    }

    private void process() {
        try {
            LanguageBundleUtil.getAndResetReqId((String)null);
            this.producer.process();
        } catch (Exception var2) {
            logger.info(this.producer.getWorkerName() + " EXCEPTION: " + var2.getMessage());
            logger.error(var2.getMessage(), var2);
        }

    }

    public void setProducer(IQuartzProducer producer) {
        this.producer = producer;
    }

    public void setActiveStandbyMode(boolean b) {
        this.activeStandbyMode = b;
    }
}
