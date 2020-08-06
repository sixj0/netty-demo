package com.sixj.nio;

import java.nio.IntBuffer;

/**
 * 举例说明Buffer的使用(简单说明)
 * @author sixiaojie
 * @date 2020-08-05-14:39
 */
public class BasicBuffer {
    public static void main(String[] args) {
        // 创建一个Buffer，大小为5，既可以存放5个int
        IntBuffer intBuffer = IntBuffer.allocate(5);

        // 向Buffer存放数据
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i*2);
        }
        // 如何从Buffer读取数据
        // 将buffer转换，读写切换
        intBuffer.flip();

        while (intBuffer.hasRemaining()){
            System.out.println(intBuffer.get());
        }
    }
}
