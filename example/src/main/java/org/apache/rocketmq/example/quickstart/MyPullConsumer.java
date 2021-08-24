package org.apache.rocketmq.example.quickstart;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: huangxiaogen
 * @Date: 2021/08/24/下午7:07
 * @Description:
 */

public class MyPullConsumer {
    public static void main(String[] args)
            throws InterruptedException, MQClientException, RemotingException, MQBrokerException {


        //DefaultMQPullConsumer有两个辅助方法可以帮助实现负载均衡，一个是
        //registerMessageQueueListener函数，一个是MQPullConsumerScheduleService
        //一个是registerMessageQueueListener函数
        /**
         * 第二种负载均衡方式 是MQPullConsumerScheduleService
         * Pull Consumer可以看到所有的Message Queue，而且从哪个Message Queue读取消息
         * 读消息时的Offset都由使用者控制，使用者可以实现任何特殊方式的负载均衡。
         */
        DefaultMQPullConsumer consumerPull = new
                DefaultMQPullConsumer("consumer_pull_grp_01");
        consumerPull.setNamesrvAddr("node1:9876");
        consumerPull.start();

        Set<MessageQueue> topicTest = consumerPull.fetchSubscribeMessageQueues("TopicTest");
        for (MessageQueue messageQueue : topicTest) {
            // 指定从哪个MQ拉取数据
            PullResult result = consumerPull.pull(messageQueue, "*", 0L, 10);
            List<MessageExt> msgFoundList = result.getMsgFoundList();
            for (MessageExt messageExt : msgFoundList) {
                System.out.println(messageExt);
            }

        }
        System.out.printf("Consumer Started.%n");
        consumerPull.shutdown();
    }
}
