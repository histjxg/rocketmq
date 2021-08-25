package org.apache.rocketmq.example.quickstart;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * Created with IntelliJ IDEA. 集群模式下不重试
 *
 * @Auther: huangxiaogen
 * @Date: 2021/08/25/上午9:52
 * @Description:
 */

public class MyConcurrentlyMessageListenerNotRetry implements MessageListenerConcurrently {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        try {
            doConsumeMessage(msgs);
        } catch (Throwable e) {
            //捕获消费逻辑中的所有异常，并返回ConsumeConcurrentlyStatus.CONSUME_SUCCESS
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        //消息处理正常，直接返回 ConsumeConcurrentlyStatus.CONSUME_SUCCESS
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;



    }

    private void doConsumeMessage(List<MessageExt> msgs) {
            System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
    }
}
