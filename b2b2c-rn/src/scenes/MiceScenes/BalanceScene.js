/**
 * Created by lylyulei on 2020/4/16 11:19.
 */
import React, {Component} from 'react';
import {
  Alert,
  BackHandler, DeviceEventEmitter,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import {colors} from '../../../config';
import {Screen} from '../../utils';
import {connect} from 'react-redux';
import * as API_Trade from '../../apis/trade';
import {
  BigButton,
  Cell,
  CellGroup,
  Checkbox,
  Image,
  InputLabel,
} from '../../widgets';
import {messageActions} from '../../redux/actions';
import * as Wechat from 'react-native-wechat';
import Alipay from '@0x5e/react-native-alipay';
import * as API_Member from '../../apis/members';
import {EventEmitter} from 'events';
const emitter = new EventEmitter();

DeviceEventEmitter.addListener('WeChat_Resp', resp => {
  emitter.emit(resp.type, resp);
});
class BalanceScene extends Component {
  static navigationOptions = ({navigation}) => {
    const _onPress = () => {
      navigation.push('BalanceDetail');
    };
    return {
      headerRight: () => (
        <TouchableOpacity style={styles.headerRight} onPress={_onPress}>
          <Text style={styles.rightText}>明细</Text>
        </TouchableOpacity>
      ),
      headerTitle: '账户余额',
      gestureEnabled: false,
    };
  };

  constructor(props) {
    super(props);
    this.state = {
      // 支付方式列表
      payment_list: [],
      // 支付方式id
      payment_plugin_id: null,
      //预存款余额-显示用
      balance: 0,
      //充值金额
      deposit: 0,
      //是否禁用立即支付按钮,默认禁用
      disabled_btn: true,
    };
  }

  componentDidMount() {
    this._paymentList();
    this._getBalance();
  }
  /**
   * 选择支付方式
   * @param item
   * @private
   */
  _onSelectPayment = item => {
    this.setState(
      {payment_plugin_id: item.plugin_id},
      this._disabledPaymentBtn,
    );
  };
  /**
   * 获取余额
   * @returns {AxiosPromise<any>}
   * @private
   */
  _getBalance = async () => {
    const balance = await API_Member.balance();
    this.setState({
      balance,
    });
  };
  /**
   * 获取支付列表
   * @returns {Promise<void>}
   * @private
   */
  _paymentList = async () => {
    const payment_list = await API_Trade.getPaymentList('REACT');
    this.setState({
      payment_list: payment_list,
    });
  };
  /**
   * 充值金额发生改变
   * @param text
   * @private
   */
  _onDepositChange = text => {
    this.setState({deposit: text}, this._disabledPaymentBtn);
  };

  /**
   * 检测是否禁用立即支付按钮
   * @private
   */
  _disabledPaymentBtn = () => {
    const {deposit, payment_plugin_id} = this.state;
    if (payment_plugin_id && deposit > 0) {
      this.setState({disabled_btn: false});
    } else {
      this.setState({disabled_btn: true});
    }
  };

  /**
   * 发起APP支付
   * @private
   */
  _onInitiatePay = async () => {
    const {dispatch} = this.props;
    const {payment_plugin_id, deposit} = this.state;

    const recharge = await API_Member.recharge({price: deposit});

    if (payment_plugin_id === null) {
      dispatch(messageActions.error('请选择一个支付方式！'));
      return;
    }
    // 支付模式
    const pay_mode = 'normal';
    // 客户端类型
    const client_type = 'REACT';
    // 支付编号
    const sn = recharge.recharge_sn;
    //交易类型
    const trade_type = 'RECHARGE';

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
      `sn=${sn},trade_type=${trade_type},payment_plugin_id=${payment_plugin_id},pay_mode=${pay_mode},client_type=${client_type}`,
    );
    // 初始化支付签名
    const signXml = await API_Trade.initiateAppPay(trade_type, sn, {
      payment_plugin_id,
      pay_mode,
      client_type,
    });
    console.log('初始化签名');
    switch (payment_plugin_id) {
      case 'alipayDirectPlugin':
        this._onAliPay(signXml);
        break;
      case 'weixinPayPlugin':
        this._onWechatPay(signXml);
        break;
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
    typeof callback === 'function' && callback();
    this.props.dispatch(messageActions.success('支付成功'));
    navigation.replace('PaySuccess', {tradeType: 'BALANCE'});
  };

  render() {
    const {payment_list, payment_plugin_id, balance, disabled_btn} = this.state;
    return (
      <View style={styles.container}>
        <View style={styles.balanceTop}>
          <Text style={styles.balanceMoney}>￥{balance}</Text>
          <Text>仅限用于本商城购买商品使用，不能提现</Text>
        </View>
        <View style={styles.rechargeContainer}>
          <Text style={styles.rechargeText}>充值</Text>
          <View>
            <InputLabel
              label="金额"
              labelStyle={styles.label_style}
              inputSytle={styles.input_style}
              placeholder="请输入充值金额"
              keyboardType="number-pad"
              validcodeUrl={'请输入正确的充值金额'}
              maxLength={6}
              onChangeText={this._onDepositChange}
            />
          </View>
          <ScrollView>
            {payment_list.map((item, index) => {
              return (
                <CellGroup key={index}>
                  <Cell
                    line="bottom"
                    icon={
                      <Image style={styles.payment_icon} uri={item.image} />
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
                </CellGroup>
              );
            })}
          </ScrollView>
          <BigButton
            style={styles.buyNow}
            disabled={disabled_btn}
            title={'立即支付'}
            onPress={this._onInitiatePay}
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
  balanceTop: {
    alignItems: 'center',
    backgroundColor: '#FFF',
    justifyContent: 'center',
    height: 100,
  },
  balanceMoney: {
    fontSize: 25,
    fontWeight: 'bold',
  },
  tabBarUnderlineStyle: {
    backgroundColor: colors.main,
    height: 1,
    width: 80,
    left: (Screen.width / 2 - 80) / 2,
  },
  label_style: {
    fontSize: 14,
  },
  input_style: {
    fontSize: 14,
  },
  rechargeContainer: {
    marginTop: 20,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#FFF',
    paddingBottom: 20,
  },
  rechargeText: {
    fontSize: 25,
    fontWeight: 'bold',
    margin: 5,
  },
  buyNow: {
    width: Screen.width - 20,
    marginTop: 20,
  },
  payment_icon: {
    width: 150,
    height: 35,
    marginRight: 5,
  },
  checkbox: {
    paddingRight: 0,
  },
  headerRight: {
    width: 44,
    height: 44,
    justifyContent: 'center',
    alignItems: 'center',
    overflow: 'hidden',
  },
  rightText: {
    width: 44,
    height: 44,
    fontSize: 17,
    fontWeight: '400',
    color: colors.text,
    textAlign: 'center',
    flex: 1,
    lineHeight: 44,
  },
});

export default connect()(BalanceScene);
