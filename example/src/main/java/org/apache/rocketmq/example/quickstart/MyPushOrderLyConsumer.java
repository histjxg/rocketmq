/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.example.quickstart;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.List;
import java.util.Set;

/**
 * This example shows how to subscribe and consume messages using providing {@link DefaultMQPushConsumer}.
 */
public class MyPushOrderLyConsumer {

    public static void main(String[] args)
            throws InterruptedException, MQClientException, RemotingException, MQBrokerException {

        /*
         * Instantiate with specified consumer group name.
         */
        /**
         * DefaultMQPushConsumer的负载均衡过程不需要使用者操心，客户端程序会自动处理，每个
         * DefaultMQPushConsumer启动后，会马上会触发一个doRebalance动作
         * 而且在同一个ConsumerGroup里加入新的DefaultMQPush-Consumer时，各个Consumer都会被触发
         * doRebalance动作。
         *
         */
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name_4");


        /*
         * Specify name server addresses.
         * <p/>
         *
         * Alternatively, you may specify name server addresses via exporting environmental variable: NAMESRV_ADDR
         * <pre>
         * {@code
         * consumer.setNamesrvAddr("name-server1-ip:9876;name-server2-ip:9876");
         * }
         * </pre>
         */

        /*
         * Specify where to start in case the specified consumer group is a brand new one.
         */

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        //可以设置负载均衡算法
        consumer.setAllocateMessageQueueStrategy(new AllocateMessageQueueAveragely());

        /*
         * Subscribe one more more topics to consume.
         */
        consumer.subscribe("TopicTest", "*");
        // 设置 NameServer 地址，保证  Consumer 可以从 NameServer 获取到 Broker 地址
        consumer.setNamesrvAddr("127.0.0.1:9876");

        /*
         *  Register callback to execute on arrival of messages fetched from brokers.
         */
        //顺序消费
        consumer.registerMessageListener(new MessageListenerOrderly() {

            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                for (MessageExt msg : msgs) {
                    System.out.println(msg.getMsgId() + "\t" + msg.getQueueId() +
                            "\t" + new String(msg.getBody()));
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }

        });





        /*
         *  Launch the consumer instance.
         */
        consumer.start();

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
