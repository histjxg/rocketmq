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
package org.apache.rocketmq.client.consumer.rebalance;

import java.util.ArrayList;
import java.util.List;
import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.client.log.ClientLogger;
import org.apache.rocketmq.logging.InternalLogger;
import org.apache.rocketmq.common.message.MessageQueue;

/**
 * Average Hashing queue algorithm
 */
public class AllocateMessageQueueAveragely implements AllocateMessageQueueStrategy {
    private final InternalLogger log = ClientLogger.getLog();

    /**
     *
     * 计算当前消费者应该消费哪些MQ的消息
     * @param consumerGroup current consumer group 当前的消费组
     * @param currentCID current consumer id 当前消费者id
     * @param mqAll message queue set in current topic 当前主题包含的mq集合
     * @param cidAll consumer set in current consumer group 当前消费组包含的消费者id
     * @return
     */

    @Override
    public List<MessageQueue> allocate(String consumerGroup, String currentCID, List<MessageQueue> mqAll,
        List<String> cidAll) {

        if (currentCID == null || currentCID.length() < 1) {
            throw new IllegalArgumentException("currentCID is empty");
        }

        if (mqAll == null || mqAll.isEmpty()) {
            throw new IllegalArgumentException("mqAll is null or mqAll empty");
        }

        if (cidAll == null || cidAll.isEmpty()) {
            throw new IllegalArgumentException("cidAll is null or cidAll empty");
        }

        List<MessageQueue> result = new ArrayList<MessageQueue>();
        if (!cidAll.contains(currentCID)) {
            log.info("[BUG] ConsumerGroup: {} The consumerId: {} not in cidAll: {}",
                consumerGroup,
                currentCID,
                cidAll);
            return result;
        }
        // 获取当前消费者在cidAll集合中的下标
        int index = cidAll.indexOf(currentCID);
        // mqAll对cidAll大小取模
        int mod = mqAll.size() % cidAll.size();
        // 计算每个消费者应该分配到的mq数量
        // 如果mq个数小于等于消费者个数，每个消费者最多分配一个mq
        // 如果mq个数大于消费者个数，
        int averageSize =
            // 如果mq个数小于等于消费组中消费者个数
            mqAll.size() <= cidAll.size() ?
                    // 平均数就是1
                    1
                    :
                    // 否则，看mod和index大小
                    (mod > 0 && index < mod ?
                            // 如果余数大于0并且当前消费者下标小于余数，则当前消费者应该消费平均数个mq+1
                            mqAll.size() / cidAll.size() + 1
                            :
                            // 如果余数大于0并且当前消费者下标大于等于余数，则当前消费者应该消费平均数个mq
                            mqAll.size() / cidAll.size());
        // 计算当前消费者消费mq的起始位置
        int startIndex = (mod > 0 && index < mod) ? index * averageSize : index * averageSize + mod;
        // 计算当前消费者消费mq的跨度，即当前消费者分几个MQ
        int range = Math.min(averageSize, mqAll.size() - startIndex);
        for (int i = 0; i < range; i++) {
            // 分配MQ，放到result集合中返回
            result.add(mqAll.get((startIndex + i) % mqAll.size()));
        }
        return result;
    }

    @Override
    public String getName() {
        return "AVG";
    }
}
