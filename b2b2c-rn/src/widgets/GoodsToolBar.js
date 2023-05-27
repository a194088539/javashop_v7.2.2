import React from 'react';
import {View, StyleSheet, TouchableOpacity, Image, Text} from 'react-native';
import {F16Text} from './Text';
import {Screen} from '../utils';
import {TextLabel} from './index';
import {navigate} from '../navigator/NavigationService';
import {colors} from '../../config';

/**
 * 商品组件带按钮
 * @param goods_img   商品图片地址
 * @param goods_name  商品名称
 * @param btn_title   按钮名
 * @param routeName   跳转route
 * @param params      跳转时传递的参数
 * @returns {*}
 * @constructor
 */
export default function GoodsToolBar({
  goods_img,
  goods_name,
  goods_id,
  goods_num,
  btn_title,
  routeName,
  params,
  disable,
}) {
  btn_title = btn_title == null ? '按钮名称' : btn_title;
  return (
    <View style={styles.t_container}>
      <TouchableOpacity
        style={styles.g_container}
        onPress={() => navigate('Goods', {id: goods_id})}>
        <Image
          style={styles.g_item_image}
          source={{
            uri: goods_img,
          }}
        />
        <View style={styles.g_item_detail}>
          <F16Text numberOfLines={2}>{goods_name}</F16Text>
          <View>
            <Text style={styles.goods_item_num}>数量：{goods_num}</Text>
          </View>
        </View>
      </TouchableOpacity>
      <View style={styles.tool_bar}>
        <Text style={{color: '#848484'}}>
          {disable ? '' : '该商品无法申请售后'}
        </Text>
        <TextLabel
          text={btn_title}
          style={styles.goods_item_apply_btn}
          tintColor={disable ? colors.main : '#ffccc7'}
          onPress={() => (disable ? navigate(routeName, params) : undefined)}
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  t_container: {
    backgroundColor: '#FFFFFF',
    height: 140,
  },
  g_container: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    width: Screen.width,
    height: 90,
    paddingHorizontal: 10,
    paddingVertical: 15,
    backgroundColor: '#FFFFFF',
    marginTop: 1,
  },
  g_item_detail: {
    width: Screen.width - 20 - 90,
    height: 80,
    marginLeft: 15,
  },
  g_item_image: {
    width: 80,
    height: 80,
    borderRadius: 3,
  },
  tool_bar: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    width: Screen.width,
    paddingHorizontal: 10,
    paddingVertical: 15,
    height: 45,
  },
  goods_item_apply_btn: {
    width: 100,
    borderRadius: 20,
    borderColor: '#FFFFFF',
  },
  goods_item_num: {
    color: '#848484',
    marginTop: 5,
  },
});
