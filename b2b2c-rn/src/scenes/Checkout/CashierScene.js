/**
 * Created by Andste on 2019-01-24.
 */
import React, {Component} from 'react';
import {
  Text,
  Alert,
  View,
  Image,
  BackHandler,
  Platform,
  StatusBar,
  ScrollView,
  StyleSheet,
  DeviceEventEmitter,
} from 'react-native';
import {connect} from 'react-redux';
import {messageActions} from './../../redux/actions';
import {isIphoneX} from 'react-native-iphone-x-helper';
import {Screen, Foundation} from '../../utils';
import {F16Text} from '../../widgets/Text';
import {HeaderBack, Modal} from '../../components';
import {BigButton, Cell, Checkbox, Price, TextLabel} from '../../widgets';
import * as API_Trade from '../../apis/trade';
import * as API_Member from '../../apis/members';
import * as Wechat from 'react-native-wechat';
import Alipay from '@0x5e/react-native-alipay';
import {PayPassword} from '../../components';
import {colors} from '../../../config';
import {navigate} from '../../navigator/NavigationService';
import {EventEmitter} from 'events';
// 支付宝图标
const alipay_icon = require('../../images/icon-alipay.png');
// 微信支付图标
const wechat_icon = require('../../images/icon-wechat-pay.png');

const paymentIcons = {
  alipayDirectPlugin: alipay_icon,
  weixinPayPlugin: wechat_icon,
};
const emitter = new EventEmitter();

DeviceEventEmitter.addListener('WeChat_Resp', resp => {
  emitter.emit(resp.type, resp);
});
const balancePayment = {
  method_name: '预存款',
  plugin_id: 'BALANCE',
  is_retrace: 1,
  image: require('../../images/icon-balance_color.png'),
};

class CashierScene extends Component {
  static navigationOptions = ({navigation}) => {
    return {
      title: '订单创建成功',
      headerLeft: () => <HeaderBack onPress={() => navigation.goBack()} />,
      gestureEnabled: false,
    };
  };

  constructor(props) {
    super(props);
    const {trade_sn, order_sn, fromScene} = props.navigation.state.params;
    this.trade_sn = trade_sn;
    this.order_sn = order_sn || null;
    this.state = {
      fromScene,
      // 支付方式列表
      payment_list: [],
      // 订单详情
      order: {},
      // 支付方式id
      payment_plugin_id: null,
      // 显示确认订单支付完成模态框
      showConfirmModal: false,
      // 显示支付密码弹框
      showPasswordModal: false,
      // 待支付金额
      need_pay_price: 0,
      //预存款余额
      balance: 0,
    };
  }

  componentDidMount() {
    // 监听安卓返回按键
    if (Platform.OS === 'android') {
      BackHandler.addEventListener('hardwareBackPress', this._onBackAndroid);
    }
    this._getCashierDate();
  }

  _getCashierDate = async () => {
    const {trade_sn, order_sn} = this;
    const params = trade_sn ? {trade_sn} : {order_sn};
    const values = await Promise.all([
      API_Trade.getCashierData(params),
      API_Trade.getPaymentList('REACT'),
      API_Member.wallet(),
    ]);
    this.setState({
      order: values[0],
      payment_list: values[1],
      balance: values[2],
      need_pay_price: values[0].need_pay_price,
    });
  };

  // 当组件即将卸载时，移除监听
  componentWillUnmount() {
    if (Platform.OS === 'android') {
      BackHandler.removeEventListener('hardwareBackPress', this._onBackAndroid);
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

  /**
   * 选择支付方式
   * @param item
   * @private
   */
  _onSelectPayment = item => {
    this.setState({payment_plugin_id: item.plugin_id});
  };

  /**
   * 关闭支付密码弹框
   * @param useBalance
   * @private
   */
  _onPasswordModalClose = () => {
    this.setState({showPasswordModal: false});
  };

  /**
   * 发起APP支付
   * @private
   */
  _onInitiatePay = async () => {
    const {trade_sn, order_sn} = this;
    const {dispatch} = this.props;
    const {payment_plugin_id} = this.state;
    if (payment_plugin_id === null) {
      dispatch(messageActions.error('请选择一个支付方式！'));
      return;
    }
    // 支付模式
    const pay_mode = 'normal';
    // 客户端类型
    const client_type = 'REACT';
    // 支付编号
    const sn = trade_sn || order_sn;
    // 交易类型【交易号|订单号】
    const trade_type = trade_sn ? 'trade' : 'order';
    // 如果是微信支付，先检查是否安装微信
    if (payment_plugin_id === 'weixinPayPlugin') {
      const isWXAppInstalled = await Wechat.isWXAppInstalled();
      if (!isWXAppInstalled) {
        dispatch(messageActions.error('您的设备没有安装微信！'));
        return;
      }
    }

    console.log('========参数=========');
    console.log(
      `trade_type=${trade_type},sn=${sn},payment_plugin_id=${payment_plugin_id},pay_mode=${pay_mode},client_type=${client_type}`,
    );
    //如果是预存款支付
    if (payment_plugin_id === balancePayment.plugin_id) {
      this._onBalancePay();
      return;
    }
    // 初始化支付签名
    const result = await API_Trade.initiateAppPay(trade_type, sn, {
      payment_plugin_id,
      pay_mode,
      client_type,
    });
    console.log('初始化签名');
    switch (payment_plugin_id) {
      case 'alipayDirectPlugin':
        this._onAliPay(result);
        break;
      case 'weixinPayPlugin':
        this._onWechatPay(result);
        break;
    }
  };

  /**
   * 预存款支付
   * @param params
   * @returns {Promise<void>}
   * @private
   */
  _onBalancePay = async () => {
    const isPwd = await API_Member.checkPwd();
    if (isPwd) {
      //显示密码输入框
      this.setState({showPasswordModal: true});
    } else {
      Alert.alert('提示', '您还未设置支付密码！', [
        {text: '取消'},
        {
          text: '去设置',
          onPress: () => {
            navigate('CheckMobile', {type: 'payPwd'});
          },
        },
      ]);
    }
  };
  /**
   * 支付密码变更
   * @param password
   * @private
   */
  _onPasswordChange = async password => {
    const {trade_sn, order_sn} = this;
    // 支付编号
    const sn = trade_sn || order_sn;
    ('');
    // 交易类型【交易号|订单号】
    const trade_type = trade_sn ? 'TRADE' : 'ORDER';
    if (password.length === 6) {
      this.setState({showPasswordModal: false});
      const payResult = await API_Trade.balancePay(sn, trade_type, password);
      //如果预存款完全抵扣，则提示支付成功
      if (payResult.need_pay === 0) {
        this._paySuccess();
      } else {
        Alert.alert(
          '提示',
          '预存款成功抵扣' +
            Foundation.formatPrice(payResult.balance) +
            '元，还需在线支付' +
            Foundation.formatPrice(payResult.need_pay) +
            '元',
          [{text: '确定'}],
        );
        this.setState({payment_plugin_id: null});
        this._getCashierDate();
      }
    }
  };

  /**
   * 支付宝支付
   * @param signXml
   * @returns {Promise<void>}
   * @private
   */
  _onAliPay = async signXml => {
    const {dispatch} = this.props;
    try {
      console.log('==========签名========');
      console.log(signXml.gateway_url);
      Alipay.pay(signXml.gateway_url);
      Alert.alert('提示', '支付成功了吗？切记不能重复支付！', [
        {text: '重新支付'},
        {text: '支付成功', onPress: this._paySuccess},
      ]);
    } catch (e) {
      let errMsg = '支付失败';
      switch (e.code) {
        case '6001':
          errMsg = '用户中途取消';
          break;
        case '6002':
          errMsg = '网络连接出错';
          break;
        case '5000':
          errMsg = '重复支付';
          break;
        case '8000':
          errMsg =
            '正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态';
      }
      dispatch(messageActions.error(errMsg));
    }
  };

  /**
   * 微信支付
   * @param signXml
   * @private
   */
  _onWechatPay = async result => {
    const {dispatch} = this.props;
    // 商家向财付通申请的商家ID
    const partnerId = result.partnerid;
    // 预支付订单ID
    const prepayId = result.prepayid;
    // 随机串
    const nonceStr = result.noncestr;
    // 时间戳
    const timeStamp = result.timestamp;
    // 商家根据微信开放平台文档对数据做的签名
    const sign = result.sign;
    Wechat.pay({
      partnerId,
      prepayId,
      nonceStr,
      timeStamp,
      package: 'Sign=WXPay',
      sign,
    });
    emitter.once('PayReq.Resp', resp => {
      if (resp.errCode === 0) {
        this._paySuccess();
      } else if (resp.errCode === -2) {
        dispatch(messageActions.error('中途取消支付'));
      } else if (resp.errCode === -1) {
        dispatch(
          messageActions.error(
            '支付失败:可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等',
          ),
        );
      } else {
        Alert.alert('提示', '支付成功了吗？切记不能重复支付！', [
          {text: '重新支付'},
          {text: '支付成功', onPress: this._paySuccess},
        ]);
      }
    });
  };
  /**
   * 支付成功回调
   * @private
   */
  _paySuccess = () => {
    const {navigation} = this.props;
    const {callback} = navigation.state.params || {};
    const {fromScene} = this.state;
    typeof callback === 'function' && callback();
    if (fromScene === 'Checkout') {
      this.props.dispatch(messageActions.success('支付成功'));
      navigation.replace('PaySuccess', {tradeType: 'TRADE'});
    } else {
      this.props.dispatch(messageActions.success('支付成功'));
      navigation.replace('PaySuccess', {tradeType: 'ORDER'});
    }
  };

  _onForgetPwd = () => {
    this._onPasswordModalClose();
    const {navigation} = this.props;
    this.setState({showPasswordModal: false});
    navigation.replace('Safe');
  };

  render() {
    const {
      order,
      payment_list,
      payment_plugin_id,
      balance,
      showPasswordModal,
      need_pay_price,
    } = this.state;
    if (!order.pay_type_text) {
      return <View />;
    }
    return (
      <View style={styles.container}>
        <StatusBar barStyle="dark-content" />
        <View style={styles.inner_delivery}>
          <Image
            style={styles.inner_delivery_img}
            source={require('../../images/icon-inner-delivery.png')}
          />
          <View style={styles.content_delivery}>
            <F16Text>
              支付方式：
              {order.pay_type_text === 'ONLINE' ? '在线支付' : '货到付款'}
            </F16Text>
            <F16Text>
              订单金额：￥{Foundation.formatPrice(need_pay_price)}
            </F16Text>
          </View>
        </View>
        <View style={styles.btns_delivery}>
          <TextLabel
            style={styles.btnsItem}
            text="查看订单"
            onPress={() => this.props.navigation.replace('MyOrder')}
          />
          <TextLabel
            style={styles.btnsItem}
            text="返回首页"
            onPress={() => this.props.navigation.navigate('Home')}
          />
        </View>
        {order.pay_type_text === 'ONLINE' && need_pay_price !== 0 && (
          <View>
            <View style={styles.need_pay}>
              <Cell
                title="需支付"
                disabled
                arrow={false}
                label={<Price price={need_pay_price} />}
              />
            </View>
            <ScrollView>
              <Cell
                line="bottom"
                key={balancePayment.plugin_id}
                title={balancePayment.method_name}
                label={
                  <Text>预存款余额￥{Foundation.formatPrice(balance)}</Text>
                }
                disabled={balance === 0}
                icon={
                  <Image
                    style={styles.payment_icon}
                    source={balancePayment.image}
                  />
                }
                onPress={() => this._onSelectPayment(balancePayment)}
                arrow={
                  <Checkbox
                    disabled={balance === 0}
                    style={styles.checkbox}
                    checked={payment_plugin_id === balancePayment.plugin_id}
                    onPress={() => this._onSelectPayment(balancePayment)}
                  />
                }
              />
              {payment_list.map(item => {
                return (
                  <Cell
                    line="bottom"
                    key={item.plugin_id}
                    title={item.method_name}
                    icon={
                      <Image
                        style={styles.payment_icon}
                        source={paymentIcons[item.plugin_id]}
                      />
                    }
                    onPress={() => this._onSelectPayment(item)}
                    arrow={
                      <Checkbox
                        style={styles.checkbox}
                        checked={item.plugin_id === payment_plugin_id}
                        onPress={() => this._onSelectPayment(item)}
                      />
                    }
                  />
                );
              })}
            </ScrollView>
          </View>
        )}

        {order.pay_type_text === 'ONLINE' && need_pay_price !== 0 && (
          <BigButton
            style={styles.pay_btn}
            title={`去支付${Foundation.formatPrice(need_pay_price)}元`}
            onPress={this._onInitiatePay}
          />
        )}
        <Modal
          header={<Text>支付密码</Text>}
          style={styles.payPassowrdModal}
          isOpen={showPasswordModal}
          onRequestClose={this._onPasswordModalClose}
          onClosed={this._onPasswordModalClose}>
          <View style={styles.passwordModal}>
            <PayPassword
              style={styles.payPassowrd}
              maxLength={6}
              onChange={this._onPasswordChange}
            />
            <Text
              onPress={this._onForgetPwd}
              style={{color: 'red', marginTop: 10}}>
              找回支付密码
            </Text>
          </View>
        </Modal>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  need_pay: {
    marginBottom: 20,
  },
  balanceCheckbox: {
    marginRight: 20,
  },
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
    height: 50,
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
  payPassowrdModal: {
    height: 200,
  },
  payPassowrd: {
    flexDirection: 'row',
    alignItems: 'center',
    alignSelf: 'center',
  },
  passwordModal: {
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    height: 80,
  },
  forgetTips: {
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    bottom: 0,
    width: Screen.width,
    height: 150,
  },
  forget_pwd: {
    position: 'absolute',
    bottom: 0,
    width: Screen.width,
    marginBottom: isIphoneX() ? 30 : 0,
    backgroundColor: '#F59C1A',
  },
});

export default connect()(CashierScene);
