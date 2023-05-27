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
export default function GoodsBtnItem({
  goods_img,
  goods_name,
  btn_title,
  routeName,
  params,
}) {
  console.log(params);
  btn_title = btn_title == null ? '按钮名称' : btn_title;
  return (
    <View style={styles.g_container}>
      <Image
        style={styles.g_item_image}
        source={{
          uri: goods_img,
        }}
      />
      <View style={styles.g_item_detail}>
        <F16Text numberOfLines={2}>{goods_name}</F16Text>
        <View style={{flexDirection: 'row', justifyContent: 'flex-end'}}>
          <TextLabel
            text={btn_title}
            style={[styles.g_item_label_btn, styles.g_item_label_btn_del]}
            textStyle={{color: colors.main}}
            onPress={() => navigate(routeName, params)}
          />
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#FAFAFA',
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
  g_item_label_btn: {
    height: 28,
    marginRight: 10,
    marginBottom: 0,
  },
  g_item_label_btn_del: {
    borderColor: colors.main,
  },
});
