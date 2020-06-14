package org.example.zookeeper.rpc.server;

import org.example.zookeeper.rpc.ITestHelloService;
import org.example.zookeeper.rpc.zk.IRegisterCenter;
import org.example.zookeeper.rpc.zk.RegisterCenterImpl;

import java.io.IOException;
import java.rmi.RemoteException;

public class ServerDemo {

    public static void main(String[] args) throws IOException {
        ITestHelloService service = new TestHelloServiceImpl();
        ITestHelloService service2 = new TestHelloServiceImpl2();

        IRegisterCenter center = new RegisterCenterImpl();
        RpcServer server = new RpcServer(center, "localhost:8081");
        server.bind(service, service2);
        server.publish();
        System.in.read();
//        server.publisher(service, 8888);
    }
}
