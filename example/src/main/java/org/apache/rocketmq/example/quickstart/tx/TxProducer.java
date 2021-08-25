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
package org.apache.rocketmq.example.quickstart.tx;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * 全局有序
 * This class demonstrates how to send messages to brokers using provided {@link DefaultMQProducer}.
 */
public class TxProducer {
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

        TransactionListener listener = new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
                // 当发送事务消息prepare(half)成功后，调用该方法执行本地事务
                System.out.println("执行本地事务，参数为：" + arg);
                try {
                    Thread.sleep(100000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                    return LocalTransactionState.ROLLBACK_MESSAGE;
                return LocalTransactionState.COMMIT_MESSAGE;
                }
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                // 如果没有收到生产者发送的Half Message的响应，broker发送请求到生产者回查生产者本地事务的状态
                // 该方法用于获取本地事务执行的状态。
                System.out.println("检查本地事务的状态：" + msg);
                return LocalTransactionState.COMMIT_MESSAGE;
                // return LocalTransactionState.ROLLBACK_MESSAGE;

            }

        };
        TransactionMQProducer producer = new TransactionMQProducer("tx_producer_grp_08");
        producer.setTransactionListener(listener);
        producer.setNamesrvAddr("node1:9876");
        producer.start(); Message message = null;
        message = new Message("tp_demo_08", "hello lagou - tx".getBytes());
        producer.sendMessageInTransaction(message, " {\"name\":\"zhangsan\"}");
    }
}
