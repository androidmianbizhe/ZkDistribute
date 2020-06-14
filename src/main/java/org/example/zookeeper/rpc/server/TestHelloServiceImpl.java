package org.example.zookeeper.rpc.server;

import org.example.zookeeper.rpc.ITestHelloService;
import org.example.zookeeper.rpc.anno.RpcAnnotation;

@RpcAnnotation(ITestHelloService.class)
public class TestHelloServiceImpl implements ITestHelloService {

    @Override
    public String say(String msg) {
        return "hello ".concat(msg);
    }
}
