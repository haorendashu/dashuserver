package me.idashu.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 描述：服务端
 *
 * @author: dashu
 * @since: 13-2-27
 */
public class Server implements Runnable{

    private int port = 80;
    private Selector selector;

    public Server() {

    }

    @Override
    public void run() {

        try {
            selector = Selector.open();

            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.socket().bind(new InetSocketAddress(port));
            ssc.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("start server");

            while(true){

                int n = selector.select();

                Set<SelectionKey> set = selector.selectedKeys();
                Iterator<SelectionKey> it = set.iterator();

                while (it.hasNext()) {

                    SelectionKey key = it.next();
                    it.remove();

                    if ((key.interestOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {

                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel sc = server.accept();
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ);

                        System.out.println("accept");

                    } else if ((key.interestOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {

                        Session session = new Session(key);
                        AnalyseHandle handle = new AnalyseHandle(session);
                        ThreadPool.getInstance().execute(handle);
                        // 取消注册
                        key.cancel();

                    }

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        new Thread(new Server()).start();
    }
}
