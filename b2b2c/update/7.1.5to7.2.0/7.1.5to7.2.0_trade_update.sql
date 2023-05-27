-- 支付中心修改账单字段  add by liuyulei 2019-12-27
ALTER TABLE es_payment_bill
DROP COLUMN out_trade_no,
CHANGE COLUMN sn bill_sn varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '付款单号，提交给第三方平台单号' AFTER `bill_id`,
CHANGE COLUMN trade_type service_type varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '子业务类型' AFTER `is_pay`,
ADD COLUMN sub_sn varchar(50) NULL COMMENT '子业务编号' AFTER `bill_sn`,
MODIFY COLUMN payment_plugin_id varchar(50) CHARACTER SET utf8 NULL DEFAULT NULL COMMENT '支付插件' AFTER `trade_price`;

-- 交易、订单表 添加预存款抵扣金额  add by liuyulei 2019-12-31
alter table es_trade add column balance decimal(20,2) default 0.00;
alter table es_order add column balance decimal(20,2) default 0.00;

-- 会员预存款日志  add by liuyulei 2020-01-02
-- ----------------------------
-- Table structure for es_deposite_log
-- ----------------------------
DROP TABLE IF EXISTS `es_deposite_log`;
CREATE TABLE `es_deposite_log`  (
  `id` int(8) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `member_id` int(8) NULL DEFAULT NULL COMMENT '会员id',
  `member_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '会员名称',
  `money` decimal(20, 2) NULL DEFAULT NULL COMMENT '记录金额',
  `time` bigint(20) NULL DEFAULT NULL COMMENT '记录时间戳',
  `detail` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '记录明细',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 会员预存款充值  add by liuyulei 2020-01-02
-- ----------------------------
-- Table structure for es_deposite_recharge
-- ----------------------------
DROP TABLE IF EXISTS `es_deposite_recharge`;
CREATE TABLE `es_deposite_recharge`  (
  `id` int(8) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `recharge_sn` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '充值订单编号',
  `member_id` int(8) NULL DEFAULT NULL COMMENT '会员id',
  `member_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员名称',
  `recharge_money` decimal(20, 2) NULL DEFAULT NULL COMMENT '充值金额',
  `recharge_time` bigint(20) NULL DEFAULT NULL COMMENT '充值时间戳',
  `recharge_way` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '充值方式，如：支付宝，微信',
  `pay_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付状态',
  `payment_plugin_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付插件',
  `pay_time` bigint(20) NULL DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;




-- 修改促销商品表   add by liuyulei 2020-02-06
ALTER TABLE `es_promotion_goods`
CHANGE COLUMN `product_id` `sku_id` int(10) NULL DEFAULT NULL COMMENT '货品id' AFTER `goods_id`;

-- 限时抢购申请表增加 规格字段 add by fk 2020-02-06
ALTER TABLE `es_seckill_apply`
ADD COLUMN `specs` longtext NULL COMMENT '规格组合信息' AFTER `original_price`;

-- 团购商品表增加 规格字段 add by fk 2020-02-06
ALTER TABLE `es_groupbuy_goods`
ADD COLUMN `specs` longtext NULL COMMENT '规格组合' AFTER `seller_name`;
-- 修改字段长度 add by fk 2020-02-06
ALTER TABLE `es_order_log`
MODIFY COLUMN `op_name` varchar(120) DEFAULT NULL COMMENT '操作者名称' AFTER `op_id`;

-- 修改字段长度 add by duanmingyu 2020-2-24
alter table es_refund modify column return_account varchar(255);