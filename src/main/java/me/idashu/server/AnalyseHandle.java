package me.idashu.server;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

/**
 * 描述：处理请求<br>
 *     判断请求类型
 *
 * @author: dashu
 * @since: 13-3-4
 */
public class AnalyseHandle extends Handle {

    public AnalyseHandle(Session session) {
        super(session);
    }

    @Override
    public void run() {

        // 获取channel
        SocketChannel channel = session.getChannel();
        if (!channel.isOpen())
            return;
        // buffer
        ByteBuffer bb = ByteBuffer.allocate(1024);
        bb.clear();
        // 用来保存读取的请求信息
        StringBuffer headSB = new StringBuffer();

        int count = 0;

        // 开始读取请求信息
        try {
            while (true) {
                bb.clear();
                count = channel.read(bb);

                // 已经读完
                if (count == 0) {
                    break;
                }
                // 连接已断开
                if (count == -1) {
                    session.close();
                    return;
                }

                headSB.append(new String(bb.array()));
            }

            System.out.println("head:"+headSB.toString());

            // 读取完请求信息，切分，放到请求头队列里面
            String[] headStrs = headSB.toString().split(Session.CRLF);
            LinkedList<String> headList = session.getHeadList();
            for (String tempStr : headStrs) {
                headList.add(tempStr);
            }

            // 分析头信息
            String tempStr = headList.poll();
            String[] urls = tempStr.split(" ");
            String[] strs = urls[1].split("/");

            // 加上index.html
//            if (strs.length == 0) {
//                strs = new String[]{"index.html"};
//            }
            String[] tempStrs = strs[strs.length-1].split("\\.");
//            if (tempStrs.length == 0) {
//                strs = Arrays.copyOf(strs, strs.length+1);
//                strs[strs.length-1] = "index.html";
//                tempStrs = new String[]{"index","html"};
//            }
            // 加上index.html over
            String mime = Mime.get(tempStrs[1]);
            session.setMime(mime);

            if(tempStrs[1].equals("do")){
                // 后缀为".do"的请求是请求java

            }else{
                // 静态请求
                StringBuffer filePathSB = new StringBuffer();
                filePathSB.append(Session.ROOTPATH);
                for (int i = 0; i < strs.length; i++) {
                    filePathSB.append(File.separator);
                    filePathSB.append(strs[i]);
                }
                System.out.println(filePathSB.toString());

                StaticHandle handle = new StaticHandle(session);
                handle.setStaticPath(filePathSB.toString());
                ThreadPool.getInstance().execute(handle);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("handle session error");
            session.close();
            return;
        }

    }

}