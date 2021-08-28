package org.apache.rocketmq.example.quickstart.querymessage;

import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.common.message.MessageDecoder;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.sysflag.MessageSysFlag;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * 读取本地的consummerQuenue文件的内容
 * @Auther: huangxiaogen
 * @Date: 2021/08/26/上午10:47
 * @Description:
 */

public class QueryLocalConsummerQueueMessage {
    /**
     * 获取消息长度和偏移量
     * @param args
     * @throws Exception
     */

//    public static void main(String[] args)throws Exception {
//        String path = "/Users/bjhl/IdeaProjects/rocketmq/rocketmq-nameserver/store/consumequeue/TopicTest/4/00000000000000000000";
//        ByteBuffer buffer = read(path);
//        while (true){
//            long offset = buffer.getLong();
//            long size = buffer.getInt();
//            long code = buffer.getLong();
//            if (size==0){
//                break;
//            }
//            System.out.println("消息长度:"+size+" 消息偏移量:" +offset);
//        }
//        System.out.println("--------------------------");
//    }

    /**
     *  通过ConsumerQueue获取消息的具体内容。
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        //consumerqueue根目录
        String consumerPath = "/Users/bjhl/IdeaProjects/rocketmq/rocketmq-nameserver/store/consumequeue/";
        //commitlog目录
        String commitLogPath = "/Users/bjhl/IdeaProjects/rocketmq/rocketmq-nameserver/store/commitlog/00000000000000000000";
        //读取commitlog文件内容
        ByteBuffer commitLogBuffer = read(commitLogPath);

        //遍历consumerqueue目录下的所有文件
        File file = new File(consumerPath);
        File[] files = file.listFiles();
        for (File f:files) {
            if (f.isDirectory()){
                File[] listFiles = f.listFiles();
                for (File queuePath:listFiles) {
                    String path = queuePath+"/00000000000000000000";
                    //读取consumerqueue文件内容
                    ByteBuffer buffer = read(path);
                    while (true){
                        //读取消息偏移量和消息长度
                        long offset = (int) buffer.getLong();
                        int size = buffer.getInt();
                        long code = buffer.getLong();
                        if (size==0){
                            break;
                        }
                        //根据偏移量和消息长度，在commitloh文件中读取消息内容
                        MessageExt message = getMessageByOffset(commitLogBuffer,offset,size);
                        if (message!=null){
                            System.out.println("消息主题:"+message.getTopic()+" MessageQueue:"+
                                    message.getQueueId()+" 消息体:"+new String(message.getBody()));
                        }
                    }
                }
            }
        }
    }
    public static MessageExt getMessageByOffset(ByteBuffer commitLog,long offset,int size) throws Exception {
        ByteBuffer slice = commitLog.slice();
        slice.position((int)offset);
        slice.limit((int) (offset+size));
        MessageExt message = QueryLocalCommitLogMessage.decodeCommitLog(slice);
        return message;
    }


    public static ByteBuffer read(String path)throws Exception{
        File file = new File(path);
        FileInputStream fin = new FileInputStream(file);
        byte[] bytes = new byte[(int)file.length()];
        fin.read(bytes);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer;
    }




}
