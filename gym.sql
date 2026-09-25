-- ============================================================
-- 健身房管理系统数据库脚本 (规范化版)
-- 数据库: gym
-- 字符集: utf8
-- ============================================================

SET FOREIGN_KEY_CHECKS=0;
SET NAMES utf8;

-- ============================================================
-- 1. 会员卡类型表 (member_type)
-- ============================================================
DROP TABLE IF EXISTS `member_type`;
CREATE TABLE `member_type` (
  `type_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '卡类型ID',
  `type_name` varchar(20) DEFAULT NULL COMMENT '卡类型名称',
  `typeci_shu` int(11) DEFAULT NULL COMMENT '次数',
  `valid_days` int(11) DEFAULT NULL COMMENT '有效天数',
  `price` float DEFAULT NULL COMMENT '价格',
  `description` varchar(100) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='会员卡类型表';

INSERT INTO `member_type` (`type_id`, `type_name`, `typeci_shu`, `valid_days`, `price`, `description`) VALUES
('1', '季卡', '0', '90', '500', NULL),
('2', '月卡', '0', '30', '150', NULL),
('3', '年卡', '0', '365', '1000', NULL),
('4', '周卡', '0', '7', '50', NULL);

-- ============================================================
-- 2. 用户表 (sys_user) - 管理员/教练/会员
-- ============================================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `login_name` varchar(20) NOT NULL COMMENT '登录名',
  `password` varchar(256) NOT NULL COMMENT '密码',
  `user_type` int(1) NOT NULL COMMENT '用户类型 0管理员 1教练 2会员',
  `real_name` varchar(20) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `sex` int(1) DEFAULT NULL COMMENT '性别 0男 1女',
  `age` int(4) DEFAULT NULL COMMENT '年龄',
  `coach_date` date DEFAULT NULL COMMENT '教练工作时间',
  `coach_teach` int(4) DEFAULT NULL COMMENT '教练教龄',
  `coach_wages` double DEFAULT NULL COMMENT '教练工资',
  `coach_static` int(1) DEFAULT NULL COMMENT '教练状态 0正常 1休假 2离职',
  `coach_address` varchar(100) DEFAULT NULL COMMENT '教练家庭住址',
  `member_type_id` bigint(20) DEFAULT NULL COMMENT '会员卡类型ID',
  `member_birthday` varchar(20) DEFAULT NULL COMMENT '会员生日',
  `member_date` date DEFAULT NULL COMMENT '会员入会时间',
  `status` int(1) DEFAULT NULL COMMENT '会员状态 1正常 0请续卡',
  `remaining_days` int(11) DEFAULT NULL COMMENT '会员剩余天数',
  `member_xufei` date DEFAULT NULL COMMENT '会员到期日期',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`user_id`),
  KEY `fk_user_member_type` (`member_type_id`),
  CONSTRAINT `fk_user_member_type` FOREIGN KEY (`member_type_id`) REFERENCES `member_type` (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 COMMENT='用户信息表';

-- 管理员数据
INSERT INTO `sys_user` (`user_id`,`login_name`,`password`,`user_type`,`real_name`) VALUES
('1', 'admin', '0192023a7bbd73250516f069df18b500', '0', '管理员'),
('2', 'zhangjinhe', '0192023a7bbd73250516f069df18b500', '0', '张晋赫');

-- 教练数据 (coach_date 调整到 2025 年)
INSERT INTO `sys_user` (`user_id`,`login_name`,`password`,`user_type`,`real_name`,`phone`,`sex`,`age`,`coach_date`,`coach_teach`,`coach_wages`,`coach_address`,`coach_static`) VALUES
('3', 'zhangqiling', 'e10adc3949ba59abbe56e057f20f883e', '1', '张起灵', '13243253432', '0', '22', '2025-08-02', '2', '6000', '张家古楼', '0'),
('4', 'lanwangji', 'e10adc3949ba59abbe56e057f20f883e', '1', '蓝忘机', '13324332344', '0', '20', '2025-08-02', '2', '10000', '云深不知处', '0'),
('5', 'huacheng', 'e10adc3949ba59abbe56e057f20f883e', '1', '花城', '13324245453', '1', '25', '2025-04-10', '3', '12000', '仙乐国', '0'),
('6', 'waner', 'e10adc3949ba59abbe56e057f20f883e', '1', '婉儿', '13342244112', '1', '18', '2025-05-01', '2', '16500', '长留', '1'),
('7', 'baifengjiu', 'e10adc3949ba59abbe56e057f20f883e', '1', '白凤九', '13433324335', '1', '20', '2025-10-04', '1', '18000', '青丘', '2'),
('8', 'zhanghan', 'e10adc3949ba59abbe56e057f20f883e', '1', '张含', '15299985622', '1', '35', '2025-04-02', '5', '10000', '北京丰台', '1'),
('9', 'zhouming', 'e10adc3949ba59abbe56e057f20f883e', '1', '周命', '15785456231', '1', '22', '2025-05-06', '2', '12000', '陕西西安', '0');

-- 会员数据 (卡类型:1=季卡90天,2=月卡30天,3=年卡365天,4=周卡7天)
-- status 与 remaining_days 保持一致:剩余天数>0=1正常,=0=0请续卡
-- 时间基准:2026-08-16(当前),member_date 在2025年入会,member_xufei 在2025-2027年
INSERT INTO `sys_user` (`user_id`,`login_name`,`password`,`user_type`,`real_name`,`phone`,`sex`,`age`,`member_type_id`,`member_date`,`member_birthday`,`status`,`remaining_days`,`member_xufei`) VALUES
('10', 'liujian', 'e10adc3949ba59abbe56e057f20f883e', '2', '刘建', '13456789087', '1', '23', '3', '2025-08-01', '08-24', '1', '365', '2027-08-01'),
('11', 'tom', 'e10adc3949ba59abbe56e057f20f883e', '2', 'Tom', '15266668585', '1', '24', '2', '2025-10-01', '08-07', '0', '0', '2025-10-31'),
('12', 'wangzeming', 'e10adc3949ba59abbe56e057f20f883e', '2', '王泽明', '17858966255', '1', '19', '4', '2025-06-01', '05-08', '0', '0', '2025-06-08'),
('13', 'zhanghong', 'e10adc3949ba59abbe56e057f20f883e', '2', '张红', '18799256874', '0', '23', '1', '2025-07-01', '04-08', '1', '30', '2026-09-15'),
('14', 'wangfang', 'e10adc3949ba59abbe56e057f20f883e', '2', '王芳', '15299950487', '0', '22', '3', '2025-01-01', '04-21', '1', '350', '2027-08-01'),
('15', 'liming', 'e10adc3949ba59abbe56e057f20f883e', '2', '李明', '15699588547', '1', '22', '3', '2025-02-01', '04-21', '0', '0', '2026-02-01'),
('16', 'jerry', 'e10adc3949ba59abbe56e057f20f883e', '2', 'jerry', '15266528547', '1', '25', '2', '2025-05-01', '04-08', '0', '0', '2025-06-01'),
('17', 'fanglan', 'e10adc3949ba59abbe56e057f20f883e', '2', '方蓝', '13456789876', '0', '22', '4', '2025-04-01', '04-29', '0', '0', '2025-04-08'),
('18', 'zhaojing', 'e10adc3949ba59abbe56e057f20f883e', '2', '赵静', '15288888888', '0', '25', '3', '2025-03-01', '04-22', '0', '0', '2026-03-01'),
('19', 'sunxi', 'e10adc3949ba59abbe56e057f20f883e', '2', '孙戏', '18566584785', '1', '35', '3', '2025-05-01', '05-03', '0', '0', '2026-05-01');

-- ============================================================
-- 3. 课程表 (course) - 原subject表
-- ============================================================
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
  `course_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_name` varchar(20) DEFAULT NULL COMMENT '课程名称',
  `selling_price` double DEFAULT NULL COMMENT '售价',
  `description` varchar(100) DEFAULT NULL COMMENT '描述',
  `duration` int(11) DEFAULT NULL COMMENT '时长',
  PRIMARY KEY (`course_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='课程信息表';

INSERT INTO `course` (`course_id`, `course_name`, `selling_price`, `description`, `duration`) VALUES
('1', '健身球', '30', NULL, NULL),
('2', '跑步机', '35', NULL, NULL),
('4', '压力车', '50', NULL, NULL),
('5', '仰卧起坐', '20', NULL, NULL),
('6', '健身操', '50', NULL, NULL);

-- ============================================================
-- 4. 商品表 (goods)
-- ============================================================
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods` (
  `goods_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `goods_name` varchar(50) DEFAULT NULL COMMENT '商品名称',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `price` double DEFAULT NULL COMMENT '进价',
  `sell_price` double DEFAULT NULL COMMENT '售价',
  `stock` int(11) DEFAULT NULL COMMENT '库存',
  `description` varchar(100) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`goods_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='商品信息表';

INSERT INTO `goods` (`goods_id`, `goods_name`, `unit`, `price`, `sell_price`, `stock`, `description`) VALUES
('2', '可口可乐', '瓶', '2', '5', '48', '1'),
('3', '百事可乐', '瓶', '2', '5', '3', ''),
('4', '小布丁雪糕', '根', '0.5', '1', '13', ''),
('5', '压缩饼干', '块', '2', '5', '24', ''),
('7', '脉动', '瓶', '3', '6', '0', ''),
('8', '毛巾', '个', '10', '20', '0', '');

-- ============================================================
-- 5. 器材表 (equipment)
-- ============================================================
DROP TABLE IF EXISTS `equipment`;
CREATE TABLE `equipment` (
  `equipment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '器材ID',
  `name` varchar(20) DEFAULT NULL COMMENT '器材名称',
  `specification` varchar(50) DEFAULT NULL COMMENT '器材说明',
  `status` int(1) DEFAULT NULL COMMENT '状态',
  `purchase_time` date DEFAULT NULL COMMENT '购买时间',
  PRIMARY KEY (`equipment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='器材信息表';

INSERT INTO `equipment` (`equipment_id`, `name`, `specification`, `status`, `purchase_time`) VALUES
('1', '跑步机', '大型斑马牌跑步机', NULL, NULL),
('2', '动感单车', '塑身', NULL, NULL),
('3', '呼啦圈', '10', NULL, NULL);

-- ============================================================
-- 6. 丢失物品表 (lost_property) - 原loos表
-- ============================================================
DROP TABLE IF EXISTS `lost_property`;
CREATE TABLE `lost_property` (
  `lost_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '遗失物品ID',
  `item_name` varchar(20) DEFAULT NULL COMMENT '遗失物品名称',
  `loos_images` varchar(50) DEFAULT NULL COMMENT '图片',
  `loos_address` varchar(50) DEFAULT NULL COMMENT '拾取地点',
  `found_time` date DEFAULT NULL COMMENT '捡到时间',
  `status` int(10) DEFAULT NULL COMMENT '状态 0未领回 1已领走',
  `scavenger` varchar(50) DEFAULT NULL COMMENT '拾取人',
  `scavenger_phone` varchar(50) DEFAULT NULL COMMENT '拾取人电话',
  `receive_name` varchar(20) DEFAULT NULL COMMENT '领走人姓名',
  `receive_phone` varchar(20) DEFAULT NULL COMMENT '领走人电话',
  `receive_date` date DEFAULT NULL COMMENT '领走时间',
  `admin` varchar(50) DEFAULT NULL COMMENT '操作员',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`lost_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='丢失物品表';

INSERT INTO `lost_property` (`lost_id`, `item_name`, `loos_images`, `loos_address`, `found_time`, `status`, `scavenger`, `scavenger_phone`, `receive_name`, `receive_phone`, `receive_date`, `admin`, `description`) VALUES
('1', '车钥匙', '1', '厕所门口', '2025-04-10', '1', '张三', '13355660000', '22', '25525', '2025-04-12', 'admin', NULL),
('2', '手机', '1', '前台', '2025-05-01', '1', '王能', '15299952320', '张宏', '15899965478', '2025-05-02', 'admin', NULL),
('3', '钱包', '1', '动感单车旁', '2025-05-01', '0', '李镇', '15966325478', NULL, NULL, NULL, 'admin', NULL),
('4', '包', '1', '门口', '2025-05-02', '0', '刘伟', '15326587548', NULL, NULL, NULL, 'admin', NULL);

-- ============================================================
-- 7. 商品销售记录表 (goods_sales_record) - 原goodinfo表
-- ============================================================
DROP TABLE IF EXISTS `goods_sales_record`;
CREATE TABLE `goods_sales_record` (
  `sales_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '销售记录ID',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员ID',
  `quantity` int(11) DEFAULT NULL COMMENT '数量',
  `amount` double DEFAULT NULL COMMENT '金额',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `sale_time` timestamp NULL DEFAULT NULL COMMENT '销售时间',
  PRIMARY KEY (`sales_id`),
  KEY `fk_sales_goods` (`goods_id`),
  KEY `fk_sales_member` (`member_id`),
  CONSTRAINT `fk_sales_goods` FOREIGN KEY (`goods_id`) REFERENCES `goods` (`goods_id`),
  CONSTRAINT `fk_sales_member` FOREIGN KEY (`member_id`) REFERENCES `sys_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='商品销售记录表';

-- ============================================================
-- 8. 私教记录表 (private_coach_record) - 原privatecoachinfo表
-- ============================================================
DROP TABLE IF EXISTS `private_coach_record`;
CREATE TABLE `private_coach_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '私教记录ID',
  `course_id` bigint(20) DEFAULT NULL COMMENT '课程ID',
  `coach_id` bigint(20) DEFAULT NULL COMMENT '教练ID',
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员ID',
  `count` int(20) DEFAULT NULL COMMENT '数量',
  `countprice` double DEFAULT NULL COMMENT '总金额',
  `realprice` double DEFAULT NULL COMMENT '实收金额',
  `course_time` date DEFAULT NULL COMMENT '上课日期',
  `status` int(11) DEFAULT NULL COMMENT '状态 1购买 0赠送',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注/打卡记录',
  `admin` varchar(20) DEFAULT 'asdf' COMMENT '操作员',
  PRIMARY KEY (`record_id`),
  KEY `fk_record_course` (`course_id`),
  KEY `fk_record_coach` (`coach_id`),
  KEY `fk_record_member` (`member_id`),
  CONSTRAINT `fk_record_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`),
  CONSTRAINT `fk_record_coach` FOREIGN KEY (`coach_id`) REFERENCES `sys_user` (`user_id`),
  CONSTRAINT `fk_record_member` FOREIGN KEY (`member_id`) REFERENCES `sys_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='私教记录表';

-- ============================================================
-- 8.1 课程预约表 (course_appointment) - 会员预约单次课程
-- ============================================================
DROP TABLE IF EXISTS `course_appointment`;
CREATE TABLE `course_appointment` (
  `appointment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `record_id` bigint(20) DEFAULT NULL COMMENT '关联私教购买记录ID',
  `course_id` bigint(20) DEFAULT NULL COMMENT '课程ID',
  `coach_id` bigint(20) DEFAULT NULL COMMENT '教练ID',
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员ID',
  `appointment_date` date DEFAULT NULL COMMENT '预约上课日期',
  `status` int(1) DEFAULT '0' COMMENT '状态 0已预约 1已打卡 2已取消',
  `checkin_time` datetime DEFAULT NULL COMMENT '教练打卡时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`appointment_id`),
  KEY `fk_appt_record` (`record_id`),
  KEY `fk_appt_coach` (`coach_id`),
  KEY `fk_appt_member` (`member_id`),
  CONSTRAINT `fk_appt_record` FOREIGN KEY (`record_id`) REFERENCES `private_coach_record` (`record_id`),
  CONSTRAINT `fk_appt_coach` FOREIGN KEY (`coach_id`) REFERENCES `sys_user` (`user_id`),
  CONSTRAINT `fk_appt_member` FOREIGN KEY (`member_id`) REFERENCES `sys_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='课程预约表';

-- ============================================================
-- 9. 充值记录表 (recharge_record) - 原chongzhi表
-- ============================================================
DROP TABLE IF EXISTS `recharge_record`;
CREATE TABLE `recharge_record` (
  `recharge_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '充值记录ID',
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员ID',
  `type_id` bigint(20) DEFAULT NULL COMMENT '卡类型ID',
  `money` bigint(20) DEFAULT NULL COMMENT '金额',
  `ssmoney` bigint(20) DEFAULT NULL COMMENT '实收金额',
  `zlmoney` bigint(20) DEFAULT NULL COMMENT '找零',
  `recharge_time` timestamp NULL DEFAULT NULL COMMENT '充值时间',
  `amount` bigint(20) DEFAULT NULL COMMENT '充值金额',
  `remark` varchar(50) DEFAULT NULL COMMENT '备注',
  `cz_static` bigint(20) DEFAULT NULL COMMENT '充值类型 1续费 2续卡',
  PRIMARY KEY (`recharge_id`),
  KEY `fk_recharge_member` (`member_id`),
  KEY `fk_recharge_type` (`type_id`),
  CONSTRAINT `fk_recharge_member` FOREIGN KEY (`member_id`) REFERENCES `sys_user` (`user_id`),
  CONSTRAINT `fk_recharge_type` FOREIGN KEY (`type_id`) REFERENCES `member_type` (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='充值记录表';

-- ============================================================
-- 10. 系统公告表 (system_notice) - 原messages表
-- ============================================================
DROP TABLE IF EXISTS `system_notice`;
CREATE TABLE `system_notice` (
  `notice_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `publisher_id` bigint(20) DEFAULT NULL COMMENT '发布人ID',
  `content` varchar(255) NOT NULL COMMENT '公告内容',
  `publish_time` varchar(50) DEFAULT NULL COMMENT '发布时间',
  `title` varchar(100) DEFAULT NULL COMMENT '标题',
  PRIMARY KEY (`notice_id`),
  KEY `fk_notice_publisher` (`publisher_id`),
  CONSTRAINT `fk_notice_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `sys_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='系统公告表';

INSERT INTO `system_notice` (`notice_id`, `publisher_id`, `content`, `publish_time`, `title`) VALUES
(1, 1, '故人西辞黄鹤楼，烟花三月下扬州。', '2025-05-12 12:33:06', NULL),
(2, 9, '孤帆远影碧空尽，唯见长江天际流。', '2025-05-12 12:34:40', NULL),
(3, 2, '床前明月光，疑是地上霜。', '2025-05-12 12:33:27', NULL),
(4, 8, '行路难！行路难！多歧路，今安在？长风破浪会有时，直挂云帆济沧海。', '2025-05-12 12:34:10', NULL),
(5, 10, '飞流直下三千尺，疑是银河落九天。', '2025-05-12 12:36:13', NULL),
(6, 10, '弃我去者，昨日之日不可留', '2025-05-12 12:37:04', NULL),
(7, 2, '两岸青山相对出，孤帆一片日边来。', '2025-05-12 12:42:18', NULL),
(8, 1, '相看两不厌，只有敬亭山。', '2025-05-12 12:42:36', NULL),
(9, 8, '我寄愁心与明月，随风直到夜郎西。', '2025-05-12 12:42:57', NULL);

SET FOREIGN_KEY_CHECKS=1;
