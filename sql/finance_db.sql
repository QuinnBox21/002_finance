-- MySQL dump 10.13  Distrib 5.7.44, for Win64 (x86_64)
--
-- Host: localhost    Database: finance_db
-- ------------------------------------------------------
-- Server version	8.0.33

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `fin_advice`
--

DROP TABLE IF EXISTS `fin_advice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fin_advice` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'income, expense, budget, investment, other',
  `priority` int DEFAULT '3' COMMENT '1-5, 5 being highest',
  `read` tinyint(1) DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_advice_user_id` (`user_id`),
  KEY `idx_advice_type` (`type`),
  KEY `idx_advice_read` (`read`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_advice`
--

LOCK TABLES `fin_advice` WRITE;
/*!40000 ALTER TABLE `fin_advice` DISABLE KEYS */;
INSERT INTO `fin_advice` VALUES (1,1,'预算即将超支','您的餐饮类别预算已使用85%，请注意控制支出。','budget',4,0,'2025-03-20 01:30:45','2025-03-20 01:30:45'),(2,1,'投资建议','根据您的收入水平，建议开始考虑投资理财产品，分散资金配置。','investment',3,0,'2025-02-20 01:30:45','2025-02-20 01:30:45'),(3,1,'收入分析','您的主要收入来源较为单一，建议考虑发展副业增加收入来源。','income',3,0,'2025-01-20 01:30:45','2025-01-20 01:30:45'),(4,1,'支出优化','在购物类别的支出占比较高，建议制定购物清单避免冲动消费。','expense',2,1,'2025-01-20 01:30:45','2025-01-20 01:30:45'),(5,1,'预算即将超支','您的医疗类别预算已使用100.00%，请注意控制支出。','budget',4,0,'2025-04-20 04:00:05','2025-04-20 04:00:05'),(6,1,'预算即将超支','您的医疗类别预算已使用100.00%，请注意控制支出。','budget',4,0,'2025-04-20 12:22:30','2025-04-20 12:22:30'),(7,1,'预算即将超支','您的医疗类别预算已使用100.00%，请注意控制支出。','budget',4,0,'2025-04-20 12:50:50','2025-04-20 12:50:50');
/*!40000 ALTER TABLE `fin_advice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_budget`
--

DROP TABLE IF EXISTS `fin_budget`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fin_budget` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `category` varchar(50) NOT NULL COMMENT '预算类别',
  `amount` decimal(10,2) NOT NULL COMMENT '预算金额',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `month` varchar(7) NOT NULL COMMENT '预算月份，格式：YYYY-MM',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='预算表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_budget`
--

LOCK TABLES `fin_budget` WRITE;
/*!40000 ALTER TABLE `fin_budget` DISABLE KEYS */;
INSERT INTO `fin_budget` VALUES (1,1,'餐饮',1000.00,'日常餐饮开销','2025-04','2025-04-19 23:21:46','2025-04-19 23:21:46'),(2,1,'购物',2000.00,'购买生活用品','2025-04','2025-04-19 23:21:46','2025-04-19 23:21:46'),(3,2,'交通',500.00,'公交地铁费用','2025-04','2025-04-19 23:21:46','2025-04-19 23:21:46'),(4,2,'娱乐',800.00,'看电影、唱 KTV','2025-04','2025-04-19 23:21:46','2025-04-19 23:21:46'),(5,3,'医疗',1500.00,'买药和体检费用','2025-04','2025-04-19 23:21:46','2025-04-19 23:21:46'),(6,3,'教育',3000.00,'购买学习资料','2025-04','2025-04-19 23:21:46','2025-04-19 23:21:46'),(7,1,'医疗',100.00,'防止生病哦','2025-04','2025-04-20 02:09:31','2025-04-20 02:09:31');
/*!40000 ALTER TABLE `fin_budget` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_transaction`
--

DROP TABLE IF EXISTS `fin_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fin_transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '交易记录ID，自增主键',
  `user_id` bigint NOT NULL COMMENT '关联的用户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '交易金额',
  `type` tinyint NOT NULL COMMENT '交易类型：0-支出，1-收入',
  `category` varchar(50) NOT NULL COMMENT '交易类别，如：工资、餐饮、购物等',
  `remark` varchar(255) DEFAULT NULL COMMENT '交易备注说明',
  `date` date NOT NULL COMMENT '交易发生日期',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`,`date`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='交易记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_transaction`
--

LOCK TABLES `fin_transaction` WRITE;
/*!40000 ALTER TABLE `fin_transaction` DISABLE KEYS */;
INSERT INTO `fin_transaction` VALUES (1,1,1800.00,1,'工资','每月工资','2025-04-01','2025-04-15 13:19:23'),(2,1,200.00,0,'餐饮','午餐消费','2025-04-02','2025-04-15 13:19:23'),(3,2,300.00,1,'收入','项目奖金','2025-04-05','2025-04-15 13:19:23'),(4,2,50.00,0,'购物','购买书籍','2025-04-06','2025-04-15 13:19:23'),(5,3,1000.00,1,'收入','销售提成','2025-04-10','2025-04-15 13:19:23'),(6,3,300.00,0,'娱乐','电影票','2025-04-11','2025-04-15 13:19:23'),(7,1,30.00,0,'购物','法国建行','2025-04-15','2025-04-15 22:49:49'),(8,1,30.00,0,'交通','打车','2025-04-20','2025-04-20 02:05:03'),(9,1,100.00,0,'医疗','生病了啊','2025-04-20','2025-04-20 02:09:49');
/*!40000 ALTER TABLE `fin_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_user`
--

DROP TABLE IF EXISTS `fin_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fin_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID，自增主键',
  `username` varchar(50) NOT NULL COMMENT '用户名，唯一标识',
  `password` varchar(255) NOT NULL COMMENT '用户密码，使用BCrypt加密',
  `email` varchar(100) NOT NULL COMMENT '用户邮箱地址',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '账户创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `idx_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_user`
--

LOCK TABLES `fin_user` WRITE;
/*!40000 ALTER TABLE `fin_user` DISABLE KEYS */;
INSERT INTO `fin_user` VALUES (1,'john_doe','$2a$10$/rYvkfn3xhq2sUsBJsCKBONYum5.HKS4chdd8JYUtI70NVw6GWsBK','john.doe@example.com','2025-04-15 13:19:08'),(2,'jane_smith','$2a$10$/rYvkfn3xhq2sUsBJsCKBONYum5.HKS4chdd8JYUtI70NVw6GWsBK','jane.smith@example.com','2025-04-15 13:19:08'),(3,'alice_wang','$2a$10$/rYvkfn3xhq2sUsBJsCKBONYum5.HKS4chdd8JYUtI70NVw6GWsBK','alice.wang@example.com','2025-04-15 13:19:08'),(4,'bob_liu','$2a$10$/rYvkfn3xhq2sUsBJsCKBONYum5.HKS4chdd8JYUtI70NVw6GWsBK','bob.liu@example.com','2025-04-15 13:19:08'),(5,'test','$2a$10$/rYvkfn3xhq2sUsBJsCKBONYum5.HKS4chdd8JYUtI70NVw6GWsBK','1926707544@qq.com','2025-04-15 13:20:09'),(10,'demo','$2a$10$SCwvqhd9vEixfW/8tFBtYut1N5aYuLn9VoRMann2RtKis7DtCYmYe','c_qingyun2002@163.com','2025-04-20 02:37:51');
/*!40000 ALTER TABLE `fin_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'finance_db'
--

--
-- Dumping routines for database 'finance_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-20 13:00:26
