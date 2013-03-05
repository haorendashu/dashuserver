package me.idashu.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * 描述：
 *
 * @author: dashu
 * @since: 13-2-27
 */

public class Session {

    public static final String CRLF = "\r\n";

    public static final String ROOTPATH = "C:"+File.separator+"server";

    /**
     * 请求Key
     */
    private SelectionKey key;
    /**
     * 通道
     */
    private SocketChannel channel;
    /**
     *  请求头的分析队列
     */
    LinkedList<String> headList;
    /**
     * 应答类型
     */
    private String mime;

    public Session(SelectionKey key) {
        this.key = key;
        headList = new LinkedList<String>();
    }

    /**
     * 关闭通道
     */
    public void close(){
        if (channel != null && channel.isOpen())
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (key != null)
            key.cancel();
    }

    public SelectionKey getKey() {
        return key;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }

    public SocketChannel getChannel() {
        if (channel == null) {
            channel = (SocketChannel) key.channel();
        }
        return channel;
    }

    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public LinkedList<String> getHeadList() {
        return headList;
    }

    public void setHeadList(LinkedList<String> headList) {
        this.headList = headList;
    }
}
