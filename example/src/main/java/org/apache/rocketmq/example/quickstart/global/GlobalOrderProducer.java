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
package org.apache.rocketmq.example.quickstart.global;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.List;

/**
 * 全局有序
 * This class demonstrates how to send messages to brokers using provided {@link DefaultMQProducer}.
 */
public class GlobalOrderProducer {
    /**
     * 发送消息的经过五个步骤：
     *  1）设置Producer的GroupName。
         *2）设置InstanceName，当一个Jvm需要启动多个Producer的时候，通过设置不同的
         *  InstanceName来区分，不设置的话系统使用默认名称“DEFAULT”。
     * 3）设置发送失败重试次数，当网络出现异常的时候，这个次数影响消息的重复投递次数。想保证
     *  不丢消息，可以设置多重试几次。
     *  4）设置NameServer地址
     *  5）组装消息并发送。
     *
     *
     *
     *
     *
     * @param args
     * @throws MQClientException
     * @throws InterruptedException
     */
    public static void main(String[] args)
            throws MQClientException, InterruptedException, RemotingException, MQBrokerException {

        DefaultMQProducer producer = new
                DefaultMQProducer("producer_grp_07_02");
        producer.setNamesrvAddr("node1:9876");
        producer.start();
        Message message = null;
        for (int i = 0; i < 100; i++) {
            message = new Message("tp_demo_07_01", ("hello lagou" +
                    i).getBytes());
            producer.send(message);
        }
        producer.shutdown();
    }
}
