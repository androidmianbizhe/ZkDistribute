package org.example.zookeeper.rpc.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

public class ServiceDiscoveryImpl implements IServiceDiscovery {

    private CuratorFramework curatorFramework;

    private List<String> repos = null;

    {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(ZKConfig.CONNECT_STR)
                .sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10))
                .build();
        curatorFramework.start();
    }

    @Override
    public String discover(String serviceName) {

        String path = ZKConfig.ZK_REGISTRY_PATH.concat("/").concat(serviceName);

        try {
            repos = curatorFramework.getChildren().forPath(path);

        } catch (Exception e) {
            throw new RuntimeException("获取子节点异常");
        }
        // 动态发现节点变化
        registerWatcher(path);

        AbstractLoadBalance randomLoadBalance = new RandomLoadBalance();
        return randomLoadBalance.selectHost(repos);
    }

    private void registerWatcher(final String path) {

        PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework, path, true);

        PathChildrenCacheListener listener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                repos = client.getChildren().forPath(path);
            }
        };

        childrenCache.getListenable().addListener(listener);

        try {
            childrenCache.start();
        } catch (Exception e) {
            throw new RuntimeException("注册PathChildren Watcher异常", e);
        }

    }
}
