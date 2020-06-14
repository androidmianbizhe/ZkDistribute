package org.example.zookeeper.rpc.server;

import org.example.zookeeper.rpc.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ProcessHandler implements Runnable {

    private Socket socket;
    Map<String, Object> handlerMap = new HashMap<>();

    public ProcessHandler(Socket socket, Map<String, Object> handlerMap) {
        this.socket = socket;
        this.handlerMap = handlerMap;
    }

    @Override
    public void run() {
        ObjectInputStream inputStream = null;
        ObjectOutputStream  outputStream = null;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            RpcRequest request = (RpcRequest) inputStream.readObject();
            Object object = invoke(request);

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(object);
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if(inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    private Object invoke(RpcRequest request) {
//        Object[] args = request.getParameters();
//        Class<?>[] types = new Class<?>[args.length];
//        for (int i = 0; i < args.length; i++) {
//            types[i] = args[i].getClass();
//        }
//        try {
//            Method method = service.getClass().getMethod(request.getMethodName(), types);
//            return method.invoke(service, args);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    private Object invoke(RpcRequest request) {
        Object[] args = request.getParameters();
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i].getClass();
        }
        String serviceName = request.getClassName();
        String version = request.getVersion();
        if (null != version && !"".equals(version)) {
            serviceName = serviceName.concat("-").concat(version);
        }
        try {
            Object service = handlerMap.get(serviceName);
            Method method = service.getClass().getMethod(request.getMethodName(), types);
            return method.invoke(service, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
