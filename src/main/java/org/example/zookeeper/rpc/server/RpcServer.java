package org.example.zookeeper.rpc.server;

import org.example.zookeeper.rpc.anno.RpcAnnotation;
import org.example.zookeeper.rpc.zk.IRegisterCenter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcServer {

    private static final ExecutorService executorService =
            new ThreadPoolExecutor(
                    10, 20, 1000,
                    TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3));

    private IRegisterCenter registerCenter;
    private String serviceAddress;

    // 服务名称和服务对象的关联
    Map<String, Object> handlerMap = new HashMap<>();

    public RpcServer(IRegisterCenter registerCenter, String serviceAddress) {
        this.registerCenter = registerCenter;
        this.serviceAddress = serviceAddress;
    }

    /**
     * 绑定服务名称和对象
     * @param services
     */
    public void bind(Object... services) {
        for (Object service : services) {
            RpcAnnotation annotation = service.getClass().getAnnotation(RpcAnnotation.class);
            String serviceName = annotation.value().getName();
            String version = annotation.version();
            if (null != version && !"".equals(version)) {
                serviceName = serviceName.concat("-").concat(version);
            }
            // 绑定服务接口名称和对应的服务
            handlerMap.put(serviceName, service);
        }
    }

    public void publish() {

        ServerSocket serverSocket = null;
        try {
            String[] addr = serviceAddress.split(":");
            serverSocket = new ServerSocket(Integer.parseInt(addr[1]));

            // 注册服务
            for (String interfaceName : handlerMap.keySet()) {
                registerCenter.register(interfaceName, serviceAddress);
                System.out.println("注册服务成功".concat(interfaceName).concat(serviceAddress));
            }

            while (true) {
                Socket socket = serverSocket.accept();
                executorService.execute(new ProcessHandler(socket, handlerMap));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


//    public void publisher(final Object service, int port) {
//
//        ServerSocket serverSocket = null;
//        try {
//            serverSocket = new ServerSocket(port);
//
//            while (true) {
//                Socket socket = serverSocket.accept();
//                executorService.execute(new ProcessHandler(socket, service));
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (serverSocket != null) {
//                try {
//                    serverSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//    }
}
