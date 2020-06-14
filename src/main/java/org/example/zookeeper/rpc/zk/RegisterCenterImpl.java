package org.example.zookeeper.rpc.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class RegisterCenterImpl implements IRegisterCenter {

    private CuratorFramework curatorFramework;

    {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(ZKConfig.CONNECT_STR)
                .sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10))
                .build();
        curatorFramework.start();
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        // 注册相应的服务
        String servicePath = ZKConfig.ZK_REGISTRY_PATH.concat("/").concat(serviceName);
        try {
            if (curatorFramework.checkExists().forPath(servicePath) == null) {
                curatorFramework.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(servicePath, "0".getBytes());

                String addressPath = servicePath.concat("/").concat(serviceAddress);
                String resNode = curatorFramework.create().withMode(CreateMode.EPHEMERAL)
                        .forPath(addressPath, "0".getBytes());
                System.out.println("服务注册成功" + resNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
