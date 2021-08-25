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
package org.apache.rocketmq.example.quickstart.order;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.example.quickstart.MyConcurrentlyMessageListenerRetry;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.List;
import java.util.Set;

/**
 * 部分有序消费
 * This example shows how to subscribe and consume messages using providing {@link DefaultMQPushConsumer}.
 */
public class PartOrderConsumer {

    public static void main(String[] args)
            throws InterruptedException, MQClientException, RemotingException, MQBrokerException {

        /*
         * Instantiate with specified consumer group name.
         */
        DefaultMQPullConsumer consumer = new
                DefaultMQPullConsumer("please_rename_unique_group_name");

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

        //可以设置负载均衡算法
        consumer.setAllocateMessageQueueStrategy(new AllocateMessageQueueAveragely());


        // 设置 NameServer 地址，保证  Consumer 可以从 NameServer 获取到 Broker 地址
        consumer.setNamesrvAddr("127.0.0.1:9876");


        Set<MessageQueue> messageQueues =
                consumer.fetchSubscribeMessageQueues("TopicTest");
        System.err.println(messageQueues.size());




        /*
         *  Launch the consumer instance.
         */
        consumer.start();

        for (MessageQueue messageQueue : messageQueues) {
            long nextBeginOffset = 0;
            System.out.println("===============================");
            do {
                PullResult pullResult = consumer.pull(messageQueue, "*",
                        nextBeginOffset, 1);
                if (pullResult == null || pullResult.getMsgFoundList() ==
                        null) break;
                nextBeginOffset = pullResult.getNextBeginOffset();
                List<MessageExt> msgFoundList =
                        pullResult.getMsgFoundList();
                System.out.println(messageQueue.getQueueId() + "\t" +
                        msgFoundList.size());
                for (MessageExt messageExt : msgFoundList) {
                    System.out.println(
                            messageExt.getTopic() + "\t" +
                                    messageExt.getQueueId() + "\t" +
                                    messageExt.getMsgId() + "\t" +
                                    new String(messageExt.getBody())
                    );
                }
            } while (true);
        }
        System.out.printf("Consumer Started.%n");
        consumer.shutdown();
    }
}
