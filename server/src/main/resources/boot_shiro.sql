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

/** 系统用户表 */
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user(
    id INT AUTO_INCREMENT COMMENT '用户ID',
    account VARCHAR(30) NOT NULL COMMENT '用户名',
    PASSWORD VARCHAR(50) COMMENT '用户密码',
    salt VARCHAR(8) COMMENT '随机盐',
    nickname VARCHAR(30) COMMENT '用户昵称',
    roleId INT COMMENT '角色ID',
    createTime DATE COMMENT '创建时间',
    updateTime DATE COMMENT '更新时间',
    deleteStatus VARCHAR(2) DEFAULT '1' COMMENT '是否有效：1有效，2无效',
    CONSTRAINT sys_user_id_pk PRIMARY KEY(id),
    CONSTRAINT sys_user_account_uk UNIQUE(account)
);
INSERT  INTO `sys_user`(`id`,`account`,`PASSWORD`,`salt`,`nickname`,`roleId`,`createTime`,`updateTime`,`deleteStatus`) VALUES (1,'admin','762F708524C8329E0A47AC9BBDDBEA67','7387c131',NULL,1,'2020-01-20',NULL,'1'),(2,'user','BC9EF2FB66D75CF008C94FEE20979C36','7785fc63',NULL,2,'2020-01-20',NULL,'1'),(3,'user1','74C3BA4B109896A6F5C5BA9739004C3F','bcdbd44e',NULL,2,'2020-01-20',NULL,'1');

/** 角色表 */
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `role_name` VARCHAR(100) DEFAULT NULL COMMENT '角色名称',
  `description` VARCHAR(100) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=INNODB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='角色表';
INSERT  INTO `sys_role`(`id`,`role_name`,`description`) VALUES (1,'admin','管理角色'),(2,'user','用户角色');

/** 权限表 */
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` VARCHAR(32) NOT NULL COMMENT '主键id',
  `name` VARCHAR(100) DEFAULT NULL COMMENT '菜单标题',
  `url` VARCHAR(255) DEFAULT NULL COMMENT '路径',
  `menu_type` INT(11) DEFAULT NULL COMMENT '菜单类型(0:一级菜单; 1:子菜单:2:按钮权限)',
  `perms` VARCHAR(255) DEFAULT NULL COMMENT '菜单权限编码',
  `sort_no` INT(10) DEFAULT NULL COMMENT '菜单排序',
  `del_flag` INT(1) DEFAULT '0' COMMENT '删除状态 0正常 1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_prem_sort_no` (`sort_no`) USING BTREE,
  KEY `index_prem_del_flag` (`del_flag`) USING BTREE
) ENGINE=INNODB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='菜单权限表';
INSERT  INTO `sys_permission`(`id`,`name`,`url`,`menu_type`,`perms`,`sort_no`,`del_flag`) VALUES ('1','新增用户','/user/add',2,'user:add',1,0),('2','删除用户','/user/delete',2,'user:delete',2,0),('3','修改用户','/user/update',2,'user:update',3,0),('4','查询用户','/user/list',2,'user:list',4,0);

/** 角色与权限关联表 */
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `role_id` INT(11) DEFAULT NULL COMMENT '角色id',
  `permission_id` INT(11) DEFAULT NULL COMMENT '权限id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_group_role_per_id` (`role_id`,`permission_id`) USING BTREE,
  KEY `index_group_role_id` (`role_id`) USING BTREE,
  KEY `index_group_per_id` (`permission_id`) USING BTREE
) ENGINE=INNODB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='角色权限表';
INSERT  INTO `sys_role_permission`(`id`,`role_id`,`permission_id`) VALUES (1,1,1),(2,1,2),(3,1,3),(4,1,4),(5,2,4);

COMMIT;
