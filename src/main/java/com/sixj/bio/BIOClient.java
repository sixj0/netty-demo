package com.sixj.bio;

import java.io.*;
import java.net.Socket;

/**
 * @author sixiaojie
 * @date 2020-08-05-11:24
 */
public class BIOClient {
    // 默认的端口号
    private static int DEFAULT_SERVER_PORT = 6666;
    // 默认的服务器IP
    private static String DEFAULT_SERVER_IP = "127.0.0.1";
    // 发送信息
    public static void send(String expression){
        send(DEFAULT_SERVER_PORT,expression);
    }

    public static void send(int port,String expression){
        System.out.println("客户端算术表达式为："+expression);
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try{
            // step 1:创建socket对象
            socket = new Socket(DEFAULT_SERVER_IP,port);
            // step 2：获取此套接字的输入流，并包装为BufferedReader对象
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // step 3：获取此套接字的输出流，并包装为PrintWriter对象
            out = new PrintWriter(socket.getOutputStream(),true);
            // step 4：往服务器写数据
            out.println(expression);
            // step 5：获取服务器返回的数据
            System.out.println("__结果为："+in.readLine());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // step 6：关闭相应的流
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out != null){
                out.close();
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            send("测试"+i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
