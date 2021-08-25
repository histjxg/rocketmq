package org.apache.rocketmq.example.quickstart.querymessage;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
public class QueryingMessageDemo {
    public static void main(String[] args) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("consumer_grp_09_01");
        consumer.setNamesrvAddr("node1:9876");
        consumer.start();
        MessageExt message = consumer.viewMessage("tp_demo_08", "0A4E00A7178878308DB150A780BB0000");
        System.out.println(message);
        System.out.println(message.getMsgId()); consumer.shutdown();
    }
}