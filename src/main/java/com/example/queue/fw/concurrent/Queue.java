package com.example.queue.fw.concurrent;

import brave.Span;
import brave.Tracing;
import com.example.queue.fw.utils.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Queue<T extends ConcurrentMessage> implements IQueue<T>, InitializingBean {
    public static ThreadLocal<Span> spanGroupPcContext = new ThreadLocal();
    private static Logger logger;
    private BlockingQueue<T> queue;
    private String uniqueName;
    private Integer warningPercent;
    private Integer overloadPercent;
    private Long remainOverDuration;
    private Integer capacity;
    private boolean joinSysOverload;
    private boolean cannotEnqueue;
    private boolean enableTraceLog = true;
    private String serverName;

    public Queue() {
    }

    @PostConstruct
    public void init() {
    }

    public boolean contain(ConcurrentMessage message) {
        throw new UnsupportedOperationException();
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.uniqueName, "uniqueName must be set");
        Assert.notNull(this.capacity, "capacity must be set");
        this.serverName = (String) DataUtil.defaultIfNull(System.getenv("SERVER_NAME"), "");
        logger = LoggerFactory.getLogger(this.uniqueName);
        this.queue = new ArrayBlockingQueue(this.capacity);
    }

    public boolean enqueue(T message) {
        message.setInTime(new Date(), this.uniqueName);
        boolean result = this.queue.offer(message);
        if (message.getKpiId() == null) {
            message.setKpiId(LanguageBundleUtil.getReqId());
        }

        this.cannotEnqueue = !result;
        if (result) {
            if (this.enableTraceLog) {
                logger.info("Entering|" + this.uniqueName + ".enqueue(" + this.queue.size() + ")[" + message.getMessageId() + "][" + message.getTraceInfo() + "]|0||");
            }
        } else if (this.joinSysOverload) {
            logger.error(this.toString() + " can not enqueue");
        }

        return result;
    }

    public T dequeue() throws InterruptedException {
        T message = (T) this.queue.take();
        message.setOutTime(new Date(), this.uniqueName);
        if (message.getSpan() != null) {
            spanGroupPcContext.set(message.getSpan());
        }

        if (this.enableTraceLog) {
            logger.info("Leaving|" + this.uniqueName + ".dequeue(" + this.queue.size() + ")[" + message.getMessageId() + "]|" + message.getRemainTime() + "||");
        }

        return message;
    }

    public List<T> drain(int maxElement) {
        List<T> lst = new ArrayList();
        this.queue.drainTo(lst, maxElement);
        Iterator var3 = lst.iterator();

        while(var3.hasNext()) {
            T msg = (T) var3.next();
            msg.setOutTime(new Date(), this.uniqueName);
        }

        return lst;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public List<ConcurrentMessage> getTopRemain(int limit) {
        List topRemainLst = new ArrayList();
        if (DataUtil.isNullOrZero(this.remainOverDuration)) {
            return topRemainLst;
        } else {
            Iterator iterator = this.queue.iterator();
            int var4 = 0;

            while(iterator.hasNext() && var4++ < limit) {
                ConcurrentMessage msg = (ConcurrentMessage)iterator.next();
                if (msg.isRemainOver(this.remainOverDuration)) {
                    topRemainLst.add(msg);
                }
            }

            return topRemainLst;
        }
    }

    public List<T> getAllRemain() {
        List remainLst = new ArrayList();
        Iterator iterator = this.queue.iterator();
        boolean var3 = false;

        while(iterator.hasNext()) {
            ConcurrentMessage msg = (ConcurrentMessage)iterator.next();
            remainLst.add(msg);
        }

        return remainLst;
    }

    public boolean isOverload() {
        if (this.overloadPercent == null) {
            return false;
        } else {
            return 100 * this.queue.size() / this.capacity > this.overloadPercent;
        }
    }

    public boolean isWarning() {
        if (this.warningPercent == null) {
            return false;
        } else {
            return 100 * this.queue.size() / this.capacity > this.warningPercent;
        }
    }

    public void setRemainOverDuration(Long remainOverDuration) {
        this.remainOverDuration = remainOverDuration;
    }

    public void setWarningPercent(Integer warningPercent) {
        this.warningPercent = warningPercent;
    }

    public void setOverloadPercent(Integer overloadPercent) {
        this.overloadPercent = overloadPercent;
    }

    public boolean isJoinSysOverload() {
        return this.joinSysOverload;
    }

    public void setJoinSysOverload(boolean joinSysOverload) {
        this.joinSysOverload = joinSysOverload;
    }

    public void setEnableTraceLog(boolean enableTraceLog) {
        this.enableTraceLog = enableTraceLog;
    }

    public String getUniqueName() {
        return this.uniqueName;
    }

    public String toString() {
        return this.serverName + " Queue{name:" + this.uniqueName + ",warningPercent:" + this.warningPercent + ",overloadPercent:" + this.overloadPercent + ",capacity:" + this.capacity + ",size:" + this.queue.size() + '}';
    }
}
