package org.example.zookeeper.rpc.server;

import org.example.zookeeper.rpc.ITestHelloService;
import org.example.zookeeper.rpc.anno.RpcAnnotation;

@RpcAnnotation(value = ITestHelloService.class, version = "2.0")
public class TestHelloServiceImpl2 implements ITestHelloService {

    @Override
    public String say(String msg) {
        return "hello version2 ".concat(msg);
    }
}
