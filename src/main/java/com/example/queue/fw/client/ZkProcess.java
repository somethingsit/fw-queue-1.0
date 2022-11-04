package com.example.queue.fw.client;

import com.example.queue.fw.utils.DataUtil;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ZkProcess extends Thread implements ZkClient, Watcher {
    private static Logger logger = LoggerFactory.getLogger(ZkProcess.class);
    private ZkClientUtil zkClientUtil = new ZkClientUtil();
    private static String parentNode;
    private String childZNode;
    private String nodeName;
    private String path;
    private long TIME_LOG;
    private long WAIT_DIE_NODE_BEFORE_START;
    private String status = "";
    private static AtomicBoolean processMXStatus = new AtomicBoolean(false);
    private static long lastQueryTime = System.currentTimeMillis();
    private static long diePeriod;

    public static boolean isDie() {
        if (!processMXStatus.get()) {
            return true;
        } else {
            diePeriod = System.currentTimeMillis() - lastQueryTime;
            return diePeriod > ZkClientUtil.getPredictDieTime();
        }
    }

    public static long getDiePeriod() {
        return diePeriod;
    }

    public ZkProcess() {
        parentNode = this.zkClientUtil.getParentNode();
        this.childZNode = this.zkClientUtil.getChildZNode();
        this.TIME_LOG = this.zkClientUtil.getTimeLog();
        this.WAIT_DIE_NODE_BEFORE_START = this.zkClientUtil.getWaitDieNodeBeforeStart();
        this.nodeName = parentNode.concat("/").concat(this.childZNode);
        this.path = "/" + this.nodeName;
    }

    public void run() {
        try {
            logger.info("_____wrapper is processing....");
            this.zkClientUtil.connect(this.zkClientUtil.getZooHost(), this);
            this.zkClientUtil.createParentNode(parentNode, this);
            this.createChildNode(this.zkClientUtil.getClientHost());
            Long nTimeTryConnect = 0L;
            boolean isOverWaitingTime = false;
            boolean showHealthyLog = Boolean.valueOf((String) DataUtil.defaultIfNull(System.getenv("HA_SHOW_HEALTHY_LOG"), "true"));

            while(true) {
                while(true) {
                    while(true) {
                        try {
                            label198: {
                                try {
                                    String zData = this.zkClientUtil.getData(this.nodeName);
                                    boolean isSame = this.zkClientUtil.getClientHost().equals(zData);
                                    if (showHealthyLog || !isSame) {
                                        logger.info("I'm " + this.zkClientUtil.getClientHost() + (isSame ? " same as " : " different with ") + zData + " is processing ...");
                                    }

                                    if (processMXStatus.get() && !this.status.equals("") && !this.status.equals(this.zkClientUtil.getClientHost())) {
                                        this.stopProcess();
                                    }

                                    this.status = zData;
                                    if (!processMXStatus.get() && this.status.equals(this.zkClientUtil.getClientHost())) {
                                        logger.info("_____App restart processMX. Session still alive");
                                        this.createChildNode(this.zkClientUtil.getClientHost());
                                    }

                                    nTimeTryConnect = 0L;
                                    isOverWaitingTime = false;
                                    lastQueryTime = System.currentTimeMillis();
                                    continue;
                                } catch (KeeperException var17) {
                                    logger.error("_____Cat not get stage. Please check network or zookeeper server ", var17);
                                    if (var17.code() != KeeperException.Code.SESSIONEXPIRED && var17.code() != KeeperException.Code.NONODE) {
                                        break label198;
                                    }
                                } catch (InterruptedException var18) {
                                    logger.error("_____Get exception while sleep thread ", var18);
                                    continue;
                                }

                                if (this.status.equals(this.zkClientUtil.getClientHost())) {
                                    logger.info("_____App retry connect success but session is expired");

                                    try {
                                        this.zkClientUtil.connect(this.zkClientUtil.getZooHost(), this);
                                        if (!this.zkClientUtil.isExist(this.nodeName, this)) {
                                            logger.info("_____Restart Active node");
                                            this.createChildNode(this.zkClientUtil.getClientHost());
                                        }
                                    } catch (KeeperException var16) {
                                        logger.error("_____Try to reconnect get still error ", var16);
                                    }
                                    continue;
                                }
                            }

                            if (!isOverWaitingTime) {
                                logger.info("_____Active is dead, Standby waiting " + this.WAIT_DIE_NODE_BEFORE_START + " ms before start");
                                Thread.sleep(this.WAIT_DIE_NODE_BEFORE_START);
                                isOverWaitingTime = true;
                            } else {
                                try {
                                    this.zkClientUtil.connect(this.zkClientUtil.getZooHost(), this);
                                    if (!this.zkClientUtil.isExist(this.nodeName, this) && this.zkClientUtil.isAutoswitch()) {
                                        logger.info("_____Start Standby node");
                                        this.createChildNode(this.zkClientUtil.getClientHost());
                                    }
                                } catch (KeeperException var15) {
                                    logger.error("_____Try to reconnect get still error ", var15);
                                }
                            }
                        } finally {
                            Thread.sleep(this.TIME_LOG);
                        }
                    }
                }
            }
        } catch (KeeperException | InterruptedException var20) {
            logger.error("_____Start function get error:", var20);
        } catch (IOException var21) {
            logger.error("_____Start function get error:", var21);
        }

    }

    public void stop_() {
        this.stopProcess();
        logger.info("_____wrapper is stoping....");
    }

    public void process(WatchedEvent event) {
        if (event.getState() == Event.KeeperState.SyncConnected) {
            logger.info("_____Connected zookeeper service...");
            this.zkClientUtil.getCountDownLatch().countDown();
        }

    }

    public void createChildNode(String data) throws KeeperException, InterruptedException, IOException {
        if (!this.zkClientUtil.isExist(this.nodeName, this)) {
            String createdPath = this.zkClientUtil.getZk().create(this.path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            logger.info("_____Created child node " + createdPath + ":" + this.path + "/" + data);
            if (processMXStatus.get() && !this.status.equals("")) {
                logger.info("_____ProcessMX is still running so framework not start it");
            } else {
                this.startProcess();
                logger.info("_____ProcessMX is started");
            }
        }

    }

    protected abstract void doStart();

    public void startProcess() {
        try {
            logger.info("--------------Try start process--------------");
            processMXStatus.set(true);
            this.status = this.zkClientUtil.getClientHost();
            this.doStart();
            logger.info("--------------All process is started--------------");
        } catch (Exception var2) {
            logger.error("Start processMX get error", var2);
        }

    }

    protected abstract void doStop();

    public void stopProcess() {
        try {
            logger.info("--------------Try stop process--------------");
            this.zkClientUtil.getZk().close();
            processMXStatus.set(false);
            this.doStop();
            logger.info("--------------All process is stoped--------------");
        } catch (Exception var2) {
            logger.error("Stop processMX get error", var2);
            System.exit(-1);
        }

    }
}
