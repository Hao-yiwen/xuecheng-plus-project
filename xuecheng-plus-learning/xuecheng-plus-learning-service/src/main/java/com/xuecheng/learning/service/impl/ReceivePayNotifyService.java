package com.xuecheng.learning.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.learning.config.PayNotifyConfig;
import com.xuecheng.messagesdk.model.po.MqMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

// 接受消息通知
@Slf4j
@Service
public class ReceivePayNotifyService {
    @RabbitListener(queues = PayNotifyConfig.PAYNOTIFY_QUEUE)
    public void receive(Message message) {
        byte[] body = message.getBody();
        String jsonStr = new String(body);
        //转换成对象
        MqMessage mqMessage = JSON.parseObject(jsonStr, MqMessage.class);
        // 根据消息内容，更新选课记录，向我的课程表插入数据
    }
}
