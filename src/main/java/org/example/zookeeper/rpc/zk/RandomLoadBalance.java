package org.example.zookeeper.rpc.zk;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance extends AbstractLoadBalance {

    @Override
    protected String doSelect(List<String> repos) {

        int len = repos.size();
        Random random = new Random();
        return repos.get(random.nextInt(len));
    }
}
