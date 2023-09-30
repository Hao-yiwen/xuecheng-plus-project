-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: 192.168.101.65    Database: xc148_orders
-- ------------------------------------------------------
-- Server version	8.0.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;



--
-- Table structure for table `mq_message`
--

DROP TABLE IF EXISTS `mq_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mq_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息id',
  `message_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息类型代码: course_publish ,  media_test',
  `business_key1` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '关联业务信息',
  `business_key2` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '关联业务信息',
  `business_key3` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '关联业务信息',
  `execute_num` int unsigned NOT NULL DEFAULT '0' COMMENT '通知次数',
  `state` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '处理状态，0:初始，1:成功',
  `returnfailure_date` datetime DEFAULT NULL COMMENT '回复失败时间',
  `returnsuccess_date` datetime DEFAULT NULL COMMENT '回复成功时间',
  `returnfailure_msg` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '回复失败内容',
  `execute_date` datetime DEFAULT NULL COMMENT '最近通知时间',
  `stage_state1` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '阶段1处理状态, 0:初始，1:成功',
  `stage_state2` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '阶段2处理状态, 0:初始，1:成功',
  `stage_state3` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '阶段3处理状态, 0:初始，1:成功',
  `stage_state4` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '阶段4处理状态, 0:初始，1:成功',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mq_message`
--

LOCK TABLES `mq_message` WRITE;
/*!40000 ALTER TABLE `mq_message` DISABLE KEYS */;
/*!40000 ALTER TABLE `mq_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mq_message_history`
--

DROP TABLE IF EXISTS `mq_message_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mq_message_history` (
  `id` bigint NOT NULL COMMENT '消息id',
  `message_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息类型代码',
  `business_key1` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '关联业务信息',
  `business_key2` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '关联业务信息',
  `business_key3` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '关联业务信息',
  `execute_num` int unsigned DEFAULT NULL COMMENT '通知次数',
  `state` int(10) unsigned zerofill DEFAULT NULL COMMENT '处理状态，0:初始，1:成功，2:失败',
  `returnfailure_date` datetime DEFAULT NULL COMMENT '回复失败时间',
  `returnsuccess_date` datetime DEFAULT NULL COMMENT '回复成功时间',
  `returnfailure_msg` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '回复失败内容',
  `execute_date` datetime DEFAULT NULL COMMENT '最近通知时间',
  `stage_state1` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `stage_state2` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `stage_state3` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `stage_state4` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mq_message_history`
--

LOCK TABLES `mq_message_history` WRITE;
/*!40000 ALTER TABLE `mq_message_history` DISABLE KEYS */;
INSERT INTO `mq_message_history` (`id`, `message_type`, `business_key1`, `business_key2`, `business_key3`, `execute_num`, `state`, `returnfailure_date`, `returnsuccess_date`, `returnfailure_msg`, `execute_date`, `stage_state1`, `stage_state2`, `stage_state3`, `stage_state4`) VALUES (1,'payresult_notify','16','60201',NULL,0,NULL,NULL,NULL,NULL,NULL,'0','0','0','0'),(15,'payresult_notify','11',NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,'0','0','0','0'),(16,'payresult_notify','15','60201',NULL,0,NULL,NULL,NULL,NULL,NULL,'0','0','0','0');
/*!40000 ALTER TABLE `mq_message_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xc_orders`
--

DROP TABLE IF EXISTS `xc_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xc_orders` (
  `id` bigint NOT NULL COMMENT '订单号',
  `total_price` float(8,2) NOT NULL COMMENT '总价',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `status` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '交易状态',
  `user_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  `order_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单类型',
  `order_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '订单名称',
  `order_descrip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单描述',
  `order_detail` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单明细json',
  `out_business_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '外部系统业务id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `orders_unioue` (`out_business_id`) USING BTREE COMMENT '外部系统的业务id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xc_orders`
--

LOCK TABLES `xc_orders` WRITE;
/*!40000 ALTER TABLE `xc_orders` DISABLE KEYS */;
INSERT INTO `xc_orders` (`id`, `total_price`, `create_date`, `status`, `user_id`, `order_type`, `order_name`, `order_descrip`, `order_detail`, `out_business_id`) VALUES (1577177773194113024,1.00,'2022-10-04 14:04:18','600002','50','60201','测试课程01','购买课程:测试课程01','[{\"goodsId\":2,\"goodsType\":\"60201\",\"goodsName\":\"测试课程01\",\"goodsPrice\":1}]','10'),(1577258681653280768,1.00,'2022-10-04 19:25:48','600002','52','60201','Nacos微服务开发实战','购买课程:Nacos微服务开发实战','[{\"goodsId\":117,\"goodsType\":\"60201\",\"goodsName\":\"Nacos微服务开发实战\",\"goodsPrice\":1}]','11'),(1585094781512269824,11.00,'2022-10-26 10:23:40','600002','51','60201','java零基础入门','购买课程:java零基础入门','[{\"goodsId\":18,\"goodsType\":\"60201\",\"goodsName\":\"java零基础入门\",\"goodsPrice\":11}]','15'),(1623527995495899136,1.00,'2023-02-09 11:43:32','600002','52','60201','Spring Cloud 开发实战','购买课程:Spring Cloud 开发实战','[{\"goodsId\":121,\"goodsType\":\"60201\",\"goodsName\":\"Spring Cloud 开发实战\",\"goodsPrice\":1}]','16');
/*!40000 ALTER TABLE `xc_orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xc_orders_goods`
--

DROP TABLE IF EXISTS `xc_orders_goods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xc_orders_goods` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '订单号',
  `goods_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '商品id',
  `goods_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '商品类型',
  `goods_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '商品名称',
  `goods_price` float(10,2) NOT NULL COMMENT '商品交易价，单位分',
  `goods_detail` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '商品详情json',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xc_orders_goods`
--

LOCK TABLES `xc_orders_goods` WRITE;
/*!40000 ALTER TABLE `xc_orders_goods` DISABLE KEYS */;
INSERT INTO `xc_orders_goods` (`id`, `order_id`, `goods_id`, `goods_type`, `goods_name`, `goods_price`, `goods_detail`) VALUES (2,1577177773194113024,'2','60201','测试课程01',1.00,NULL),(3,1577258681653280768,'117','60201','Nacos微服务开发实战',1.00,NULL),(4,1585094781512269824,'18','60201','java零基础入门',11.00,NULL),(5,1623527995495899136,'121','60201','Spring Cloud 开发实战',1.00,NULL);
/*!40000 ALTER TABLE `xc_orders_goods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xc_pay_record`
--

DROP TABLE IF EXISTS `xc_pay_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xc_pay_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `pay_no` bigint NOT NULL COMMENT '本系统支付交易号',
  `out_pay_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '第三方支付交易流水号',
  `out_pay_channel` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '第三方支付渠道编号',
  `order_id` bigint NOT NULL COMMENT '商品订单号',
  `order_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单名称',
  `total_price` float(8,2) NOT NULL COMMENT '订单总价单位元',
  `currency` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '币种CNY',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `status` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '支付状态',
  `pay_success_time` datetime DEFAULT NULL COMMENT '支付成功时间',
  `user_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `pay_order_unioue2` (`pay_no`) USING BTREE COMMENT '本系统支付交易号',
  UNIQUE KEY `pay_order_unioue` (`out_pay_no`) USING BTREE COMMENT '第三方支付订单号'
) ENGINE=InnoDB AUTO_INCREMENT=1623527995601891330 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xc_pay_record`
--

LOCK TABLES `xc_pay_record` WRITE;
/*!40000 ALTER TABLE `xc_pay_record` DISABLE KEYS */;
INSERT INTO `xc_pay_record` (`id`, `pay_no`, `out_pay_no`, `out_pay_channel`, `order_id`, `order_name`, `total_price`, `currency`, `create_date`, `status`, `pay_success_time`, `user_id`) VALUES (1577177773231415298,1577177773231861760,NULL,NULL,1577177773194113024,'测试课程01',1.00,'CNY','2022-10-04 14:04:18','601001',NULL,'50'),(1577179016003612674,1577179015973519360,NULL,NULL,1577177773194113024,'测试课程01',1.00,'CNY','2022-10-04 14:09:15','601001',NULL,'50'),(1577181370643955713,1577181370624544768,NULL,NULL,1577177773194113024,'测试课程01',1.00,'CNY','2022-10-04 14:18:36','601001',NULL,'50'),(1577182027190972417,1577182027171524608,NULL,NULL,1577177773194113024,'测试课程01',1.00,'CNY','2022-10-04 14:21:13','601001',NULL,'50'),(1577182653388025858,1577182653344460800,NULL,NULL,1577177773194113024,'测试课程01',1.00,'CNY','2022-10-04 14:23:42','601001',NULL,'50'),(1577237009017651202,1577237008990695424,NULL,NULL,1577177773194113024,'测试课程01',1.00,'CNY','2022-10-04 17:59:41','601001',NULL,'50'),(1577239361250500609,1577239361225244672,NULL,NULL,1577177773194113024,'测试课程01',1.00,'CNY','2022-10-04 18:09:02','601002','2022-10-04 18:09:44','50'),(1577419635984793601,1577419635962195968,'2022100522001422760505741092','Alipay',1577258681653280768,'Nacos微服务开发实战',1.00,'CNY','2022-10-05 06:05:23','601002','2022-10-05 06:06:39','52'),(1585094781699452930,1585094781684236288,NULL,NULL,1585094781512269824,'java零基础入门',11.00,'CNY','2022-10-26 10:23:40','601001',NULL,'51'),(1585096384011689985,1585096383987376128,'2022102622001422760505751569','603002',1585094781512269824,'java零基础入门',11.00,'CNY','2022-10-26 10:30:02','601002','2022-10-26 10:32:13','51'),(1585118358242865154,1585118358214623232,'2022102622001422760505751132','603002',1585094781512269824,'java零基础入门',11.00,'CNY','2022-10-26 11:57:21','601002','2022-10-26 11:58:50','51'),(1623527995601891329,1623527995592368128,'2023020922001422760505798854','603002',1623527995495899136,'Spring Cloud 开发实战',1.00,'CNY','2023-02-09 11:43:32','601002','2023-02-09 11:44:42','52');
/*!40000 ALTER TABLE `xc_pay_record` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-02-09 16:51:44
