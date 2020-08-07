### 简介

- Netty是由JBoss提供的一个Java开源框架，现为github上的独立项目。
- Netty 是一个异步的、基于事件驱动的网络应用框架，用以快速开发高性能、高可靠性的网络IO程序
- Netty主要针对在TCP协议下，面向Clients端的高并发应用，或者 peer-to-peer 场景下的大量数据持续传输的应用
- Netty本质是一个NIO框架，适用于服务器通讯相关的多种应用场景

TCP/IP -> 原生JDK io/网络 -> NIO(io,网络) -> Netty

### I/O模型基本说明

- I/O模型简单的理解：就是用什么样的通道进行数据的发送和接收，很大程度上决定了程序通信的性能
- Java共支持3种网络编程模型I/O模式：BIO、NIO、AIO
- Java BIO：同步并阻塞（传统阻塞型），服务器实现模式为一个连接一个线程，即客户端有连接请求时服务器端就需要启动一个线程进行处理，如果这个连接不做任何事倩会造成不必要的线程开销
- Java NIO：同步非阻塞，服务器实现模式为一个线程处理多个请求（连接），即客户端发送的连接请求都会注册到多路复用器上，多路复用器轮询到连接有I/O请求就进行处理
- Java AIO ( NIO.2 ) ：异步非阻塞， AIO 引入异步通道的概念，采用了 Proactor 模式，简化了程序编写，有效的请求才启动线程，它的特点是先由操作系统完成后才通知服务端程序启动线程去处理，一般适用于连接数较多且连接时间较长的应用

### BIO、NIO、AIO适用场景分析

- BIO方式适用于连接数目比较小且固定的架构，这种方式对服务器资源要求比较高, 并发局限于应用中，JDK1.4以前的唯一选择，但程序简单易理解。
- NIO方式适用于连接数目多且连接比较短（轻操作）的架构，比如聊天服务器，弹幕 系统，服务器间通讯等。编程比较复杂，JDK1.4开始支持。
- AIO 方式使用于连接数目多且连接比较长（重操作）的架构，比如相册服务器，充分调用 OS参与并发操作，编程比较复杂， JDK7 开始支持。

### BIO编程简单流程

1)  服务器端启动一个ServerSocket

2)  客户端启动Socket对服务器进行通信，默认情况下服务器端需要对每个客户建立一个线程与之通讯

3)  客户端发出请求后，先咨询服务器是否有线程响应，如果没有则会等待，或者被拒绝

4)  如果有响应，客户端线程会等待请求结束后，才继续执行

Server —1:n— Thread —1:1— Client

BIO应用实例代码：https://github.com/sixj0/netty-demo/tree/master/src/main/java/com/sixj/bio

### Java NIO基本介绍

- Java NIO全称 java non-blocking IO，是指JDK提供的新API。从JDK1.4开始，Java提供了一系列改进的输入/输出的新特性，被统称为NIO(即New IO),是同步非阻塞的
- NIO相关类都被放在java.nio包及子包下，并且对原java.io包中的很多类进行改写
- NIO有三大核心部分：Channel（通道），Buffer（缓冲区），Selector（选择器）
- NIO是面向缓冲区，或者面向块编程的。数据读取到一个它稍后处理的缓冲区中，需要时可以在缓冲区中前后移动，这就增加了处理过程中的灵活性，使用它可以提供非阻塞式的高伸缩性网络
- Java NIO的非阻塞模式，使一个线程从某个通道发送请求或者读取数据，但是它仅能得到目前可用的数据，如果目前没有数据可用时，就什么都不会获取，而不是保持线程阻塞，所以直至数据变得可以读取之前，该线程可以继续做其他的事情。非阻塞写也是如此，一个线程请求写入一些数据到某个通道，但不需要等待它完全写入，这个线程同时可以去做别的事情。
- 通俗理解：NIO是可以做到用一个线程来处理多个操作的。假设有10000个请求过来，根据实际情况，可以分配50或者100个线程来处理。不像之前的阻塞IO那样，非要分配10000个。
- HTTP2.0使用了多路复用的技术，做到同一个连接并发处理多个请求，而且并发请求的数量比HTTP1.1大了好几个数量级。

Server —1:n— Thread —1:1— Selector —1:n— Chanel —1:1— Buffer —1:1— Client

### NIO和BIO的比较

- BIO以流的方式处理数据，而NIO以块的方式处理数据，块IO的效率比流IO高很多
- BIO是阻塞的，NIO是非阻塞的
- BIO基于字节流和字符流进行操作，而NIO基于channel和buffer进行操作，数据总是从通道读取到缓冲区中，或者从缓冲区写入到通道中。selector用于监听多个通道事件（比如：连接请求，数据到达等），因此使用单个线程就可以监听多个客户端通道

### 缓冲区（Buffer）

基本介绍

缓冲区：缓冲区本质上是一个可以读写的内存块，可以理解成是一个容取对象（含数组），该对象提供了一组方法，可以更轻松的使用内存块，缓冲区对象内置了一些机制，能够跟踪和记录缓冲区的状态变化情况。channel提供从文件、网络读取数据的渠道，但是读取或写入的数据都必须经由Buffer

```java
// Invariants: mark <= position <= limit <= capacity
private int mark = -1;// 标记
private int position = 0;// 位置，下一个要被读写的元素的索引，每次读写缓冲区时都会改变该值，为下次读写作准备 
private int limit;// 表示缓冲区的当前重点，不能对缓冲区超过极限的位置进行读写操作，且极限是可以修改的
private int capacity;// 容量，既可以容纳最大数据量，在缓冲区创建时被设定并且不能改变
```

### 通道（Channel）

基本介绍

1） NIO的通道类似于流，但是有些区别如下：

- 通道可以同时进行读写，而流只能读或者只能写
- 通道可以实现异步读写数据
- 通道可以从缓冲区读数据，也可以写数据到缓冲区

2） BIO中的stream是单向的，例如FileInputStream对象只能进行读取数据的操作，而NIO中的通道（channel）是双向的，可以读操作，也可以写操作

3） Channel在NIO中是一个接口 public interface Channel extends Closeable{}

4） 常用的Channel类有：FileChannel、DatagramChannel、ServerSocketChannel和SocketChannel

5） FileChannel用于文件的数据读写，DatagramChannel用于UDP的数据读写，ServerSocketChannel和SocketChannel用于TCP的数据读写

### Selector(选择器)

基本介绍

- Java的NIO，用非阻塞的IO方式，可以用一个线程，处理多个客户端的连接，就会使用到Selector（选择器）
- Selector能够检测多个注册的通道上是否有事件发生（注意：多个Channel以事件的方式可以注册到同一个Selector），如果有事件发生，便获取事件然后针对每个事件进行相应的处理，这样就可以只用一个单线程去管理多个通道，也就是管理多个连接和请求。
- 只有在连接/通道真正有读写事件发生时，才会进行读写，就大大地减少了系统开销，并且不必为每个连接都创建一个线程，不用去维护多个线程
- 避免了多线程之间的上下文切换导致的开销

```java
selector.select();// 不阻塞
selector.select(1000);// 阻塞1000毫秒，在1000毫秒后返回
selector.wakeup();// 唤醒selector
selector.selectNow();// 不阻塞，立马返还
```

1. 当客户端连接时，会通过ServerSocketChannel得到SocketChannel
2. 将socketChannel注册到Selector上，register（Selector sel，int ops）, 一个selector上可以注册多个SocketChannel
3. 注册后返回一个SelectionKey,会和该selector关联
4. selector进行监听select方法，返回有事件发生的通道的个数
5. 进一步得到各个SelectionKey(有事件发生)

**ServerSocketChannel在服务器端监听新的客户端Socket连接**

**SocketChannel，网络IO通道，具体负责进行读写操作，NIO把缓冲区的数据写入通道，或者把通道里的数据读到缓冲区**

NIO应用实例代码：https://github.com/sixj0/netty-demo/tree/master/src/main/java/com/sixj/nio

### NIO 网络编程应用实例-群聊系统

实例要求：

1. 编写一个NIO群聊系统，实现服务器端和客户端之间的数据简单通讯（非阻塞）

2. 实现多人群聊

3. 服务器端：可以监测用户上线、离线，并实现消息转发功能

4. 客户端：通过channel可以无阻塞发送消息给其他用户，同时可以接受其他用户发送的消息（有服务器转发得到）

群聊系统实例代码：https://github.com/sixj0/netty-demo/tree/master/src/main/java/com/sixj/nio/groupchat