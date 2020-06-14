package org.example.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class App {
    public static void main(String[] args) throws IOException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    countDownLatch.await();
                    DistributeLock distributeLock = new DistributeLock();
                    distributeLock.lock();// getLock
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "Thread-" + i).start();
            countDownLatch.countDown();
        }
        System.in.read();
    }
}
