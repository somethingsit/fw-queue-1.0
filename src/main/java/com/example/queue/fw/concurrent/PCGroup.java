package com.example.queue.fw.concurrent;

import com.example.queue.fw.utils.DataUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.*;

public class PCGroup<T extends ConcurrentMessage> implements ApplicationContextAware {
    private static int instanceCounter = 1;
    private static final Logger logger = LoggerFactory.getLogger(PCGroup.class);
    private String uniqueName;
    private int numOfProducer;
    private int numOfConsumer;
    private long producerSleepInterval;
    private String cronExpression;
    private int producerBatchSize;
    private int consumerBatchSize;
    private Map<String, Object> contextParamMap;
    private IQueue<T> queue;
    private QueueRetry<T> queueRetry;
    private Class clazzProducer;
    private Class clazzConsumer;
    private List<IWorker> producerLst;
    private List<IWorker> consumerLst;
    private ApplicationContext context;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public PCGroup() {
    }

    public void init() throws Exception {
        try {
            this.uniqueName = "PC" + instanceCounter++ + "_" + (String) DataUtil.defaultIfNull(this.uniqueName, "UNQ");
            logger.info("[" + this.toString() + "]: START initialize");
            this.producerLst = new ArrayList();
            this.consumerLst = new ArrayList();
            boolean activeStandByMode = Arrays.stream(this.context.getEnvironment().getActiveProfiles()).anyMatch((x) -> {
                return DataUtil.safeEqual(x, ApplicationContextProvider.HAMode.HA_ACTIVE_STANDBY.toString());
            });
            int i;
            IWorker consumer;
            if (!DataUtil.isNullOrEmpty(this.cronExpression)) {
                IQuartzProducer producer = (IQuartzProducer)this.context.getBean(DataUtil.lowerFirstChar(this.clazzProducer.getSimpleName()));
                producer.setQueue(this.queue);
                producer.setWorkerName(this.clazzProducer.getSimpleName());
                producer.setBatchSize(this.producerBatchSize);
                producer.setProperties(this.clazzProducer, this.contextParamMap);
                Map jobDataMap = new HashMap();
                jobDataMap.put("activeStandbyMode", activeStandByMode);
                jobDataMap.put("producer", producer);
                JobDetail jobDetail = JobBuilder.newJob().withIdentity("JOB_" + this.clazzProducer.getSimpleName(), this.uniqueName).ofType(QuartzProducerRunner.class).setJobData(new JobDataMap(jobDataMap)).build();
                Trigger trigger = TriggerBuilder.newTrigger().withIdentity("TR_" + this.clazzProducer.getSimpleName(), this.uniqueName).startNow().withSchedule(CronScheduleBuilder.cronSchedule(this.cronExpression)).build();
                SchedulerFactoryBean schedulerFactoryBean = (SchedulerFactoryBean)this.context.getBean(SchedulerFactoryBean.class);
                schedulerFactoryBean.getObject().scheduleJob(jobDetail, trigger);
            } else {
                for(i = 0; i < this.numOfProducer; ++i) {
                    consumer = (IWorker)this.context.getBean(DataUtil.lowerFirstChar(this.clazzProducer.getSimpleName()));
                    consumer.setActiveStandbyMode(activeStandByMode);
                    consumer.setWorkerName(this.clazzProducer.getSimpleName() + "-" + (i + 1));
                    consumer.setSleepInterval(this.producerSleepInterval);
                    consumer.setBatchSize(this.producerBatchSize);
                    consumer.setQueue(this.queue);
                    consumer.setProperties(this.clazzProducer, this.contextParamMap);
                    consumer.init();
                    consumer.start();
                    this.producerLst.add(consumer);
                }
            }

            for(i = 0; i < this.numOfConsumer; ++i) {
                consumer = (IWorker)this.context.getBean(DataUtil.lowerFirstChar(this.clazzConsumer.getSimpleName()));
                consumer.setActiveStandbyMode(activeStandByMode);
                consumer.setWorkerName(this.clazzConsumer.getSimpleName() + "-" + (i + 1));
                consumer.setQueue(this.queue);
                consumer.setBatchSize(this.consumerBatchSize);
                consumer.setProperties(this.clazzConsumer, this.contextParamMap);
                consumer.init();
                consumer.start();
                this.consumerLst.add(consumer);
            }

            logger.info("[" + this.toString() + "]: END initialize");
        } catch (Exception var7) {
            logger.error(var7.getMessage(), var7);
            System.out.println(ExceptionUtils.getStackTrace(var7));
            Thread.sleep(1000L);
            System.exit(-1);
        }

    }

    public void setProducerSleepInterval(long producerSleepInterval) {
        this.producerSleepInterval = producerSleepInterval;
    }

    public void setNumOfProducer(int numOfProducer) {
        this.numOfProducer = numOfProducer;
    }

    public void setNumOfConsumer(int numOfConsumer) {
        this.numOfConsumer = numOfConsumer;
    }

    public void setProducerBatchSize(int producerBatchSize) {
        this.producerBatchSize = producerBatchSize;
    }

    public void setConsumerBatchSize(int consumerBatchSize) {
        this.consumerBatchSize = consumerBatchSize;
    }

    public void setClazzProducer(Class clazzProducer) {
        this.clazzProducer = clazzProducer;
    }

    public void setClazzConsumer(Class clazzConsumer) {
        this.clazzConsumer = clazzConsumer;
    }

    public void setQueue(IQueue<T> queue) {
        this.queue = queue;
    }

    public void setQueueRetry(QueueRetry<T> queueRetry) {
        this.queueRetry = queueRetry;
    }

    public void setContextParamMap(Map<String, Object> contextParamMap) {
        this.contextParamMap = contextParamMap;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("PCGroup[");
        sb.append("uniqueName=").append(this.uniqueName);
        sb.append("numOfProducer=").append(this.numOfProducer);
        sb.append(", numOfConsumer=").append(this.numOfConsumer);
        sb.append(", producerSleepInterval=").append(this.producerSleepInterval);
        sb.append(", cronExpression=").append(this.cronExpression);
        sb.append(", producerBatchSize=").append(this.producerBatchSize);
        sb.append(", consumerBatchSize=").append(this.consumerBatchSize);
        sb.append(", contextParamMap=").append(this.contextParamMap);
        sb.append(", queue").append(this.queue != null ? this.queue.toString() : "");
        sb.append(", clazzProducer=").append(this.clazzProducer);
        sb.append(", clazzConsumer=").append(this.clazzConsumer);
        sb.append(']');
        return sb.toString();
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }
}
