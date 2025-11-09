-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: HomestayBooking
-- ------------------------------------------------------
-- Server version	8.0.44

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
-- Table structure for table `booking`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking` (
                           `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                           `user_id` bigint unsigned DEFAULT NULL,
                           `homestay_id` bigint unsigned NOT NULL,
                           `check_in` date NOT NULL,
                           `check_out` date NOT NULL,
                           `nights` int GENERATED ALWAYS AS ((to_days(`check_out`) - to_days(`check_in`))) STORED,
                           `total_price` decimal(14,2) NOT NULL,
                           `status` varchar(40) COLLATE utf8mb4_general_ci DEFAULT 'pending',
                           `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id`),
                           KEY `user_id` (`user_id`),
                           KEY `homestay_id` (`homestay_id`),
                           CONSTRAINT `booking_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL,
                           CONSTRAINT `booking_ibfk_2` FOREIGN KEY (`homestay_id`) REFERENCES `homestay` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking`
--

LOCK TABLES `booking` WRITE;
/*!40000 ALTER TABLE `booking` DISABLE KEYS */;
INSERT INTO `booking` (`id`, `user_id`, `homestay_id`, `check_in`, `check_out`, `total_price`, `status`, `created_at`) VALUES (1,2,1,'2025-11-01','2025-11-05',10000000.00,'confirmed','2025-10-20 10:00:00'),(2,3,2,'2025-11-10','2025-11-15',9000000.00,'confirmed','2025-10-21 11:00:00'),(3,1,3,'2025-11-20','2025-11-25',6000000.00,'pending','2025-10-22 14:00:00'),(4,1,1,'2025-10-01','2025-10-03',1900000.00,'completed','2025-10-27 17:45:27'),(5,4,2,'2025-10-10','2025-10-12',2400000.00,'completed','2025-10-27 17:45:27'),(6,5,3,'2025-11-05','2025-11-07',5000000.00,'pending','2025-10-27 17:45:27'),(7,8,4,'2025-09-20','2025-09-22',1300000.00,'cancelled','2025-10-27 17:45:27'),(8,4,1,'2025-11-09','2025-11-11',5000000.00,'pending',NULL);
/*!40000 ALTER TABLE `booking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_log_admin`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_log_admin` (
                                  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                  `admin_id` bigint unsigned NOT NULL,
                                  `session_id` bigint unsigned NOT NULL,
                                  `action` enum('view','warn_user','close_session','delete_message') COLLATE utf8mb4_general_ci DEFAULT 'view',
                                  `note` text COLLATE utf8mb4_general_ci,
                                  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`id`),
                                  KEY `admin_id` (`admin_id`),
                                  KEY `session_id` (`session_id`),
                                  CONSTRAINT `chat_log_admin_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                                  CONSTRAINT `chat_log_admin_ibfk_2` FOREIGN KEY (`session_id`) REFERENCES `chat_session` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_log_admin`
--

LOCK TABLES `chat_log_admin` WRITE;
/*!40000 ALTER TABLE `chat_log_admin` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_log_admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_message`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_message` (
                                `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                `session_id` bigint unsigned NOT NULL,
                                `sender_id` bigint unsigned DEFAULT NULL,
                                `is_from_bot` tinyint(1) DEFAULT '0',
                                `message_type` enum('text','image','file','system') COLLATE utf8mb4_general_ci DEFAULT 'text',
                                `content` text COLLATE utf8mb4_general_ci NOT NULL,
                                `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                `is_read` tinyint(1) DEFAULT '0',
                                PRIMARY KEY (`id`),
                                KEY `session_id` (`session_id`),
                                KEY `sender_id` (`sender_id`),
                                CONSTRAINT `chat_message_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `chat_session` (`id`) ON DELETE CASCADE,
                                CONSTRAINT `chat_message_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_message`
--

LOCK TABLES `chat_message` WRITE;
/*!40000 ALTER TABLE `chat_message` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_session`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_session` (
                                `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                `customer_id` bigint unsigned DEFAULT NULL,
                                `host_id` bigint unsigned DEFAULT NULL,
                                `initiated_by` enum('customer','host','bot') COLLATE utf8mb4_general_ci DEFAULT 'customer',
                                `is_with_bot` tinyint(1) DEFAULT '0',
                                `status` enum('active','closed','archived') COLLATE utf8mb4_general_ci DEFAULT 'active',
                                `started_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                `ended_at` datetime DEFAULT NULL,
                                PRIMARY KEY (`id`),
                                KEY `customer_id` (`customer_id`),
                                KEY `host_id` (`host_id`),
                                CONSTRAINT `chat_session_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `user` (`id`) ON DELETE SET NULL,
                                CONSTRAINT `chat_session_ibfk_2` FOREIGN KEY (`host_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_session`
--

LOCK TABLES `chat_session` WRITE;
/*!40000 ALTER TABLE `chat_session` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_session` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 */ /*!50003 TRIGGER `trg_chat_session_close` AFTER UPDATE ON `chat_session` FOR EACH ROW BEGIN

    IF NEW.status = 'closed' AND OLD.status <> 'closed' THEN

        INSERT INTO `notification` (`user_id`, `title`, `body`, `meta`)

        VALUES (NEW.customer_id, 'Phiên chat đã kết thúc',

                CONCAT('Phiên chat ID ', NEW.id, ' đã được kết thúc.'),

                JSON_OBJECT('session_id', NEW.id));

    END IF;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `chatbot_training`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chatbot_training` (
                                    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                    `question_pattern` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                                    `answer_template` text COLLATE utf8mb4_general_ci NOT NULL,
                                    `category` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
                                    `is_active` tinyint(1) DEFAULT '1',
                                    `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chatbot_training`
--

LOCK TABLES `chatbot_training` WRITE;
/*!40000 ALTER TABLE `chatbot_training` DISABLE KEYS */;
/*!40000 ALTER TABLE `chatbot_training` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `complaint`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `complaint` (
                             `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                             `user_id` bigint unsigned NOT NULL,
                             `booking_id` bigint unsigned DEFAULT NULL,
                             `homestay_id` bigint unsigned DEFAULT NULL,
                             `subject` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
                             `content` text COLLATE utf8mb4_general_ci,
                             `status` varchar(20) COLLATE utf8mb4_general_ci DEFAULT 'pending',
                             `assigned_admin_id` bigint unsigned DEFAULT NULL,
                             `admin_response` text COLLATE utf8mb4_general_ci,
                             `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                             `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             KEY `user_id` (`user_id`),
                             KEY `booking_id` (`booking_id`),
                             KEY `homestay_id` (`homestay_id`),
                             KEY `assigned_admin_id` (`assigned_admin_id`),
                             CONSTRAINT `complaint_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                             CONSTRAINT `complaint_ibfk_2` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`) ON DELETE SET NULL,
                             CONSTRAINT `complaint_ibfk_3` FOREIGN KEY (`homestay_id`) REFERENCES `homestay` (`id`) ON DELETE SET NULL,
                             CONSTRAINT `complaint_ibfk_4` FOREIGN KEY (`assigned_admin_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `complaint`
--

LOCK TABLES `complaint` WRITE;
/*!40000 ALTER TABLE `complaint` DISABLE KEYS */;
INSERT INTO `complaint` VALUES (1,1,1,1,'Phàn nàn về dọn phòng','Phòng không được dọn kỹ khi nhận','resolved',6,NULL,'2025-10-27 17:45:27','2025-10-27 17:45:27'),(2,4,2,2,'Không nhận được hóa đơn','Tôi cần hóa đơn VAT cho chuyến đi','pending',6,NULL,'2025-10-27 17:45:27','2025-10-27 17:45:27');
/*!40000 ALTER TABLE `complaint` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `homestay`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `homestay` (
                            `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                            `user_id` bigint unsigned DEFAULT NULL,
                            `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                            `description` text COLLATE utf8mb4_general_ci,
                            `address` text COLLATE utf8mb4_general_ci,
                            `city` varchar(150) COLLATE utf8mb4_general_ci DEFAULT NULL,
                            `lat` double DEFAULT NULL,
                            `long` double DEFAULT NULL,
                            `capacity` smallint DEFAULT '2',
                            `num_rooms` smallint DEFAULT '1',
                            `bathroom_count` smallint DEFAULT '1',
                            `base_price` decimal(12,2) NOT NULL,
                            `amenities` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
                            `status` tinyint DEFAULT '0',
                            `approved_by` bigint unsigned DEFAULT NULL,
                            `approved_at` datetime DEFAULT NULL,
                            `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                            `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            `is_deleted` tinyint(1) DEFAULT '0',
                            PRIMARY KEY (`id`),
                            KEY `user_id` (`user_id`),
                            KEY `approved_by` (`approved_by`),
                            CONSTRAINT `homestay_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL,
                            CONSTRAINT `homestay_ibfk_2` FOREIGN KEY (`approved_by`) REFERENCES `user` (`id`) ON DELETE SET NULL,
                            CONSTRAINT `homestay_chk_1` CHECK (json_valid(`amenities`)),
                            CONSTRAINT `homestay_chk_2` CHECK ((`status` in (0,1,2,3,4)))
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `homestay`
--

LOCK TABLES `homestay` WRITE;
/*!40000 ALTER TABLE `homestay` DISABLE KEYS */;
INSERT INTO `homestay` VALUES (1,1,'Villa Biển Đà Nẵng','Villa sang trọng view biển, gần bãi tắm Mỹ Khê','123 Võ Nguyên Giáp, Sơn Trà','Đà Nẵng',16.0471,108.2376,8,3,2,2500000.00,'{\"wifi\": true, \"pool\": true, \"parking\": true, \"ac\": true, \"kitchen\": true}',2,1,'2025-01-15 10:00:00','2025-01-10 08:30:00','2025-01-15 10:00:00',0),(2,1,'Nhà Gỗ Sapa View Núi','Nhà gỗ ấm cúng với view núi tuyệt đẹp, gần trung tâm Sapa','45 Phố Cầu Mây, Sapa','Lào Cai',22.3364,103.8438,6,2,1,1800000.00,'{\"wifi\": true, \"heater\": true, \"fireplace\": true, \"mountain_view\": true}',3,1,'2025-01-20 14:30:00','2025-01-18 09:00:00','2025-10-27 17:23:57',0),(3,2,'Căn Hộ Phố Cổ Hà Nội','Căn hộ hiện đại ngay trung tâm phố cổ Hà Nội','78 Hàng Bạc, Hoàn Kiếm','Hà Nội',21.0285,105.8542,4,1,1,1200000.00,'{\"wifi\": true, \"ac\": true, \"elevator\": true, \"city_view\": true}',2,1,'2025-02-01 11:00:00','2025-01-28 16:00:00','2025-02-01 11:00:00',0),(4,2,'Bungalow Phú Quốc Beach','Bungalow riêng tư bên bãi biển Phú Quốc','234 Bãi Trường, Phú Quốc','Kiên Giang',10.2275,103.9707,5,2,1,2200000.00,'{\"wifi\": true, \"beach_access\": true, \"bbq\": true, \"ac\": true, \"parking\": true}',2,1,'2025-02-05 09:30:00','2025-02-02 10:00:00','2025-02-05 09:30:00',0),(5,3,'Homestay Đà Lạt Romantic','Homestay phong cách vintage, view vườn hoa đẹp','56 Đường Trần Phú, Phường 4','Lâm Đồng',11.9404,108.4583,6,2,2,1500000.00,'{\"wifi\": true, \"garden\": true, \"parking\": true, \"heater\": true, \"breakfast\": true}',2,1,'2025-02-10 15:00:00','2025-02-07 08:00:00','2025-02-10 15:00:00',0),(6,3,'Studio Hội An Ancient Town','Studio đẹp trong phố cổ Hội An','12 Nguyễn Thái Học, Minh An','Quảng Nam',15.8801,108.338,2,1,1,900000.00,'{\"wifi\": true, \"ac\": true, \"kitchen\": true, \"ancient_town\": true}',2,1,'2025-02-12 10:30:00','2025-02-10 12:00:00','2025-02-12 10:30:00',0),(7,1,'Villa Nha Trang Chờ Duyệt','Villa đang chờ admin duyệt','89 Trần Phú, Nha Trang','Khánh Hòa',12.2388,109.1967,10,4,3,3000000.00,'{\"wifi\": true, \"pool\": true, \"beach_view\": true}',1,NULL,NULL,'2025-10-20 14:00:00','2025-10-20 14:00:00',0),(8,2,'Căn Hộ Vũng Tàu Tạm Ẩn','Căn hộ đang tạm ẩn để sửa chữa','67 Thùy Vân, Vũng Tàu','Bà Rịa - Vũng Tàu',10.346,107.0842,4,1,1,1100000.00,'{\"wifi\": true, \"ac\": true, \"sea_view\": true}',3,1,'2025-01-25 10:00:00','2025-01-25 10:00:00','2025-10-22 16:00:00',0),(9,3,'Villa Bị Khóa Test','Villa test trạng thái bị khóa','100 Test Street','Hồ Chí Minh',10.8231,106.6297,8,3,2,2000000.00,'{\"wifi\": true}',4,1,'2025-01-10 10:00:00','2025-01-10 10:00:00','2025-10-23 10:00:00',0),(12,1,'Test Homestay',NULL,NULL,'Hà Nội',NULL,NULL,2,1,1,1000000.00,NULL,1,NULL,NULL,'2025-10-26 16:56:47','2025-10-26 16:56:47',0),(13,2,'Căn Hộ Phố Cổ Hà Nội','Căn hộ hiện đại ngay trung tâm phố cổ Hà Nội','78 Hàng Bạc, Hoàn Kiếm','Hà Nội',21.0285,105.8542,7,2,1,1500000.00,'{\"wifi\": true, \"ac\": true, \"elevator\": true, \"city_view\": true}',1,NULL,NULL,'2025-10-27 00:24:28','2025-10-27 00:24:28',0),(14,1,'Villa Biển Nha Trang Mới','Villa cao cấp 3 phòng ngủ, view biển tuyệt đẹp, gần trung tâm thành phố Nha Trang','123 Trần Phú, Vĩnh Nguyên, Nha Trang','Khánh Hòa',12.2388,109.1967,8,3,2,3500000.00,'{\"wifi\": true, \"pool\": true, \"parking\": true, \"ac\": true, \"sea_view\": true, \"kitchen\": true}',1,NULL,NULL,'2025-10-27 00:25:21','2025-10-27 00:25:21',0),(15,2,'Homestay Bình An','Homestay gần biển, phong cách hiện đại','123 Đường Biển, TP Nha Trang','Nha Trang',12.2501,109.1943,4,2,2,950000.00,'[\"wifi\", \"điều hòa\", \"máy nước nóng\"]',2,6,'2025-10-27 17:45:27','2025-10-27 17:45:27','2025-10-27 17:45:27',0),(16,3,'Cường Retreat','Homestay sân vườn rộng, thích hợp nghỉ dưỡng','45 Đường Hoa Phượng, TP Đà Lạt','Đà Lạt',11.9405,108.4583,6,3,3,1200000.00,'[\"vườn\", \"wifi\", \"bếp\", \"view đồi\"]',2,6,'2025-10-27 17:45:27','2025-10-27 17:45:27','2025-10-27 17:45:27',0),(17,2,'Villa Biển Xanh','Biệt thự có hồ bơi riêng, gần biển','12 Nguyễn Huệ, TP Đà Nẵng','Đà Nẵng',16.0544,108.2022,8,4,4,2500000.00,'[\"hồ bơi\", \"bếp\", \"BBQ\", \"wifi\"]',2,6,'2025-10-27 17:45:27','2025-10-27 17:45:27','2025-10-27 17:45:27',0),(18,3,'Homestay ABC Updated','Mô tả mới','123 Đường XYZ, Quận ABC','Đà Nẵng',10.7765,106.7009,2,1,1,0.00,'\"{\\\"wifi\\\": true, \\\"parking\\\": true, \\\"pool\\\": true}\"',2,1,'2025-11-07 11:59:31','2025-10-27 17:45:27','2025-11-07 18:59:32',0),(19,7,'Khôi Homestay Hà Nội','Căn hộ kiểu studio hiện đại','56 Hoàng Hoa Thám, Hà Nội','Hà Nội',21.0285,105.8542,2,1,1,800000.00,'[\"wifi\", \"điều hòa\", \"bếp nhỏ\"]',1,6,'2025-10-27 17:45:27','2025-10-27 17:45:27','2025-10-27 17:45:27',0);
/*!40000 ALTER TABLE `homestay` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 */ /*!50003 TRIGGER `trg_homestay_update` AFTER UPDATE ON `homestay` FOR EACH ROW BEGIN

    DECLARE v_actor BIGINT;

    SET v_actor = IFNULL(@actor_id, NEW.user_id);



    IF (NEW.name <> OLD.name) THEN

        INSERT INTO `homestay_change_log` (`homestay_id`, `field_name`, `old_value`, `new_value`, `changed_by`)

        VALUES (OLD.id, 'name', OLD.name, NEW.name, v_actor);

    END IF;



    IF (NEW.status <> OLD.status) THEN

        INSERT INTO `homestay_status_history` (`homestay_id`, `old_status`, `new_status`, `reason`, `changed_by`)

        VALUES (OLD.id, OLD.status, NEW.status, 'Status changed', v_actor);

    END IF;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `homestay_change_log`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `homestay_change_log` (
                                       `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                       `homestay_id` bigint unsigned NOT NULL,
                                       `field_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                                       `old_value` text COLLATE utf8mb4_general_ci,
                                       `new_value` text COLLATE utf8mb4_general_ci,
                                       `changed_by` bigint unsigned DEFAULT NULL,
                                       `changed_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                       PRIMARY KEY (`id`),
                                       KEY `homestay_id` (`homestay_id`),
                                       KEY `changed_by` (`changed_by`),
                                       CONSTRAINT `homestay_change_log_ibfk_1` FOREIGN KEY (`homestay_id`) REFERENCES `homestay` (`id`) ON DELETE CASCADE,
                                       CONSTRAINT `homestay_change_log_ibfk_2` FOREIGN KEY (`changed_by`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `homestay_change_log`
--

LOCK TABLES `homestay_change_log` WRITE;
/*!40000 ALTER TABLE `homestay_change_log` DISABLE KEYS */;
INSERT INTO `homestay_change_log` VALUES (1,18,'name','Căn hộ Mini Cường','Homestay ABC Updated',3,'2025-10-29 01:10:15');
/*!40000 ALTER TABLE `homestay_change_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `homestay_image`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `homestay_image` (
                                  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                  `homestay_id` bigint unsigned NOT NULL,
                                  `url` text COLLATE utf8mb4_general_ci NOT NULL,
                                  `alt` text COLLATE utf8mb4_general_ci,
                                  `is_primary` tinyint(1) DEFAULT '0',
                                  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`id`),
                                  KEY `homestay_id` (`homestay_id`),
                                  CONSTRAINT `homestay_image_ibfk_1` FOREIGN KEY (`homestay_id`) REFERENCES `homestay` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `homestay_image`
--

LOCK TABLES `homestay_image` WRITE;
/*!40000 ALTER TABLE `homestay_image` DISABLE KEYS */;
INSERT INTO `homestay_image` VALUES (1,1,'https://example.com/images/villa-danang-1.jpg','Villa Biển Đà Nẵng - Mặt tiền',1,'2025-10-26 22:41:57'),(2,1,'https://example.com/images/villa-danang-2.jpg','Villa Biển Đà Nẵng - Hồ bơi',0,'2025-10-26 22:41:57'),(3,2,'https://example.com/images/sapa-house-1.jpg','Nhà Gỗ Sapa - View núi',1,'2025-10-26 22:41:57'),(4,3,'https://example.com/images/hanoi-apt-1.jpg','Căn Hộ Phố Cổ - Phòng khách',1,'2025-10-26 22:41:57'),(5,4,'https://example.com/images/phuquoc-bungalow-1.jpg','Bungalow Phú Quốc - Bãi biển',1,'2025-10-26 22:41:57'),(6,5,'https://example.com/images/dalat-homestay-1.jpg','Homestay Đà Lạt - Vườn hoa',1,'2025-10-26 22:41:57'),(7,6,'https://example.com/images/hoian-studio-1.jpg','Studio Hội An - Phòng ngủ',1,'2025-10-26 22:41:57'),(8,1,'/images/homestay1a.jpg','Phòng khách Bình An',0,'2025-10-27 17:45:27'),(9,1,'/images/homestay1b.jpg','Phòng ngủ Bình An',0,'2025-10-27 17:45:27'),(10,2,'/images/homestay2a.jpg','Toàn cảnh Cường Retreat',0,'2025-10-27 17:45:27'),(11,3,'/images/homestay3a.jpg','Villa Biển Xanh',0,'2025-10-27 17:45:27'),(12,4,'/images/homestay4a.jpg','Căn hộ Mini',0,'2025-10-27 17:45:27'),(13,5,'/images/homestay5a.jpg','Khôi Homestay Hà Nội',0,'2025-10-27 17:45:27');
/*!40000 ALTER TABLE `homestay_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `homestay_pending`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `homestay_pending` (
                                    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                    `homestay_id` bigint unsigned NOT NULL,
                                    `pending_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                                    `submitted_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                    `status` enum('waiting','approved','rejected') COLLATE utf8mb4_general_ci DEFAULT 'waiting',
                                    `reviewed_by` bigint unsigned DEFAULT NULL,
                                    `reviewed_at` datetime DEFAULT NULL,
                                    `reason` text COLLATE utf8mb4_general_ci,
                                    PRIMARY KEY (`id`),
                                    KEY `homestay_id` (`homestay_id`),
                                    KEY `reviewed_by` (`reviewed_by`),
                                    CONSTRAINT `homestay_pending_ibfk_1` FOREIGN KEY (`homestay_id`) REFERENCES `homestay` (`id`) ON DELETE CASCADE,
                                    CONSTRAINT `homestay_pending_ibfk_2` FOREIGN KEY (`reviewed_by`) REFERENCES `user` (`id`) ON DELETE SET NULL,
                                    CONSTRAINT `homestay_pending_chk_1` CHECK (json_valid(`pending_data`))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `homestay_pending`
--

LOCK TABLES `homestay_pending` WRITE;
/*!40000 ALTER TABLE `homestay_pending` DISABLE KEYS */;
INSERT INTO `homestay_pending` VALUES (1,18,'{\"name\":\"Homestay ABC Updated\",\"description\":\"Mô tả mới\",\"address\":\"123 Đường XYZ, Quận ABC\",\"city\":\"Đà Nẵng\",\"lat\":16.0544,\"longitude\":108.2022,\"capacity\":6,\"numRooms\":3,\"bathroomCount\":2,\"basePrice\":800000,\"amenities\":\"{\\\"wifi\\\": true, \\\"parking\\\": true, \\\"pool\\\": true}\"}','2025-10-29 01:04:29','approved',6,'2025-10-28 18:10:15',NULL),(2,17,'{\"name\":\"Homestay ABC Updated\",\"description\":\"Mô tả mới\",\"address\":\"123 Đường XaaYZ, Quận ABC\",\"city\":\"Hà Nội\",\"lat\":16.0544,\"longitude\":118.2022,\"capacity\":6,\"numRooms\":2,\"bathroomCount\":2,\"basePrice\":800000,\"amenities\":\"{\\\"wifi\\\": true, \\\"parking\\\": true, \\\"pool\\\": true}\"}','2025-10-29 01:09:46','rejected',6,'2025-10-28 18:11:23','Thông tin không đầy đủ, vui lòng bổ sung địa chỉ cụ thể');
/*!40000 ALTER TABLE `homestay_pending` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 */ /*!50003 TRIGGER `trg_homestay_pending_approve` AFTER UPDATE ON `homestay_pending` FOR EACH ROW BEGIN

    -- Chỉ chạy khi status chuyển từ 'waiting' sang 'approved'

    IF OLD.status = 'waiting' AND NEW.status = 'approved' THEN

        -- Cập nhật homestay bằng JSON trong pending_data

        UPDATE homestay

        SET

            name = JSON_UNQUOTE(JSON_EXTRACT(NEW.pending_data, '$.name')),

            description = JSON_UNQUOTE(JSON_EXTRACT(NEW.pending_data, '$.description')),

            address = JSON_UNQUOTE(JSON_EXTRACT(NEW.pending_data, '$.address')),

            city = JSON_UNQUOTE(JSON_EXTRACT(NEW.pending_data, '$.city')),

            base_price = JSON_EXTRACT(NEW.pending_data, '$.base_price'),

            amenities = JSON_EXTRACT(NEW.pending_data, '$.amenities'),

            updated_at = NOW(),

            status = 2, -- công khai

            approved_by = NEW.reviewed_by,

            approved_at = NEW.reviewed_at

        WHERE id = NEW.homestay_id;

    END IF;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 */ /*!50003 TRIGGER `trg_notify_homestay_approved` AFTER UPDATE ON `homestay_pending` FOR EACH ROW BEGIN

    -- Chỉ gửi thông báo khi chuyển từ 'waiting' sang 'approved'

    IF OLD.status = 'waiting' AND NEW.status = 'approved' THEN

        INSERT INTO notification (user_id, title, body, created_at)

        SELECT h.user_id,

               'Yêu cầu cập nhật Homestay đã được chấp thuận',

               CONCAT(

                       'Yêu cầu chỉnh sửa thông tin Homestay "', h.name,

                       '" của bạn đã được admin duyệt thành công.'

               ),

               NOW()

        FROM homestay h

        WHERE h.id = NEW.homestay_id;

    END IF;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `homestay_status_history`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `homestay_status_history` (
                                           `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                           `homestay_id` bigint unsigned NOT NULL,
                                           `old_status` tinyint DEFAULT NULL,
                                           `new_status` tinyint DEFAULT NULL,
                                           `reason` text COLLATE utf8mb4_general_ci,
                                           `changed_by` bigint unsigned DEFAULT NULL,
                                           `changed_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                           PRIMARY KEY (`id`),
                                           KEY `homestay_id` (`homestay_id`),
                                           KEY `changed_by` (`changed_by`),
                                           CONSTRAINT `homestay_status_history_ibfk_1` FOREIGN KEY (`homestay_id`) REFERENCES `homestay` (`id`) ON DELETE CASCADE,
                                           CONSTRAINT `homestay_status_history_ibfk_2` FOREIGN KEY (`changed_by`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `homestay_status_history`
--

LOCK TABLES `homestay_status_history` WRITE;
/*!40000 ALTER TABLE `homestay_status_history` DISABLE KEYS */;
INSERT INTO `homestay_status_history` VALUES (1,2,2,3,'Status changed',1,'2025-10-27 17:23:57'),(2,18,1,2,'Status changed',3,'2025-10-27 18:33:45'),(3,18,2,3,'Status changed',3,'2025-10-27 18:37:02'),(4,18,3,4,'Status changed',3,'2025-10-27 18:44:07'),(5,18,4,2,'Status changed',3,'2025-10-29 01:04:24'),(6,18,2,3,'Status changed',3,'2025-10-29 17:19:09'),(7,18,3,2,'Status changed',3,'2025-11-07 18:59:32');
/*!40000 ALTER TABLE `homestay_status_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
                                `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                `user_id` bigint unsigned NOT NULL,
                                `title` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
                                `body` text COLLATE utf8mb4_general_ci,
                                `is_read` tinyint(1) DEFAULT '0',
                                `meta` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
                                `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`),
                                KEY `user_id` (`user_id`),
                                CONSTRAINT `notification_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                                CONSTRAINT `notification_chk_1` CHECK (json_valid(`meta`))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES (1,1,'Đặt phòng thành công','Bạn đã đặt Homestay Bình An thành công',0,'{\"booking_id\": 1}','2025-10-27 17:45:27'),(2,2,'Homestay được duyệt','Homestay Bình An đã được Admin duyệt',1,'{\"homestay_id\": 1}','2025-10-27 17:45:27'),(3,3,'Phản hồi từ khách','Bạn có một đánh giá mới từ khách hàng',0,'{\"review_id\": 2}','2025-10-27 17:45:27'),(4,5,'Thanh toán hoàn tất','Thanh toán VNPay cho Villa Biển Xanh thành công',1,'{\"payment_id\": 3}','2025-10-27 17:45:27'),(5,3,'Yêu cầu cập nhật Homestay đã được chấp thuận','Yêu cầu chỉnh sửa thông tin Homestay \"Homestay ABC Updated\" của bạn đã được admin duyệt thành công.',0,NULL,'2025-10-29 01:10:15');
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
                           `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                           `booking_id` bigint unsigned NOT NULL,
                           `amount` decimal(14,2) NOT NULL,
                           `method` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
                           `status` varchar(40) COLLATE utf8mb4_general_ci DEFAULT 'initiated',
                           `transaction_code` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
                           `paid_at` datetime DEFAULT NULL,
                           `callback_payload` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
                           `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id`),
                           KEY `booking_id` (`booking_id`),
                           CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`) ON DELETE CASCADE,
                           CONSTRAINT `payment_chk_1` CHECK (json_valid(`callback_payload`))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
INSERT INTO `payment` VALUES (1,1,1900000.00,'VNPay','success','TXN1001','2025-10-27 17:45:27',NULL,'2025-10-27 17:45:27'),(2,2,2400000.00,'MoMo','success','TXN1002','2025-10-27 17:45:27',NULL,'2025-10-27 17:45:27'),(3,3,5000000.00,'VNPay','initiated','TXN1003',NULL,NULL,'2025-10-27 17:45:27'),(4,4,1300000.00,'MoMo','refunded','TXN1004','2025-10-27 17:45:27',NULL,'2025-10-27 17:45:27');
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
                          `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                          `booking_id` bigint unsigned DEFAULT NULL,
                          `homestay_id` bigint unsigned NOT NULL,
                          `customer_id` bigint unsigned DEFAULT NULL,
                          `rating` tinyint NOT NULL,
                          `comment` text COLLATE utf8mb4_general_ci,
                          `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                          `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          `status` tinyint DEFAULT '1',
                          `is_deleted` tinyint(1) DEFAULT '0',
                          PRIMARY KEY (`id`),
                          KEY `booking_id` (`booking_id`),
                          KEY `homestay_id` (`homestay_id`),
                          KEY `customer_id` (`customer_id`),
                          CONSTRAINT `review_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`) ON DELETE SET NULL,
                          CONSTRAINT `review_ibfk_2` FOREIGN KEY (`homestay_id`) REFERENCES `homestay` (`id`) ON DELETE CASCADE,
                          CONSTRAINT `review_ibfk_3` FOREIGN KEY (`customer_id`) REFERENCES `user` (`id`) ON DELETE SET NULL,
                          CONSTRAINT `review_chk_1` CHECK ((`rating` between 1 and 5)),
                          CONSTRAINT `review_chk_2` CHECK ((`status` in (0,1,2)))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` VALUES (1,1,1,1,5,'Homestay rất sạch sẽ và chủ nhà thân thiện','2025-10-27 17:45:27','2025-10-27 17:45:27',1,0),(2,2,2,4,4,'Không gian thoáng, vườn đẹp, nhưng hơi xa trung tâm','2025-10-27 17:45:27','2025-10-27 17:45:27',1,0),(3,3,3,5,5,'Rất đáng tiền, hồ bơi tuyệt vời!','2025-10-27 17:45:27','2025-10-27 17:45:27',1,0),(4,4,4,8,3,'Ổn nhưng hơi ồn ào vì gần đường','2025-10-27 17:45:27','2025-10-27 17:45:27',1,0);
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review_report`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review_report` (
                                 `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                 `review_id` bigint unsigned NOT NULL,
                                 `reporter_id` bigint unsigned NOT NULL,
                                 `reason` text COLLATE utf8mb4_general_ci NOT NULL,
                                 `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `status` tinyint DEFAULT '0',
                                 PRIMARY KEY (`id`),
                                 KEY `review_id` (`review_id`),
                                 KEY `reporter_id` (`reporter_id`),
                                 CONSTRAINT `review_report_ibfk_1` FOREIGN KEY (`review_id`) REFERENCES `review` (`id`) ON DELETE CASCADE,
                                 CONSTRAINT `review_report_ibfk_2` FOREIGN KEY (`reporter_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review_report`
--

LOCK TABLES `review_report` WRITE;
/*!40000 ALTER TABLE `review_report` DISABLE KEYS */;
INSERT INTO `review_report` VALUES (1,4,6,'Nội dung không phù hợp','2025-10-27 17:45:27',1);
/*!40000 ALTER TABLE `review_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
                        `id` smallint unsigned NOT NULL AUTO_INCREMENT,
                        `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                        `description` text COLLATE utf8mb4_general_ci,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'ADMIN','Quản trị hệ thống, duyệt homestay, xử lý khiếu nại'),(2,'HOST','Chủ homestay, quản lý homestay và đặt phòng'),(3,'CUSTOMER','Khách hàng đặt phòng, đánh giá homestay');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
                        `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                        `name` varchar(150) COLLATE utf8mb4_general_ci NOT NULL,
                        `email` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                        `phone` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `passwd` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                        `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        `status` tinyint NOT NULL DEFAULT '1',
                        `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `email` (`email`),
                        CONSTRAINT `user_chk_1` CHECK ((`status` in (0,1,2,3)))
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Nguyễn Văn A','nguyenvana@example.com','0901234567','$2a$10$dummy_password_hash','2025-10-26 22:41:57','2025-10-26 22:41:57',1,0),(2,'Trần Thị B','tranthib@example.com','0912345678','$2a$10$dummy_password_hash','2025-10-26 22:41:57','2025-10-26 22:41:57',1,0),(3,'Lê Văn C','levanc@example.com','0923456789','$2a$10$dummy_password_hash','2025-10-26 22:41:57','2025-10-26 22:41:57',1,0),(4,'Nguyễn Văn An','an.nguyen@example.com','0901111222','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','2025-10-27 17:45:27','2025-10-27 17:45:27',1,0),(5,'Trần Thị Bình','binh.tran@example.com','0902222333','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','2025-10-27 17:45:27','2025-10-27 17:45:27',1,0),(6,'Lê Quốc Cường','cuong.le@example.com','0903333444','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','2025-10-27 17:45:27','2025-10-27 17:45:27',1,0),(7,'Phạm Thu Dung','dung.pham@example.com','0904444555','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','2025-10-27 17:45:27','2025-10-27 17:45:27',1,0),(8,'Vũ Hồng Hải','hai.vu@example.com','0905555666','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','2025-10-27 17:45:27','2025-10-27 17:45:27',1,0),(9,'Admin Hệ Thống','admin@homestay.vn','0999999999','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','2025-10-27 17:45:27','2025-10-27 17:45:27',1,0),(10,'Trần Minh Khôi','khoi.tran@example.com','0906666777','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','2025-10-27 17:45:27','2025-10-27 17:45:27',2,0),(11,'Nguyễn Hoàng Linh','linh.nguyen@example.com','0907777888','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','2025-10-27 17:45:27','2025-10-27 17:45:27',1,0),(12,'Phạm Gia Nam','nam.pham@example.com','0908888999','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','2025-10-27 17:45:27','2025-10-27 17:45:27',3,0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 */ /*!50003 TRIGGER `trg_user_update` AFTER UPDATE ON `user` FOR EACH ROW BEGIN

    DECLARE v_actor BIGINT;

    SET v_actor = IFNULL(@actor_id, NEW.id);



    IF (NEW.email <> OLD.email) THEN

        INSERT INTO `user_profile_history` (`user_id`, `field_name`, `old_value`, `new_value`, `changed_by`)

        VALUES (OLD.id, 'email', OLD.email, NEW.email, v_actor);

    END IF;



    IF (NEW.phone <> OLD.phone) THEN

        INSERT INTO `user_profile_history` (`user_id`, `field_name`, `old_value`, `new_value`, `changed_by`)

        VALUES (OLD.id, 'phone', OLD.phone, NEW.phone, v_actor);

    END IF;



    IF (NEW.status <> OLD.status) THEN

        INSERT INTO `user_status_history` (`user_id`, `old_status`, `new_status`, `reason`, `changed_by`)

        VALUES (OLD.id, OLD.status, NEW.status, 'Status changed', v_actor);

    END IF;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `user_profile_history`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_profile_history` (
                                        `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                        `user_id` bigint unsigned NOT NULL,
                                        `field_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
                                        `old_value` text COLLATE utf8mb4_general_ci,
                                        `new_value` text COLLATE utf8mb4_general_ci,
                                        `changed_by` bigint unsigned DEFAULT NULL,
                                        `changed_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                        PRIMARY KEY (`id`),
                                        KEY `user_id` (`user_id`),
                                        KEY `changed_by` (`changed_by`),
                                        CONSTRAINT `user_profile_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                                        CONSTRAINT `user_profile_history_ibfk_2` FOREIGN KEY (`changed_by`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_profile_history`
--

LOCK TABLES `user_profile_history` WRITE;
/*!40000 ALTER TABLE `user_profile_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_profile_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
                             `user_id` bigint unsigned NOT NULL,
                             `role_id` smallint unsigned NOT NULL,
                             PRIMARY KEY (`user_id`,`role_id`),
                             KEY `role_id` (`role_id`),
                             CONSTRAINT `user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                             CONSTRAINT `user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (6,1),(1,2),(2,2),(3,2),(7,2),(4,3),(5,3),(8,3),(9,3);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_session`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_session` (
                                `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                `user_id` bigint unsigned NOT NULL,
                                `refresh_token` varchar(512) COLLATE utf8mb4_general_ci NOT NULL,
                                `device_info` text COLLATE utf8mb4_general_ci,
                                `ip_address` text COLLATE utf8mb4_general_ci,
                                `expires_at` datetime DEFAULT NULL,
                                `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`),
                                KEY `user_id` (`user_id`),
                                CONSTRAINT `user_session_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_session`
--

LOCK TABLES `user_session` WRITE;
/*!40000 ALTER TABLE `user_session` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_status_history`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_status_history` (
                                       `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                       `user_id` bigint unsigned NOT NULL,
                                       `old_status` tinyint DEFAULT NULL,
                                       `new_status` tinyint DEFAULT NULL,
                                       `reason` text COLLATE utf8mb4_general_ci,
                                       `changed_by` bigint unsigned DEFAULT NULL,
                                       `changed_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                       PRIMARY KEY (`id`),
                                       KEY `user_id` (`user_id`),
                                       KEY `changed_by` (`changed_by`),
                                       CONSTRAINT `user_status_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                                       CONSTRAINT `user_status_history_ibfk_2` FOREIGN KEY (`changed_by`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_status_history`
--

LOCK TABLES `user_status_history` WRITE;
/*!40000 ALTER TABLE `user_status_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_status_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'HomestayBooking'
--

--
-- Dumping routines for database 'HomestayBooking'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-09  0:56:52
