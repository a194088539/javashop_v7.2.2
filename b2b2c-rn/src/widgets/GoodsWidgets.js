import React from 'react';
import {View, StyleSheet, TouchableOpacity, Image} from 'react-native';
import {F16Text} from './Text';
import {Screen} from '../utils';

export default function GoodsItemPro({goods_img, goods_name}) {
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
    justifyContent: 'center',
    width: Screen.width - 20 - 100,
    height: 80,
    marginLeft: 15,
  },
  g_item_image: {
    width: 80,
    height: 80,
    borderRadius: 3,
  },
});
