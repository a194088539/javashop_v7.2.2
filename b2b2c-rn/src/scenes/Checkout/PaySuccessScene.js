/**
 * Created by Paul on 2019-09-19.
 */
import React, {Component} from 'react';
import {
  View,
  Image,
  BackHandler,
  Platform,
  StatusBar,
  StyleSheet,
} from 'react-native';
import {connect} from 'react-redux';
import {isIphoneX} from 'react-native-iphone-x-helper';
import {Screen} from '../../utils';
import {F16Text} from '../../widgets/Text';
import {HeaderBack} from '../../components';
import {TextLabel} from '../../widgets';

class PaySuccessScene extends Component {
  static navigationOptions = ({navigation}) => {
    return {
      title: '支付成功',
      headerLeft: () => <HeaderBack onPress={() => navigation.goBack()} />,
      gestureEnabled: false,
    };
  };

  constructor(props) {
    super(props);
    const {params} = props.navigation.state;
    this.state = {
      // 交易类型
      tradeType: params.tradeType,
    };
  }

  async componentDidMount() {
    // 监听安卓返回按键
    if (Platform.OS === 'android') {
      BackHandler.addEventListener('hardwareBackPress', this._onBackAndroid);
    }
  }

  /**
   * 监听安卓返回的方法
   * @returns {boolean}
   * @private
   */
  _onBackAndroid = () => {
    if (this.props.navigation) {
      return this.props.navigation.goBack();
    }
  };

  render() {
    const {tradeType} = this.state;
    return (
      <View style={styles.container}>
        <StatusBar barStyle="dark-content" />
        <View style={styles.inner_delivery}>
          <Image
            style={styles.inner_delivery_img}
            source={require('../../images/icon-inner-delivery.png')}
          />
          <View style={styles.content_delivery}>
            <F16Text>支付完成</F16Text>
          </View>
        </View>
        <View style={styles.btns_delivery}>
          {tradeType === 'BALANCE' ? (
            <TextLabel
              style={styles.btnsItem}
              text="查看余额"
              onPress={() => this.props.navigation.replace('Balance')}
            />
          ) : (
            <TextLabel
              style={styles.btnsItem}
              text="查看订单"
              onPress={() => this.props.navigation.replace('MyOrder')}
            />
          )}
          <TextLabel
            style={styles.btnsItem}
            text="返回首页"
            onPress={() => this.props.navigation.navigate('Home')}
          />
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  need_pay: {},
  checkbox: {
    paddingRight: 0,
  },
  inner_delivery: {
    alignItems: 'center',
    justifyContent: 'center',
    alignSelf: 'center',
    flexWrap: 'wrap',
    width: 170,
    marginTop: 35,
    marginBottom: 30,
    height: 50,
  },
  inner_delivery_img: {
    width: 50,
    height: 50,
  },
  content_delivery: {
    height: 20,
  },
  btns_delivery: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  btnsItem: {
    height: 25,
    lineHeight: 20,
    backgroundColor: '#fff',
    color: '#333',
    fontSize: 12,
    textAlign: 'center',
    width: 150,
    borderRadius: 5,
  },
  payment_icon: {
    width: 27,
    height: 27,
    marginRight: 5,
  },
  pay_btn: {
    position: 'absolute',
    bottom: 0,
    width: Screen.width,
    marginBottom: isIphoneX() ? 30 : 0,
  },
});

export default connect()(PaySuccessScene);
