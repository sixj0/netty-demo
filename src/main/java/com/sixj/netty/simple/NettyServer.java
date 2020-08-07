package com.sixj.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author sixiaojie
 * @date 2020-08-07-17:00
 */
public class NettyServer {
    public static void main(String[] args){
        // 创建BossGroup和WorkerGroup
        // bossGroup只是处理连接请求，真正和客户端业务处理，会交给workerGroup完成
        // 两个都是无限循环
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 创建服务器端启动的对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            // 使用链式编程来进行设置
            // 设置两个线程组
            bootstrap.group(bossGroup,workerGroup)
                    // 使用NioServerSocketChannel作为服务器的通道实现
                    .channel(NioServerSocketChannel.class)
                    // 设置线程队列得到连接个数
                    .option(ChannelOption.SO_BACKLOG,128)
                    // 设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    // 给WorkerGroup的EventLoop对应管道设置处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 创建一个通道测试对象（匿名对象）
                        // 给pipeline 设置处理器
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            System.out.println("服务器准备好了");

            // 绑定一个端口并且同步，生成一个ChannelFuture对象
            // 启动服务器（并绑定端口）
            ChannelFuture channelFuture = bootstrap.bind(6668).sync();

            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
































