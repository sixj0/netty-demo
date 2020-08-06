package com.sixj.nio;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 从文件中读内容
 * @author sixiaojie
 * @date 2020-08-05-16:40
 */
public class NIOFileChannel02 {
    public static void main(String[] args) throws Exception {
        // 创建一个输入流
        File file = new File("/Users/sixj/Desktop/file01.txt");
        FileInputStream fileInputStream = new FileInputStream(file);

        // 通过fileInputStream获取对应的FileChannel
        FileChannel fileChannel = fileInputStream.getChannel();

        // 创建一个缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());

        // 将通道的数据读入到buffer
        fileChannel.read(byteBuffer);

        // 将byteBuffer的字节数据转换成String
        System.out.println(new String(byteBuffer.array()));

        // 关闭流
        fileInputStream.close();
    }
}
