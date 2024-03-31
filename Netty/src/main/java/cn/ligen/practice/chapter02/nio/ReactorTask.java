package cn.ligen.practice.chapter02.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author ligen
 * @date 2024/3/28 0:02
 * @description NIO服务端
 */
public class ReactorTask implements Runnable{

    Selector selector = null;
    ServerSocketChannel serverSocketChannel = null;

    public static void main(String[] args) throws IOException {
        // 1、打开ServerSocketChannel，用户建通客户端的连接，它是所有客户端连接的父管道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 2、绑定监听端口，设置连接为非阻塞模式
        serverSocketChannel.socket().bind(new InetSocketAddress("127.0.0.1", 8080));
        serverSocketChannel.configureBlocking(false);
        // 3、创建Reactor线程，创建多路服务器并启动线程
        Selector selector = Selector.open();
        new Thread(new ReactorTask()).start();

    }

    @Override
    public void run() {
        try {
            // 4、将ServerSocketChannel注册到Reactor线程的多路复用器上，监听ACCEPT事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            // 5、多路复用器在线程run方法的无线循环体内轮询准备就绪的key
            while (true) {
                int num = selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    // 处理
                    // 6、多路复用器监听到有新客户端接入，处理新接入请求
                    SocketChannel channel = serverSocketChannel.accept();
                    // 7、设置客户端链路为非阻塞
                    channel.configureBlocking(false);
                    channel.socket().setReuseAddress(true);
                    // 8、将新接入客户端注册到Reactor线程的多路复用器上，监听读
                    channel.register(selector, SelectionKey.OP_READ);
                    // 9、异步读缓冲区
                    int readNumber = channel.read(ByteBuffer.allocateDirect(1024));
                    // 10、对Buffer进行编码

                }
            }
        } catch (ClosedChannelException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
