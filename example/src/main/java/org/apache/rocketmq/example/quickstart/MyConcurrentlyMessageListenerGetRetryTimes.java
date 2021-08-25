package org.apache.rocketmq.example.quickstart;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * Created with IntelliJ IDEA. 集群模式获取重试次数
 *
 * @Auther: huangxiaogen
 * @Date: 2021/08/25/上午9:52
 * @Description:
 */

public class MyConcurrentlyMessageListenerGetRetryTimes implements MessageListenerConcurrently {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        for (MessageExt msg : msgs) {
            System.out.println(msg.getReconsumeTimes());
        }
        doConsumeMessage(msgs);
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

    }

    private void doConsumeMessage(List<MessageExt> msgs) {
            System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
    }
}
