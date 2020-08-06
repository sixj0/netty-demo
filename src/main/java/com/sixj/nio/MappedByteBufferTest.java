package com.sixj.nio;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * MappedByteBuffer可以让文件直接在内存（堆外内存）中进行修改，
 * 操作系统不需要拷贝一次
 * 而如何同步到文件由NIO来完成
 * @author sixiaojie
 * @date 2020-08-05-19:37
 */
public class MappedByteBufferTest {
    public static void main(String[] args) throws Exception {

        RandomAccessFile randomAccessFile = new RandomAccessFile("/Users/sixj/Desktop/file01.txt", "rw");
        // 获取对应的文件通道
        FileChannel channel = randomAccessFile.getChannel();

        /*
         * 参数1：FileChannel.MapMode.READ_WRITE 使用读写模式
         * 参数2：0 可以直接修改的其实位置
         * 参数3：5 是映射到内存的大小，即将 file01.txt的多少个字节映射到内存
         * 可以直接修改的范围[0,5)
         */
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

        mappedByteBuffer.put(0,(byte)'H');
        mappedByteBuffer.put(3,(byte)'9');

        randomAccessFile.close();

    }
}
