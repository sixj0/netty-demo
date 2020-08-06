package com.sixj.nio;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 向文件写内容
 * @author sixiaojie
 * @date 2020-08-05-16:26
 */
public class NIOFileChannel01 {
    public static void main(String[] args) throws Exception {
        String str = "hello,司晓杰";

        // 创建一个输出流
        FileOutputStream fileOutputStream = new FileOutputStream("/Users/sixj/Desktop/file01.txt");

        // 通过fileOutputStream获取对应的FileChannel
        FileChannel fileChannel = fileOutputStream.getChannel();

        // 创建一个缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        // 将str放入到byteBuffer
        byteBuffer.put(str.getBytes());

        // 对byteBuffer，进行读写转换
        byteBuffer.flip();

        // 将byteBuffer写入到fileChannel
        fileChannel.write(byteBuffer);

        // 关闭流
        fileOutputStream.close();
    }
}
