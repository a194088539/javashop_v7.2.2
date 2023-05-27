/**
 * Created by Andste on 2018/11/19.
 */
import React from 'react';
import {View, Text, StyleSheet, TouchableOpacity, Platform} from 'react-native';
import {isIphoneX} from 'react-native-iphone-x-helper';
import {colors} from '../../../config';
import {Screen} from '../../utils';
import {navigate} from '../../navigator/NavigationService';
import {ActionButton} from '../../components/ActionSheet';

const isIos = Platform.OS === 'ios';

export default function({backEle, onEditPress, topRightText, logined}) {
  return (
    <View style={styles.container}>
      <View style={styles.left}>{backEle}</View>
      <View style={styles.center}>
        <Text style={styles.center_title}>购物车</Text>
      </View>
      <View style={styles.right_view}>
        {logined && (
          <TouchableOpacity onPress={onEditPress}>
            <Text style={styles.center_title}>{topRightText}</Text>
          </TouchableOpacity>
        )}
        {isIos && (
          <ActionButton
            buttonText={'...'}
            verticalOrientation="down"
            offsetX={23}
            offsetY={5}
            size={28}
            headerShortcut
            buttonColor="transparent">
            <TouchableOpacity
              onPress={() => {
                navigate('Home');
              }}>
              <Text style={styles.actionButtonItemText}>首页</Text>
            </TouchableOpacity>
            <TouchableOpacity
              onPress={() => {
                navigate('Classify');
              }}>
              <Text style={styles.actionButtonItemText}>商品分类</Text>
            </TouchableOpacity>
            <TouchableOpacity
              onPress={() => {
                navigate('Cart');
              }}>
              <Text style={styles.actionButtonItemText}>购物车</Text>
            </TouchableOpacity>
            <TouchableOpacity
              onPress={() => {
                navigate('Mine');
              }}>
              <Text style={styles.actionButtonItemText}>我的</Text>
            </TouchableOpacity>
          </ActionButton>
        )}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    width: Screen.width,
    height: isIphoneX() ? 84 : 64,
    paddingTop: isIphoneX() ? 40 : 20,
    zIndex: 1000,
    backgroundColor: colors.navigator_background,
  },
  left: {
    width: 88,
  },
  center: {
    justifyContent: 'center',
    alignItems: 'center',
    width: Screen.width - 44 * 4,
  },
  center_title: {
    color: colors.text,
    fontSize: 17,
    fontWeight: '400',
  },
  right_view: {
    flexDirection: 'row',
    alignItems: 'center',
    width: 100,
  },
  actionButtonItemText: {
    color: 'white',
  },
});
