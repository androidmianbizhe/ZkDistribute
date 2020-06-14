package org.example.zookeeper.rpc.client;

import org.example.zookeeper.rpc.zk.IServiceDiscovery;

import java.lang.reflect.Proxy;

public class RpcClientProxy {

    private IServiceDiscovery serviceDiscovery;

    public RpcClientProxy(IServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T clientProxy(final Class<T> interfaceCls, String version) {

        return (T) Proxy.newProxyInstance(
                interfaceCls.getClassLoader(), new Class[]{interfaceCls}, new RemoteInvacationHandler(serviceDiscovery, version));
    }

//    public <T> T clientProxy(final Class<T> interfaceCls, final String host, final int port) {
//
//        return (T) Proxy.newProxyInstance(
//                interfaceCls.getClassLoader(), new Class[]{interfaceCls}, new RemoteInvacationHandler(host, port));
//    }
}
