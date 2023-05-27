-- 添加会员钱包表  add by liuyulei 2020-01-02
DROP TABLE IF EXISTS `es_member_wallet`;
CREATE TABLE `es_member_wallet`  (
  `id` int(8) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `member_id` int(8) NULL DEFAULT NULL COMMENT '会员id',
  `member_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员名称',
  `pre_deposite` decimal(20, 2) NULL DEFAULT NULL COMMENT '会员预存款，默认为0',
  `deposite_password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '预存款密码，默认为-1',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Compact;

-- 增加会员钱包 add by fk 2020-03-19
insert into es_member_wallet(member_id,member_name,pre_deposite,deposite_password) select member_id,uname,0,-1 from es_member;
