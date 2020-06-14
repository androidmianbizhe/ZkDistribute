package org.example.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class DistributeLock implements Lock, Watcher {

    private ZooKeeper zk = null;
    private String ROOT_LOCK = "/locks";
    private String WAIT_LOCK; //等待前一个锁
    private String CURRENT_LOCK;

    private CountDownLatch countDownLatch;

    public DistributeLock() {
        try {
            zk = new ZooKeeper("192.168.174.129:2181", 4000, this);
            // 获取ROOT节点是否存在
            Stat stat = zk.exists(ROOT_LOCK, false);
            if (stat == null) {
                // 创建持久化的节点
                zk.create(ROOT_LOCK, "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lock() {
        if (this.tryLock()) {
            System.out.println(Thread.currentThread().getName() + "->" + CURRENT_LOCK + "->get lock success");
            return;
        }
        try {
            waitForLock(WAIT_LOCK);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void waitForLock(String prev) throws KeeperException, InterruptedException {
        // listen the pre node
        Stat stat = zk.exists(prev, true);
        if (stat != null) {
            System.out.println(Thread.currentThread().getName() + "->wait the lock" + ROOT_LOCK + "/" + prev + "unlock");
            countDownLatch = new CountDownLatch(1);
            countDownLatch.await();
            System.out.println(Thread.currentThread().getName() + "->get lock success");
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        // 创建临时有序节点
        try {
            CURRENT_LOCK = zk.create(ROOT_LOCK.concat("/"), "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(Thread.currentThread().getName() + "->" + CURRENT_LOCK + ", try to lock");

            List<String> childrenList = zk.getChildren(ROOT_LOCK, false);
            TreeSet<String> sortedSet = new TreeSet<>();
            for (String child: childrenList) {
                sortedSet.add(ROOT_LOCK.concat("/").concat(child));
            }
            // get min node
            String firstNode = sortedSet.first();
            SortedSet<String> lessThanMe = sortedSet.headSet(CURRENT_LOCK);
            if (CURRENT_LOCK.equals(firstNode)) {
                return true;
            }
            if (!lessThanMe.isEmpty()) {
                WAIT_LOCK = lessThanMe.last();
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        System.out.println(Thread.currentThread().getName() + "->unlock" + CURRENT_LOCK);
        try {
            zk.delete(CURRENT_LOCK, -1);
            CURRENT_LOCK = null;
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public void process(WatchedEvent event) {
        // wait node changed
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }
}
