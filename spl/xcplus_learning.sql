-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: 192.168.101.65    Database: xc148_learning
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
-- Table structure for table `xc_choose_course`
--

DROP TABLE IF EXISTS `xc_choose_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xc_choose_course` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `course_id` bigint NOT NULL COMMENT '课程id',
  `course_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '课程名称',
  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户id',
  `company_id` bigint NOT NULL COMMENT '机构id',
  `order_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '选课类型',
  `create_date` datetime NOT NULL COMMENT '添加时间',
  `course_price` float(10,2) NOT NULL COMMENT '课程价格',
  `valid_days` int NOT NULL COMMENT '课程有效期(天)',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '选课状态',
  `validtime_start` datetime NOT NULL COMMENT '开始服务时间',
  `validtime_end` datetime NOT NULL COMMENT '结束服务时间',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xc_choose_course`
--

LOCK TABLES `xc_choose_course` WRITE;
/*!40000 ALTER TABLE `xc_choose_course` DISABLE KEYS */;
INSERT INTO `xc_choose_course` (`id`, `course_id`, `course_name`, `user_id`, `company_id`, `order_type`, `create_date`, `course_price`, `valid_days`, `status`, `validtime_start`, `validtime_end`, `remarks`) VALUES (16,121,'Spring Cloud 开发实战','52',1232141425,'700002','2023-02-09 11:43:32',1.00,365,'701001','2023-02-09 11:43:32','2024-02-09 11:43:32',NULL),(17,121,'Spring Cloud 开发实战','52',1232141425,'700002','2023-02-09 11:49:06',1.00,365,'701002','2023-02-09 11:49:06','2024-02-09 11:49:06',NULL);
/*!40000 ALTER TABLE `xc_choose_course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xc_course_tables`
--

DROP TABLE IF EXISTS `xc_course_tables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xc_course_tables` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `choose_course_id` bigint NOT NULL COMMENT '选课记录id',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户id',
  `course_id` bigint NOT NULL COMMENT '课程id',
  `company_id` bigint NOT NULL COMMENT '机构id',
  `course_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '课程名称',
  `course_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '课程类型',
  `create_date` datetime NOT NULL COMMENT '添加时间',
  `validtime_start` datetime DEFAULT NULL COMMENT '开始服务时间',
  `validtime_end` datetime NOT NULL COMMENT '到期时间',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `course_tables_unique` (`user_id`,`course_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xc_course_tables`
--

LOCK TABLES `xc_course_tables` WRITE;
/*!40000 ALTER TABLE `xc_course_tables` DISABLE KEYS */;
INSERT INTO `xc_course_tables` (`id`, `choose_course_id`, `user_id`, `course_id`, `company_id`, `course_name`, `course_type`, `create_date`, `validtime_start`, `validtime_end`, `update_date`, `remarks`) VALUES (11,16,'52',121,1232141425,'Spring Cloud 开发实战','700002','2023-02-09 11:44:48','2023-02-09 11:43:32','2024-02-09 11:43:32',NULL,NULL);
/*!40000 ALTER TABLE `xc_course_tables` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xc_learn_record`
--

DROP TABLE IF EXISTS `xc_learn_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xc_learn_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '课程id',
  `course_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '课程名称',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户id',
  `learn_date` datetime DEFAULT NULL COMMENT '最近学习时间',
  `learn_length` bigint DEFAULT NULL COMMENT '学习时长',
  `teachplan_id` bigint DEFAULT NULL COMMENT '章节id',
  `teachplan_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '章节名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `learn_record_unique` (`course_id`,`user_id`,`teachplan_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xc_learn_record`
--

LOCK TABLES `xc_learn_record` WRITE;
/*!40000 ALTER TABLE `xc_learn_record` DISABLE KEYS */;
INSERT INTO `xc_learn_record` (`id`, `course_id`, `course_name`, `user_id`, `learn_date`, `learn_length`, `teachplan_id`, `teachplan_name`) VALUES (1,123,'SpringBoot实战','52','2022-10-06 11:31:19',22,222,'入门程序'),(2,121,'Java编程思想','52','2022-10-06 11:31:57',10,333,'Java学习路径'),(7,117,'Nacos微服务开发实战','52','2022-10-06 13:18:24',0,269,'1.1 什么是配置中心'),(8,117,'Nacos微服务开发实战','52','2022-10-06 13:18:23',0,270,'1.2Nacos简介'),(9,117,'Nacos微服务开发实战','52','2022-10-06 13:18:25',0,271,'1.3安装Nacos Server'),(10,117,'Nacos微服务开发实战','52','2022-10-06 13:18:27',0,272,'1.4Nacos配置入门'),(11,117,'Nacos微服务开发实战','52','2022-10-06 13:41:08',0,275,'2.1什么是服务发现'),(12,117,'Nacos微服务开发实战','52','2022-10-06 13:18:46',0,276,'2.2服务发现快速入门');
/*!40000 ALTER TABLE `xc_learn_record` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-02-09 16:51:22
