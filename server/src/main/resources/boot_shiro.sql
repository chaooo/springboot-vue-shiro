/*
SQLyog Professional v12.09 (64 bit)
MySQL - 5.7.28 : Database - shiro_boot
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`shiro_boot` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `shiro_boot`;

/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `account` varchar(30) NOT NULL COMMENT '用户名',
  `PASSWORD` varchar(50) DEFAULT NULL COMMENT '用户密码',
  `salt` varchar(8) DEFAULT NULL COMMENT '随机盐',
  `nickname` varchar(30) DEFAULT NULL COMMENT '用户昵称',
  `roleId` int(11) DEFAULT NULL COMMENT '角色ID',
  `createTime` date DEFAULT NULL COMMENT '创建时间',
  `updateTime` date DEFAULT NULL COMMENT '更新时间',
  `deleteStatus` varchar(2) DEFAULT '1' COMMENT '是否有效：1有效，2无效',
  PRIMARY KEY (`id`),
  UNIQUE KEY `et_users_username_uk` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Data for the table `sys_user` */

insert  into `sys_user`(`id`,`account`,`PASSWORD`,`salt`,`nickname`,`roleId`,`createTime`,`updateTime`,`deleteStatus`) values (1,'admin','ED0B89639E600B4AFEA37F5E0BF1B7FC','64374642',NULL,NULL,'2020-01-17',NULL,'1'),(2,'user1','282F7782CD1966D7011B66B010CBAA86','c3fc5d03','哈哈哈',2,'2020-01-17',NULL,'1'),(3,'user2','D705C8F9A18286C3D75C0A0B41911763','55f69edf',NULL,1,'2020-01-17',NULL,'1');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
