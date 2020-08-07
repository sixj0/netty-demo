package com.sixj.nio.filecopy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 文件拷贝
 * @author sixiaojie
 * @date 2020-08-07-14:07
 */
public class TestFileCopy {
    public static void main(String[] args) throws Exception {
        fileCopy2("a.mp4", "b.mp4");
        fileCopy3("a.mp4", "c.mp4");
        fileCopy4("a.mp4", "d.mp4");
        fileCopy5("a.mp4", "e.mp4");
        fileCopy6("a.mp4", "f.mp4");
    }
    static void fileCopy1(String srcName,String destName) throws Exception{
        FileInputStream fis = new FileInputStream(srcName);
        FileOutputStream fos = new FileOutputStream(destName);
        while(true){
            int a = fis.read();
            if (a == -1){ break; }
            fos.write(a);
        }
        fis.close();
        fos.close();
    }
    static void fileCopy2(String srcName,String destName) throws Exception{
        long t1 = System.nanoTime();

        FileInputStream fis = new FileInputStream(srcName);
        FileOutputStream fos = new FileOutputStream(destName);
        byte[] bs = new byte[1024];
        while(true){
            int len = fis.read(bs);
            if (len == -1) { break;}
            fos.write(bs, 0 , len);
        }
        fis.close();
        fos.close();

        long t2 = System.nanoTime();
        System.out.println((t2-t1)/1E9);
    }
    static void fileCopy3(String srcName,String destName) throws Exception{
        long t1 = System.nanoTime();

        FileInputStream fis = new FileInputStream(srcName);
        BufferedInputStream in = new BufferedInputStream(fis);

        FileOutputStream fos = new FileOutputStream(destName);
        BufferedOutputStream out = new BufferedOutputStream(fos);

        byte[] bs = new byte[1024];
        while(true){
            int len = in.read(bs);
            if (len == -1) {break;}
            out.write(bs, 0 , len);
        }

        in.close();
        out.close();

        long t2 = System.nanoTime();
        System.out.println((t2-t1)/1E9);
    }

    static void fileCopy4(String srcName,String destName) throws Exception{
        long t1 = System.nanoTime();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        FileInputStream fis = new FileInputStream(srcName);
        FileOutputStream fos = new FileOutputStream(destName);
        FileChannel channel1 = fis.getChannel();
        FileChannel channel2 = fos.getChannel();

        while(true){
            int a = channel1.read(buffer);
            if (a == -1) {break;}
            buffer.flip();//写模式->读模式
            channel2.write(buffer);
            buffer.clear();//读模式->写模式
        }
        channel1.close();
        channel2.close();

        long t2 = System.nanoTime();
        System.out.println((t2-t1)/1E9);
    }
    static void fileCopy5(String srcName,String destName) throws Exception{
        long t1 = System.nanoTime();

        FileInputStream fis = new FileInputStream(srcName);
        FileOutputStream fos = new FileOutputStream(destName);
        FileChannel channel1 = fis.getChannel();
        FileChannel channel2 = fos.getChannel();

        MappedByteBuffer buffer = channel1.map(FileChannel.MapMode.READ_ONLY, 0, channel1.size());
        channel2.write(buffer);

        channel1.close();
        channel2.close();

        long t2 = System.nanoTime();
        System.out.println((t2-t1)/1E9);
    }
    //最快
    static void fileCopy6(String srcName,String destName) throws Exception{
        long t1 = System.nanoTime();

        FileInputStream fis = new FileInputStream(srcName);
        FileOutputStream fos = new FileOutputStream(destName);
        FileChannel channel1 = fis.getChannel();
        FileChannel channel2 = fos.getChannel();

        channel1.transferTo(0, channel1.size(), channel2);
        channel1.close();
        channel2.close();

        long t2 = System.nanoTime();
        System.out.println((t2-t1)/1E9);
    }
}
