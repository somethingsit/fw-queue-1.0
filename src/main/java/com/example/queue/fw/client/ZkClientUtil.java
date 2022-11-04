package com.example.queue.fw.client;

import com.example.queue.fw.utils.DataUtil;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class ZkClientUtil {
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private static Logger logger = LoggerFactory.getLogger(ZkClientUtil.class);
    private String zooHost;
    private String clientHost;
    private String parentNode;
    private String childZNode;
    private boolean autoswitch;
    private long timeLog;
    private long waitDieNodeBeforeStart;
    private int nTimeTryConnect;
    private static long predictDieTime;
    private ZooKeeper zk;

    public CountDownLatch getCountDownLatch() {
        return this.countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public int getnTimeTryConnect() {
        return this.nTimeTryConnect;
    }

    public void setnTimeTryConnect(int nTimeTryConnect) {
        this.nTimeTryConnect = nTimeTryConnect;
    }

    public String getZooHost() {
        return this.zooHost;
    }

    public void setZooHost(String zooHost) {
        this.zooHost = zooHost;
    }

    public String getClientHost() {
        return this.clientHost;
    }

    public void setClientHost(String clientHost) {
        this.clientHost = clientHost;
    }

    public String getParentNode() {
        return this.parentNode;
    }

    public String getChildZNode() {
        return this.childZNode;
    }

    public ZooKeeper getZk() {
        return this.zk;
    }

    public boolean isAutoswitch() {
        return this.autoswitch;
    }

    public void setAutoswitch(boolean autoswitch) {
        this.autoswitch = autoswitch;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public long getTimeLog() {
        return this.timeLog;
    }

    public static long getPredictDieTime() {
        return predictDieTime;
    }

    public long getWaitDieNodeBeforeStart() {
        return this.waitDieNodeBeforeStart;
    }

    public void setWaitDieNodeBeforeStart(long waitDieNodeBeforeStart) {
        this.waitDieNodeBeforeStart = waitDieNodeBeforeStart;
    }

    public ZkClientUtil() {
        try {
            DataUtil.assertNotNullEnvVariable("SERVER_NAME");
            DataUtil.assertNotNullEnvVariable("HA_PARENT_NODE");
            DataUtil.assertNotNullEnvVariable("HA_CHILD_NODE");
            Properties prop = new Properties();
            prop.load(new FileInputStream("../resources/zk.properties"));
            this.zooHost = prop.getProperty("zooHost");
            this.clientHost = System.getenv("SERVER_NAME") + "-" + UUID.randomUUID().toString();
            this.parentNode = System.getenv("HA_PARENT_NODE");
            this.childZNode = System.getenv("HA_CHILD_NODE");
            this.autoswitch = Boolean.valueOf(prop.getProperty("autoSwitch"));
            this.timeLog = Long.valueOf(prop.getProperty("timeLog"));
            this.nTimeTryConnect = Integer.valueOf(prop.getProperty("nTimeTryConnect"));
            predictDieTime = Long.valueOf(prop.getProperty("predictDieTime"));
            if (predictDieTime < this.timeLog + 1000L) {
                predictDieTime = this.timeLog + 1000L;
            }

            this.waitDieNodeBeforeStart = Long.valueOf(prop.getProperty("waitDieNodeBeforeStart"));
        } catch (Exception var2) {
            logger.error("Read zk.properties get error: " + var2);
        }

    }

    public void connect(String host, Watcher watcher) throws InterruptedException, IOException {
        this.zk = new ZooKeeper(host, 5000, watcher);
        this.countDownLatch.await();
    }

    public void createParentNode(String groupName, Watcher watcher) throws KeeperException, InterruptedException {
        String path = "/" + groupName;
        if (!this.isExist(groupName, watcher)) {
            String createdPath = this.zk.create(path, (byte[])null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            logger.info("Created parent node " + createdPath);
        }

    }

    public void close() throws InterruptedException {
        this.zk.close();
    }

    public String getData(String zNodeName) throws KeeperException, InterruptedException {
        String path = "/" + zNodeName;
        String zData = new String(this.zk.getData(path, false, (Stat)null));
        return zData;
    }

    public boolean isExist(String zNodeName, Watcher watcher) {
        String path = "/" + zNodeName;

        try {
            return this.zk.exists(path, watcher) != null;
        } catch (Exception var5) {
            logger.error("Check zoo node path get error", var5);
            return false;
        }
    }
}
