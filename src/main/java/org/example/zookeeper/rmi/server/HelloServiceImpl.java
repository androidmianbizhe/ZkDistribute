package org.example.zookeeper.rmi.server;

import org.example.zookeeper.rmi.IHelloService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class HelloServiceImpl extends UnicastRemoteObject implements IHelloService {

    public HelloServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String sayHello(String msg) throws RemoteException {
        System.out.println("test");
        return "hello ".concat(msg);
    }
}
