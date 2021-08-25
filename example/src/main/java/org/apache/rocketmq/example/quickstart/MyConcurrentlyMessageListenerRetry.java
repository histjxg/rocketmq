package org.apache.rocketmq.example.quickstart;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * Created with IntelliJ IDEA. 集群模式下重试
 *
 * @Auther: huangxiaogen
 * @Date: 2021/08/25/上午9:52
 * @Description:
 */

public class MyConcurrentlyMessageListenerRetry implements MessageListenerConcurrently {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        //处理消息消费失败后，重试配置方式
        doConsumeMessage(msgs);
        for (MessageExt msg : msgs) {
            System.out.println(msg.getReconsumeTimes());
        }
        //方式1：返回 ConsumeConcurrentlyStatus.RECONSUME_LATER，消息将重试
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        //方式2：返回 null，消息将重试
//        return null;
        //方式3：直接抛出异常， 消息将重试
//        throw new RuntimeException("Consumer Message exceotion");


    }

    private void doConsumeMessage(List<MessageExt> msgs) {
        System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
    }
}
