package com.sixj.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用一个Buffer完成文件读取
 * 使用FileChannel和方法read、write完成文件的拷贝
 * @author sixiaojie
 * @date 2020-08-05-16:49
 */
public class NIOFileChannel03 {
    public static void main(String[] args) throws Exception {

        FileInputStream  fileInputStream = new FileInputStream("/Users/sixj/Desktop/file01.txt");
        FileChannel fileChannel01 = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream("/Users/sixj/Desktop/file02.txt");
        FileChannel fileChannel02 = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        while (true){
            // 清空buffer
            byteBuffer.clear();
            int read = fileChannel01.read(byteBuffer);
            if(read == -1){
                break;
            }
            // 将buffer中的数据写入到fileChannel02--file02.txt
            byteBuffer.flip();
            fileChannel02.write(byteBuffer);
        }

        fileInputStream.close();
        fileOutputStream.close();

    }
}
