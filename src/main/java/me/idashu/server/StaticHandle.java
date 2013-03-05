package me.idashu.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * 描述：
 *
 * @author: dashu
 * @since: 13-3-4
 */
public class StaticHandle extends Handle {

    /**
     * 静态路径
     */
    private String staticPath;

    public StaticHandle(Session session) {
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

        File file = new File(staticPath);

        try {
            if (file.exists()) {
                StringBuffer sb = new StringBuffer();
                sb.append("HTTP/1.1 200 OK").append(Session.CRLF);
                sb.append("Content-Type:").append(Session.CRLF);
                sb.append("Content-Length: ").append(file.length()).append(Session.CRLF);
                sb.append(Session.CRLF);

                bb.clear();
                bb.put(sb.toString().getBytes());
                bb.flip();

                channel.write(bb);

                FileInputStream fis = new FileInputStream(file);
                FileChannel fc = fis.getChannel();
                bb.clear();
                int count = 0;
                while((count = fc.read(bb))!=-1){
                    bb.flip();
                    channel.write(bb);
                    bb.clear();
                }

            } else {
                // 404

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

    }

    public String getStaticPath() {
        return staticPath;
    }

    public void setStaticPath(String staticPath) {
        this.staticPath = staticPath;
    }
}
