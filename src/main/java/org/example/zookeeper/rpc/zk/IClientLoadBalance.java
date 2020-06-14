package org.example.zookeeper.rpc.zk;

import java.util.List;

public interface IClientLoadBalance {

    String selectHost(List<String> repos);
}
