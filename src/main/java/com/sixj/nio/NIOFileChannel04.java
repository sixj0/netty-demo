package com.sixj.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * 拷贝文件transferFrom方法
 * @author sixiaojie
 * @date 2020-08-05-17:23
 */
public class NIOFileChannel04 {
    public static void main(String[] args) throws Exception{

        FileInputStream  fileInputStream = new FileInputStream("/Users/sixj/Desktop/未命名.png");
        FileChannel fileChannel01 = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream("/Users/sixj/Desktop/未命名(1).png");
        FileChannel fileChannel02 = fileOutputStream.getChannel();

        // 使用transferFrom完成拷贝
        fileChannel02.transferFrom(fileChannel01,0,fileChannel01.size());

        fileInputStream.close();
        fileOutputStream.close();
    }
}
