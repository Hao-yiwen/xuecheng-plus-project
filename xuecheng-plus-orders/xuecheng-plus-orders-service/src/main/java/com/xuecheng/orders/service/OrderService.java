package com.xuecheng.orders.service;

import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcPayRecord;

import java.io.IOException;

public interface OrderService {
    /**
     * 创建订单
     * @param userId
     * @param addOrderDto
     * @return
     * @throws IOException
     */
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) throws IOException;

    /**
     * 查询支付记录
     * @param payNo
     * @return
     */
    public XcPayRecord getPayRecordByPayno(String payNo);

    /**
     * 请求支付宝查询支付结果
     * @param payNo
     * @return
     */
    public PayRecordDto queryPayResult(String payNo);

    /**
     * 保存支付状态
     * @param payStatusDto
     */
    public void saveAlipayStatus(PayStatusDto payStatusDto);

    /**
     * 发送通知结果
     * @param message
     */
    public void notifyPayResult(MqMessage message);
}
