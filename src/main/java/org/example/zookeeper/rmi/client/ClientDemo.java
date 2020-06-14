package org.example.zookeeper.rmi.client;

import org.example.zookeeper.rmi.IHelloService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientDemo {
    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        IHelloService service = (IHelloService) Naming.lookup("rmi://127.0.0.1/Hello");
        System.out.println(service.sayHello("world!"));
    }
}
