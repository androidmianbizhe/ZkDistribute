package org.example.zookeeper.rpc.client;

import org.example.zookeeper.rpc.ITestHelloService;
import org.example.zookeeper.rpc.zk.IServiceDiscovery;
import org.example.zookeeper.rpc.zk.ServiceDiscoveryImpl;

public class ClinetDemo {
    public static void main(String[] args) {
        IServiceDiscovery discovery = new ServiceDiscoveryImpl();

        RpcClientProxy rpcClientProxy = new RpcClientProxy(discovery);
        ITestHelloService service = rpcClientProxy.clientProxy(ITestHelloService.class, "2.0");
        System.out.println(service.say("mic"));
    }
}
