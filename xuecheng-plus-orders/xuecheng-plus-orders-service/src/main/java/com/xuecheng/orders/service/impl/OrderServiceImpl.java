package com.xuecheng.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.utils.IdWorkerUtils;
import com.xuecheng.base.utils.QRCodeUtil;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xuecheng.orders.config.AlipayConfig;
import com.xuecheng.orders.config.PayNotifyConfig;
import com.xuecheng.orders.mapper.XcOrdersGoodsMapper;
import com.xuecheng.orders.mapper.XcOrdersMapper;
import com.xuecheng.orders.mapper.XcPayRecordMapper;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcOrders;
import com.xuecheng.orders.model.po.XcOrdersGoods;
import com.xuecheng.orders.model.po.XcPayRecord;
import com.xuecheng.orders.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    XcOrdersMapper xcOrdersMapper;

    @Autowired
    XcOrdersGoodsMapper xcOrdersGoodsMapper;

    @Autowired
    XcPayRecordMapper xcPayRecordMapper;

    @Autowired
    OrderService myOrderService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    MqMessageService mqMessageService;

    @Value("${pay.qrcodeurl}")
    String qrcodeurl;


    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;

    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;

    @Override
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) {
        // 插入订单表，插入订单主表，订单明细表
        XcOrders xcOrders = saveXcOrders(userId, addOrderDto);
        // 插入支付记录
        XcPayRecord payRecord = createPayRecord(xcOrders);
        Long payNo = payRecord.getPayNo();
        // 生成支付二维码
        QRCodeUtil qrCodeUtil = new QRCodeUtil();
        String url = String.format(qrcodeurl, payNo);
        String qrCode = null;
        try {
            qrCode = qrCodeUtil.createQRCode(url, 200, 200);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord, payRecordDto);
        payRecordDto.setQrcode(qrCode);
        return payRecordDto;
    }

    @Override
    public XcPayRecord getPayRecordByPayno(String payNo) {
        XcPayRecord xcPayRecord = xcPayRecordMapper.selectOne(new LambdaQueryWrapper<XcPayRecord>().eq(XcPayRecord::getPayNo, payNo));
        return xcPayRecord;
    }

    // 保存支付记录
    public XcPayRecord createPayRecord(XcOrders orders) {
        Long orderId = orders.getId();
        // 如果此订单不存在不能添加支付记录
        XcOrders xcOrders = xcOrdersMapper.selectById(orderId);
        if (xcOrders == null) {
            XueChengPlusException.cast("订单不存在");
        }
        String status = xcOrders.getStatus();
        if (status.equals("601002")) {
            XueChengPlusException.cast("此订单已支付");
        }
        XcPayRecord xcPayRecord = new XcPayRecord();
        // 如果此订单支付结果为成功，不再添加支付记录，避免重复支付
        xcPayRecord.setPayNo(IdWorkerUtils.getInstance().nextId());
        xcPayRecord.setOrderId(orderId);
        xcPayRecord.setOrderName(xcOrders.getOrderName());
        xcPayRecord.setTotalPrice(xcOrders.getTotalPrice());
        xcPayRecord.setCurrency("CNY");
        xcPayRecord.setStatus("601001");// 未支付
        xcPayRecord.setUserId(xcOrders.getUserId());
        xcPayRecord.setCreateDate(LocalDateTime.now());
        int insert = xcPayRecordMapper.insert(xcPayRecord);
        if (insert <= 0) {
            XueChengPlusException.cast("插入支付记录失败");
        }
        return xcPayRecord;
    }

    // 插入订单表，插入订单主表，订单明细表
    public XcOrders saveXcOrders(String ueserId, AddOrderDto addOrderDto) {
        // 进行幂等性判断，同一个选课记录只能有一个订单
        XcOrders xcOrders = getOrderByBusinessId(addOrderDto.getOutBusinessId());
        if (xcOrders != null) {
            return xcOrders;
        }
        // 插入订单主表
        xcOrders = new XcOrders();
        // 使用雪花算法生成订单号
        xcOrders.setId(IdWorkerUtils.getInstance().nextId());
        xcOrders.setTotalPrice(addOrderDto.getTotalPrice());
        xcOrders.setCreateDate(LocalDateTime.now());
        xcOrders.setStatus("600001");
        xcOrders.setUserId(ueserId);
        xcOrders.setOrderType("60201");
        xcOrders.setOrderName(addOrderDto.getOrderName());
        xcOrders.setOrderDescrip(addOrderDto.getOrderDescrip());
        xcOrders.setOrderDetail(addOrderDto.getOrderDetail());
        xcOrders.setOutBusinessId(addOrderDto.getOutBusinessId()); // 记录选课表的id
        int insert = xcOrdersMapper.insert(xcOrders);
        if (insert <= 0) {
            XueChengPlusException.cast("添加订单失败");
        }
        Long orderId = xcOrders.getId();
        // 插入订单明细表
        // 将前端传入的json传转成list
        String orderDetail = addOrderDto.getOrderDetail();
        List<XcOrdersGoods> xcOrdersGoods = JSON.parseArray(orderDetail, XcOrdersGoods.class);
        xcOrdersGoods.forEach(goods -> {
            goods.setOrderId(orderId);
            int insert1 = xcOrdersGoodsMapper.insert(goods);
        });

        return xcOrders;
    }

    // 根据业务id查询订单, 业务id就是选课记录表中的主键
    public XcOrders getOrderByBusinessId(String businessId) {
        return xcOrdersMapper.selectOne(new LambdaQueryWrapper<XcOrders>().eq(XcOrders::getOutBusinessId, businessId));
    }

    @Override
    public PayRecordDto queryPayResult(String payNo) {
        // 调用支付宝的接口查询支付接口
        PayStatusDto payStatusDto = queryPayResultFromAlipay(payNo);
        // 拿到支付结果更新订单表和支付状态
        myOrderService.saveAlipayStatus(payStatusDto);
        // 返回最新的支付记录信息
        XcPayRecord payRecordByPayno = getPayRecordByPayno(payNo);
        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecordByPayno, payRecordDto);
        return payRecordDto;
    }

    /**
     * 查询支付结果
     *
     * @param payNo
     * @return
     */
    public PayStatusDto queryPayResultFromAlipay(String payNo) {
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, APP_ID, APP_PRIVATE_KEY, "json", AlipayConfig.CHARSET, ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE); //获得初始化的AlipayClient
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", payNo);
        request.setBizContent(bizContent.toString());
        String body = null;
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (!response.isSuccess()) {
                XueChengPlusException.cast("查询支付结果信息失败");
            }
            body = response.getBody();
        } catch (AlipayApiException e) {
            XueChengPlusException.cast("查询支付结果信息失败");
        }
        Map<String, String> bodyMap = (Map) JSON.parseObject(body, Map.class).get("alipay_trade_query_response");

        // 解析支付结果
        PayStatusDto payStatusDto = new PayStatusDto();
        payStatusDto.setOut_trade_no(payNo);
        payStatusDto.setTrade_no(bodyMap.get("trade_no"));
        payStatusDto.setTrade_status(bodyMap.get("trade_status"));
        payStatusDto.setApp_id(APP_ID);
        payStatusDto.setTotal_amount(bodyMap.get("total_amount"));

        return payStatusDto;
    }

    /**
     * 保存支付结果
     *
     * @param payStatusDto 从支付宝查询到的信息
     */
    @Override
    public void saveAlipayStatus(PayStatusDto payStatusDto) {
        // 支付记录号
        String outTradeNo = payStatusDto.getOut_trade_no();
        XcPayRecord payRecordByPayno = getPayRecordByPayno(outTradeNo);
        if (payRecordByPayno == null) {
            XueChengPlusException.cast("找不到相关的支付记录");
        }
        Long orderId = payRecordByPayno.getOrderId();
        XcOrders xcOrders = xcOrdersMapper.selectById(orderId);
        if (xcOrders == null) {
            XueChengPlusException.cast("找不到相关联的订单");
        }
        // 支付状态
        String statusFromDb = payRecordByPayno.getStatus();
        if (statusFromDb.equals("601002")) {
            // 如果已经成功了
            return;
        }
        // 如果支付成功
        String tradeStatus = payStatusDto.getTrade_status();
        if (tradeStatus.equals("TRADE_SUCCESS")) {// 支付宝返回信息为支付成功
            payRecordByPayno.setStatus("601002");
            payRecordByPayno.setOutPayNo(payStatusDto.getOut_trade_no());
            //第三方支付渠道编号
            payRecordByPayno.setOutPayChannel("Alipay");
            payRecordByPayno.setPaySuccessTime(LocalDateTime.now());
            // 更新支付记录表的状态
            xcPayRecordMapper.updateById(payRecordByPayno);
            // 更新订单表的状态为支付成功
            xcOrders.setStatus("600002");
            xcOrdersMapper.updateById(xcOrders);

            // 消息写到数据库
            MqMessage payresultNotify = mqMessageService.addMessage("payresult_notify", xcOrders.getOutBusinessId(), xcOrders.getOrderType(), null);
            // 发送消息
            notifyPayResult(payresultNotify);
        }
    }

    @Override
    public void notifyPayResult(MqMessage message) {
        String jsonString = JSON.toJSONString(message);
        // 持久化消息
        Message message1 = MessageBuilder.withBody(jsonString.getBytes(StandardCharsets.UTF_8)).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
        Long id = message.getId();
        CorrelationData correlationData = new CorrelationData();
        // 使用correlationData指定回调方法
        correlationData.getFuture().addCallback(result -> {
            if (result.isAck()) {
                //消息发送成功
                log.debug("发送消息成功:{}",jsonString);
                // 将消息从数据库表删除
                mqMessageService.removeById(id);
            } else {
                // 消息发送失败
                log.debug("发送消息失败:{}",jsonString);
            }
        }, ex -> {
            // 发生异常
            log.debug("发送消息失败:{}",jsonString);
        });
        rabbitTemplate.convertAndSend(PayNotifyConfig.PAYNOTIFY_EXCHANGE_FANOUT, "",message1,correlationData);
    }
}
