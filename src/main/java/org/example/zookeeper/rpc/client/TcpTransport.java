package org.example.zookeeper.rpc.client;

import org.example.zookeeper.rpc.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TcpTransport {
    private String serviceAddress;

    public TcpTransport(String serviceAddress) {

        this.serviceAddress = serviceAddress;
    }

//    private String host;
//    private int port;
//
//    public TcpTransport(String host, int port) {
//        this.host = host;
//        this.port = port;
//    }

    private Socket newSocket() {
        System.out.println("创建一个新的连接");
        Socket socket = null;
        try {
            String[] addrs = serviceAddress.split(":");
            socket = new Socket(addrs[0], Integer.parseInt(addrs[1]));
            return socket;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("连接建立失败");
        }
    }

    public Object send(RpcRequest request) {
        Socket socket = null;
        ObjectOutputStream outputStream = null;
        ObjectInputStream inputStream = null;
        try {
            socket = newSocket();

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(request);
            outputStream.flush();

            inputStream = new ObjectInputStream(socket.getInputStream());

            return inputStream.readObject();

        } catch (Exception exception) {
            throw new RuntimeException("发起远程调用失败", exception);
        } finally {
            try {
                outputStream.close();
                inputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
