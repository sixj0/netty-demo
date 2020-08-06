package com.sixj.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @author sixiaojie
 * @date 2020-08-06-16:26
 */
public class GroupChatClient {
    private final String HOST = "127.0.0.1";
    private final int PORT = 6667;
    private Selector selector;
    private SocketChannel socketChannel;
    private String userName;

    public GroupChatClient() throws IOException {

        selector = Selector.open();
        // 连接服务器
        socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
        // 设置非阻塞
        socketChannel.configureBlocking(false);
        // 将channel注册到selector
        socketChannel.register(selector,SelectionKey.OP_READ);
        // 得到userName
        userName = socketChannel.getLocalAddress().toString();
        System.out.println(userName+" is ok...");
    }

    /**
     * 向服务器发送消息
     */
    public void sendInfo(String info){
        info = userName+" 说："+ info;
        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 读取从服务端回复的消息
     */
    public void readInfo(){
        try {
            int readChannels = selector.select();
            // 有可用的通道
            if(readChannels > 0){
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isReadable()){
                        // 得到相关的通道
                        SocketChannel sc = (SocketChannel)key.channel();
                        // 创建buffer
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        // 读取
                        StringBuilder stringBuilder = new StringBuilder();
                        while (true){
                            buffer.clear();
                            int count = sc.read(buffer);
                            if(count <= 0){
                                break;
                            }
                            // 将缓冲区的数据转成字符串
                            stringBuilder.append(new String(buffer.array(),0,count));

                        }
                        // 输出消息
                        String msg = stringBuilder.toString();
                        System.out.println(msg.trim());
                    }
                    iterator.remove();
                }
            }else {
                System.out.println("没有可用的通道...");

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // 启动客户端
        GroupChatClient chatClient = new GroupChatClient();

        // 启动一个线程,每隔3秒，读取从服务器发送的数据
        new Thread(()->{
            while (true){
                chatClient.readInfo();
                try {
                    Thread.sleep(3000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();
        
        // 发送数据给服务器
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String s = scanner.nextLine();
            chatClient.sendInfo(s);
        }
    }
}
