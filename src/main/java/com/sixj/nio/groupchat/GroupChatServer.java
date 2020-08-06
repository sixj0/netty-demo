package com.sixj.nio.groupchat;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * 服务器启动并监听6667
 * 服务器接收客户端信息，并实现转发（处理上线和离线）
 * @author sixiaojie
 * @date 2020-08-06-15:46
 */
public class GroupChatServer {
    private Selector selector;
    private ServerSocketChannel listenChannel;
    private static final int PORT = 6667;
    public GroupChatServer(){
        try {
            // 得到选择器
            selector = Selector.open();
            // ServerSocketChannel
            listenChannel = ServerSocketChannel.open();
            // 绑定端口
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            // 设置非阻塞模式
            listenChannel.configureBlocking(false);
            // 将该listenChannel注册到Selector
            listenChannel.register(selector,SelectionKey.OP_ACCEPT);


        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public void listen(){
        try{
            // 循环处理
            while (true){
                int count = selector.select();
                // 有事件处理
                if(count > 0){
                    // 遍历得到的SelectionKey集合
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        // 取出SelectorKey
                        SelectionKey key = iterator.next();
                        // 监听到accept
                        if(key.isAcceptable()){
                            SocketChannel socketChannel = listenChannel.accept();
                            socketChannel.configureBlocking(false);
                            // 将该socketChannel注册到selector
                            socketChannel.register(selector,SelectionKey.OP_READ);
                            // 提示
                            System.out.println(socketChannel.getRemoteAddress()+" 上线");
                        }
                        // 通道发生read事件，即通道是可读的状态
                        if(key.isReadable()){
                            // 处理读
                            readData(key);
                        }

                        // 当前的key删除，防止重复处理
                        iterator.remove();
                    }
                }else {
                    System.out.println("等待...");
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
    }


    /**
     * 读取客户端消息
     * @param key
     */
    private void readData(SelectionKey key){
        // 定义一个SocketChannel
        SocketChannel channel = null;
        try{
            // 得到channel
            channel = (SocketChannel) key.channel();
            // 创建buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            // 读取
            StringBuilder stringBuilder = new StringBuilder();
            while (true){
                buffer.clear();
                int count = channel.read(buffer);
                if(count == -1){
                    System.out.println(channel.getRemoteAddress()+" 离线");
                    // 取消注册
                    key.cancel();
                    // 关闭通道
                    channel.close();
                    break;
                }
                if(count == 0){
                    break;
                }
                // 将缓冲区的数据转成字符串
                stringBuilder.append(new String(buffer.array(),0,count));
            }
            // 输出消息
            String msg = stringBuilder.toString();
            if(!StringUtils.isEmpty(msg.trim())){
                System.out.println("form 客户端："+msg);
                // 向其他的客户端转发消息(去掉自己)
                sendInfoToOtherClients(msg,channel);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 转发消息给其他客户端（通道）
     */
    private void sendInfoToOtherClients(String msg,SocketChannel self) throws IOException {
        System.out.println("服务器转发消息中...");
        // 遍历所有注册到selector上的socketChannel,并排除自己
        for (SelectionKey key : selector.keys()) {
            // 通过key取出对应的socketChannel
            Channel targetChannel = key.channel();
            // 排除自己
            if(targetChannel instanceof SocketChannel && !targetChannel.equals(self)){
                // 转型
                SocketChannel dest = (SocketChannel) targetChannel;
                // 将msg存储到buffer
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                // 将buffer 的数据写入通道
                dest.write(buffer);
            }
        }
    }

    public static void main(String[] args) {
        // 创建一个服务器对象
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }
}
