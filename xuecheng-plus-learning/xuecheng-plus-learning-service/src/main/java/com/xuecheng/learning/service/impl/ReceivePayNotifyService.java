package com.xuecheng.learning.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.learning.config.PayNotifyConfig;
import com.xuecheng.learning.service.MyCourseTableService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// 接受消息通知
@Slf4j
@Service
public class ReceivePayNotifyService {
    @Autowired
    MyCourseTableService myCourseTableService;

    @RabbitListener(queues = PayNotifyConfig.PAYNOTIFY_QUEUE)
    public void receive(Message message) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        byte[] body = message.getBody();
        String jsonStr = new String(body);
        //转换成对象
        MqMessage mqMessage = JSON.parseObject(jsonStr, MqMessage.class);
        // 解析消息内容
        // 选课id
        String chooseCourseId = mqMessage.getBusinessKey1();
        // 订单类型
        String orderType = mqMessage.getBusinessKey2();
        // 学习中心只需要购买课程的支付订单结果
        if(orderType.equals("60201")){
            // 根据消息内容，更新选课记录，向我的课程表插入数据
            boolean b = myCourseTableService.saveChooseCourseSuccess(chooseCourseId);
            if(!b){
                XueChengPlusException.cast("保存选课记录失败");
            }
        }
    }
}
