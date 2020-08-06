package com.sixj.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author sixiaojie
 * @date 2020-08-06-14:35
 */
public class NIOServer {
    public static void main(String[] args) throws IOException {
        // 创建ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 得到一个Selector对象
        Selector selector = Selector.open();

        // 绑定一个端口6666，在服务器端监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));

        // 设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        // 把serverSocketChannel注册到 selector，关心事件为OP_ACCEPT
        serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);

        // 循环等待客户端连接
        while (true){
            // 等待1秒，如果没有事件发生，返回
            if(selector.select(1000) == 0){
                System.out.println("服务器等待1秒，无连接");
                continue;
            }
            // 如果返回的>0,获取到相关的selectionKeys集合
            // 1. 如果返回的>0，表示已经获取到关注的事件
            // 2. selector.selectedKeys 返回关注事件的集合
            // 通过selectedKeys反向获取通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            // 遍历Set<SelectionKey>
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()){
                // 获取到SelectionKey
                SelectionKey key = keyIterator.next();
                // 根据key对应的通道发生的事件作出相应的处理
                // 如果是OP_ACCEPT,有新的客户端连接
                if(key.isAcceptable()){
                    // 该客户端生成一个SocketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("客户端连接成功，生成了一个socketChannel"+socketChannel.hashCode());
                    // 将socketChannel设置为非阻塞
                    socketChannel.configureBlocking(false);
                    // 将socketChannel注册到selector,关注事件为OP_READ，同时给socket关联一个buffer
                    socketChannel.register(selector,SelectionKey.OP_READ,ByteBuffer.allocate(1024));
                }

                // 发生OP_READ
                if(key.isReadable()){
                    // 通过key，反向获取到对应channel
                    SocketChannel channel = (SocketChannel)key.channel();
                    // 获取到该channel关联的buffer
                    ByteBuffer buffer = (ByteBuffer)key.attachment();
                    // 把当前Chanel数据读到buffer
                    channel.read(buffer);
                    System.out.println("from客户端 "+new String(buffer.array()));
                }

                // 手动从集合中移除当前的selectionKey,防止重复操作
                keyIterator.remove();
            }
        }
    }
}
