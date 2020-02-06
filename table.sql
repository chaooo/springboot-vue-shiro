/*
SQLyog Professional v12.09 (64 bit)
MySQL - 5.7.27 : Database - shiro_boot
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
  UNIQUE KEY `sys_user_account_uk` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Data for the table `sys_user` */
insert  into `sys_user`(`id`,`account`,`PASSWORD`,`salt`,`nickname`,`roleId`,`createTime`,`updateTime`,`deleteStatus`) values (1,'admin','762F708524C8329E0A47AC9BBDDBEA67','7387c131','超级管理员',1,'2020-01-20',NULL,'1'),(2,'editor','BC9EF2FB66D75CF008C94FEE20979C36','7785fc63','普通编辑',2,'2020-01-20',NULL,'1'),(3,'user11','74C3BA4B109896A6F5C5BA9739004C3F','bcdbd44e','普通用户',3,'2020-01-20',NULL,'1');

/*Table structure for table `sys_permission` */
DROP TABLE IF EXISTS `sys_permission`;

CREATE TABLE `sys_permission` (
  `id` varchar(32) NOT NULL COMMENT '主键id',
  `name` varchar(100) DEFAULT NULL COMMENT '菜单标题',
  `url` varchar(255) DEFAULT NULL COMMENT '路径',
  `menu_type` int(11) DEFAULT NULL COMMENT '菜单类型(0:一级菜单; 1:子菜单:2:按钮权限)',
  `perms` varchar(255) DEFAULT NULL COMMENT '菜单权限编码',
  `sort_no` int(10) DEFAULT NULL COMMENT '菜单排序',
  `del_flag` int(1) DEFAULT '0' COMMENT '删除状态 0正常 1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_prem_sort_no` (`sort_no`) USING BTREE,
  KEY `index_prem_del_flag` (`del_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='菜单权限表';

/*Data for the table `sys_permission` */
insert  into `sys_permission`(`id`,`name`,`url`,`menu_type`,`perms`,`sort_no`,`del_flag`) values ('1','新增用户','/user/add',2,'user:add',1,0),('2','删除用户','/user/delete',2,'user:delete',2,0),('3','修改用户','/user/update',2,'user:update',3,0),('4','查询用户','/user/list',2,'user:list',4,0);

/*Table structure for table `sys_role` */
DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `role_name` varchar(100) DEFAULT NULL COMMENT '角色名称',
  `description` varchar(100) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='角色表';

/*Data for the table `sys_role` */
insert  into `sys_role`(`id`,`role_name`,`description`) values (1,'admin','管理角色'),(2,'editor','编辑角色'),(3,'user','普通用户');

/*Table structure for table `sys_role_permission` */
DROP TABLE IF EXISTS `sys_role_permission`;

CREATE TABLE `sys_role_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) DEFAULT NULL COMMENT '角色id',
  `permission_id` int(11) DEFAULT NULL COMMENT '权限id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_group_role_per_id` (`role_id`,`permission_id`) USING BTREE,
  KEY `index_group_role_id` (`role_id`) USING BTREE,
  KEY `index_group_per_id` (`permission_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='角色权限表';

/*Data for the table `sys_role_permission` */
insert  into `sys_role_permission`(`id`,`role_id`,`permission_id`) values (1,1,1),(2,1,2),(3,1,3),(4,1,4),(5,2,3),(6,2,4),(7,3,4);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
