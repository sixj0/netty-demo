package com.sixj.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Scattering：将数据写入到buffer时，可以采用buffer数组，依次写入
 * Gathering：从buffer读取数据时，也可以采用buffer数组，依次读
 * @author sixiaojie
 * @date 2020-08-06-11:15
 */
public class ScatteringAndGatheringTest {
    public static void main(String[] args) throws IOException {

        // 使用ServerSocketChannel SocketChannel 网络

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);

        // 绑定端口到socket，并启动
        serverSocketChannel.socket().bind(inetSocketAddress);

        // 创建buffer数组
        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);

        // 等待客户端连接
        SocketChannel socketChannel = serverSocketChannel.accept();

        // 假定从客户端接受8个字节
        int messageLength = 8;

        // 循环的读取
        while (true){
            int byteRead = 0;
            while (byteRead < messageLength){
                long read = socketChannel.read(byteBuffers);
                byteRead += read;
                // 累计读取的字节
                System.out.println("byteRead="+byteRead);

                Arrays.asList(byteBuffers).stream().map(
                        buffer->"position="+buffer.position()+
                                ",limit="+buffer.limit())
                        .forEach(System.out::println);

                // 将所有buffer进行反转
                Arrays.asList(byteBuffers).forEach(Buffer::flip);

                // 将数据读出显示到客户端
                long byteWrite = 0;
                while (byteWrite < messageLength){
                    long write = socketChannel.write(byteBuffers);
                    byteWrite += write;
                }

                // 将所有buffer复位
                Arrays.asList(byteBuffers).forEach(Buffer::clear);

                System.out.println("byteRead:="+byteRead+",byteWrite:="+byteWrite+",messageLength:="+messageLength);
            }
        }

    }
}
