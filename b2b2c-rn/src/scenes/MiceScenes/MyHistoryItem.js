/**
 * Created by snow on 2020-03-07
 */
import React from 'react';
import {View, Image, StyleSheet, Text, TouchableOpacity} from 'react-native';
import {navigate} from '../../navigator/NavigationService';
import {colors} from '../../../config';
import {Screen, Foundation} from '../../utils';
import {F16Text} from '../../widgets/Text';
import {TextLabel} from '../../widgets';
import Price from '../../widgets/Price';

const WIDTH = Screen.width;

export default function({data, delItem}) {
  const {history} = data;
  let countNum = 0;
  return (
    <View style={styles.container}>
      <View style={styles.container_time}>
        <Text style={styles.container_time_text}>
          {Foundation.unixToDate(data.time, 'MM月dd日')}
        </Text>
      </View>
      {history.map((goods, index) => {
        return (
          <TouchableOpacity
            style={styles.g_container}
            onPress={() => navigate('Goods', {id: goods.goods_id})}>
            <Image
              style={styles.g_item_image}
              source={{uri: goods.goods_img}}
            />
            <View style={styles.g_item_detail}>
              <F16Text numberOfLines={2}>{goods.goods_name}</F16Text>
              <F16Text style={{color: colors.main}}>
                ￥ {Foundation.formatPrice(goods.goods_price)}
              </F16Text>
              <View style={{flexDirection: 'row'}}>
                <TextLabel
                  text="删除"
                  style={[styles.g_item_label_btn, styles.g_item_label_btn_del]}
                  textStyle={{color: '#FFFFFF'}}
                  onPress={() => delItem(goods)}
                />
              </View>
            </View>
          </TouchableOpacity>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  g_container: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    width: Screen.width,
    height: 135,
    paddingHorizontal: 10,
    paddingVertical: 15,
    backgroundColor: '#FFFFFF',
  },
  g_item_image: {
    width: 105,
    height: 105,
    borderColor: colors.cell_line_backgroud,
    borderWidth: 1,
    borderRadius: 3,
  },
  g_item_detail: {
    justifyContent: 'space-between',
    width: Screen.width - 20 - 105 - 15,
    height: 105,
    marginLeft: 15,
  },
  g_item_label_btn: {
    height: 25,
    marginRight: 10,
    marginBottom: 0,
  },
  g_item_label_btn_del: {
    borderColor: colors.main,
    backgroundColor: colors.main,
  },
  container: {
    flex: 1,
  },
  container_time: {
    backgroundColor: '#FFFFFF',
  },
  container_time_text: {
    color: colors.text,
    fontSize: 20,
  },
});
