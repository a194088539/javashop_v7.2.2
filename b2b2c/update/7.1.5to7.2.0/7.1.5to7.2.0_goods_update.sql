-- 商品分类表新增字段  add by duanmingyu 2019-12-24
alter table es_category add is_show varchar(20) default 'YES' comment '是否显示 YES：是，NO：否';
alter table es_category add adv_image varchar(255) default null comment '分类广告图片';
alter table es_category add adv_image_link varchar(255) default null comment '分类广告图片链接地址';

-- 商品表新增字段  add by duanmingyu 2019-12-31
alter table es_goods add mobile_intro longtext default null comment '商品移动端详情';
alter table es_goods add goods_video varchar(255) default null comment '商品视频';

-- 商品草稿表新增字段  add by duanmingyu 2019-12-31
alter table es_draft_goods add mobile_intro longtext default null comment '商品移动端详情';
alter table es_draft_goods add goods_video varchar(255) default null comment '商品视频';