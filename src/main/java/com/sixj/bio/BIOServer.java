package com.sixj.bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 使用BIO模型编写一个服务器端，监听6666端口，当有客户端连接时，就启动一个线程与之通讯
 * 要求使用线程池改善，可以连接多个客户端
 * @author sixiaojie
 * @date 2020-08-05-10:52
 */
public class BIOServer {

    /**
     * Java NIO之前，基于Java的所有socket通信都采用同步阻塞模式（BIO）
     * 类似于一问一答模式，客户端发起一次请求，同步等待调用结果的返回。
     * BIO的服务端通信模型，通常由一个独立的Acceptor（消费者）线程负责监听客户端的连接，
     * 它收到客户端的连接之后，为每个客户端创建一个新的线程进行链路处理，处理完后，
     * 通过输出流返回应答给新的客户端，线程销毁，典型的一请求一应答通信模型
     *
     * 该模型存在的问题：
     * 服务端的线程个数与客户端的并发访问数量呈1：1的正比关系，java中线程是比较宝贵的系统资源，线程数量快速膨胀后，
     * 系统的性能急剧下降，随着访问量的继续增大，系统最终就死掉了。
     */
    public static void main(String[] args) throws Exception {

        // 创建线程池
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();

        // 创建serverSocket
        ServerSocket serverSocket = new ServerSocket(6666);

        System.out.println("服务器启动了");

        while (true){
            // 监听，等待客户端连接
            final Socket socket = serverSocket.accept();
            System.out.println("连接到一个客户端");

            // 就创建一个线程，与之通讯
            newCachedThreadPool.execute(()->{
                // 可以和客户端通讯
                handler(socket);
            });
        }
    }

    /**
     * 编写一个handler方法，和客户端通讯
     */
    public static void handler(Socket socket){
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            // step 1：获取此套接字的输入流，并包装为BufferedReader对象
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // step 2：获取此套接字的输出流，并包装为PrintWriter对象
            out = new PrintWriter(socket.getOutputStream(),true);
            String expression;
            String result;
            while(true){
                // step 3: 读取一行，如果读到输入流尾部，就退出循环
                if((expression = in.readLine()) == null){
                    break;
                }
                System.out.println("服务器收到消息"+expression);

                // step 4: 对接收到的字符串做处理
                result = "服务端处理："+expression.length()+"";

                // step 5：将结果写入输出流，返回给客户端
                out.println(result);
            }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            System.out.println("关闭和client的连接");
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
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
