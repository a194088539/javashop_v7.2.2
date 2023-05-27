import React, {Component} from 'react';
import {
  Alert,
  View,
  StatusBar,
  FlatList,
  StyleSheet,
  Text,
  TouchableOpacity,
  Picker,
} from 'react-native';
import {connect} from 'react-redux';
import {GoodsItemPro, GoodsBtnItem, GoodsToolBar} from '../../widgets/index';

class MyDemoScene extends Component {
  static navigationOptions = ({navigation}) => {
    const {params = {}} = navigation.state;
    return {
      title: params.title || '公用组件演示页',
      state: {
        language: 'java',
      },
    };
  };

  render() {
    return (
      <View style={styles.container}>
        <Text>商品组件-居中商品名称</Text>
        <GoodsItemPro
          goods_img={
            'http://javashop-statics.oss-cn-beijing.aliyuncs.com/demo/7EBD931E14FF477FB248823F3CA3316A.jpg_400x400'
          }
          goods_name={
            '商品名称-商品名称商品名称商品名称商品名称商品名称 (居中展示)'
          }
        />
        <Text>商品组件-带按钮</Text>
        <GoodsBtnItem
          goods_img={
            'http://javashop-statics.oss-cn-beijing.aliyuncs.com/demo/7EBD931E14FF477FB248823F3CA3316A.jpg_400x400'
          }
          goods_name={
            '商品名称-商品名称商品名称商品名称商品名称商品名称 (TOP展示)'
          }
          btn_title={'按钮'}
          routeName={'MyTest'}
          params={{a: 11, b: 222}}
        />
        <Text>商品组件-带工具栏</Text>
        <GoodsToolBar
          goods_img={
            'http://javashop-statics.oss-cn-beijing.aliyuncs.com/demo/7EBD931E14FF477FB248823F3CA3316A.jpg_400x400'
          }
          goods_name={
            '商品名称-商品名称商品名称商品名称商品名称商品名称 (TOP展示)'
          }
          btn_title={'按钮'}
          routeName={'MyTest'}
          params={{a: 11, b: 222}}
          disable={true}
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});

export default connect()(MyDemoScene);
