package org.example.zookeeper.rpc.client;

import org.example.zookeeper.rpc.RpcRequest;
import org.example.zookeeper.rpc.zk.IServiceDiscovery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RemoteInvacationHandler implements InvocationHandler {
    private IServiceDiscovery serviceDiscovery;
    private String version;

//    private String host;
//    private int port;
//
//    public RemoteInvacationHandler(String host, int port) {
//        this.host = host;
//        this.port = port;
//    }

    public RemoteInvacationHandler(IServiceDiscovery serviceDiscovery, String version) {

        this.serviceDiscovery = serviceDiscovery;
        this.version = version;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        RpcRequest request = new RpcRequest();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameters(args);
        request.setVersion(version);

        // 根据接口名称拿到服务地址

        String serviceAddress = serviceDiscovery.discover(request.getClassName());

        TcpTransport transport = new TcpTransport(serviceAddress);
        return transport.send(request);
    }
}
