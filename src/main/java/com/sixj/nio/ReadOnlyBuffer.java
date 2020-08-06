package com.sixj.nio;

import java.nio.ByteBuffer;

/**
 * 只读buffer
 * @author sixiaojie
 * @date 2020-08-05-17:33
 */
public class ReadOnlyBuffer {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        for (int i = 0; i < 64; i++) {
            buffer.put((byte) i);
        }

        buffer.flip();
        // 转换为只读的buffer
        ByteBuffer readOnlyBuffer = buffer.asReadOnlyBuffer();

        // 读取
        while (readOnlyBuffer.hasRemaining()){
            System.out.println(readOnlyBuffer.get());
        }

        // 只读buffer put会抛异常
//        readOnlyBuffer.put((byte) 100);
    }
}
