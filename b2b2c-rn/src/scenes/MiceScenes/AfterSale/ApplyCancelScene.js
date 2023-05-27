/**
 * Created by Andste on 2018/11/6.
 */
import React, {Component} from 'react';
import {
  Picker,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  Platform,
  View,
} from 'react-native';
import {connect} from 'react-redux';
import {navigate} from '../../../navigator/NavigationService';
import {messageActions} from '../../../redux/actions';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import {isIphoneX} from 'react-native-iphone-x-helper';
import Icon from 'react-native-vector-icons/Ionicons';
import {Modal} from '../../../components';
import {colors} from '../../../../config';
import {Screen, Foundation, RegExp} from '../../../utils';
import {Checkbox} from '../../../widgets';
import * as API_Order from '../../../apis/order';
import * as API_AfterSale from '../../../apis/after-sale';

class ApplyCancelScene extends Component {
  static navigationOptions = {
    title: '取消订单',
  };

  constructor(props) {
    super(props);
    const {params} = this.props.navigation.state;
    this.sn = params.order_sn;
    this.state = {
      loading: false,
      refund_info: {
        refund_price: 0,
        account_type: 'ALIPAY',
        reason: '请选择取消原因',
      },
      reason: '',
      /** 是否已收货 */
      rog: 'NO',
      /** 是否支持原路退款 */
      isRetrace: false,
      /**
       * 是否支持退款至预存款
       */
      isRetraceBalance: false,
      /** 取消原因下拉框数据 */
      reasonSelectActions: [
        {name: '请选择取消原因'},
        {name: '商品无货'},
        {name: '配送时间问题'},
        {name: '不想要了'},
        {name: '商品信息填写错误'},
        {name: '地址信息填写错误'},
        {name: '商品降价'},
        {name: '货物破损已拒签'},
        {name: '订单无物流跟踪记录'},
        {name: '非本人签收'},
        {name: '其他'},
      ],
      /** 账户类型下拉框选中的值 */
      accountTypeText: '请选择账户类型',
      /** 账户类型下拉框数据 */
      accountTypeSelectActions: [
        {name: '支付宝', value: 'ALIPAY'},
        {name: '微信', value: 'WEIXINPAY'},
        {name: '银行卡', value: 'BANKTRANSFER'},
      ],
      accountDialogShow: false,
      reasonDialogShow: false,
    };
  }

  componentDidMount() {
    this.GET_OrderCancelDetail();
  }

  _accountType = type => {
    switch (type) {
      case 'ALIPAY':
        return '支付宝';
      case 'WEIXINPAY':
        return '微信';
      case 'BANKTRANSFER':
        return '银行转账';
      default:
        return '请选择退款方式';
    }
  };

  GET_OrderCancelDetail = () => {
    let {refund_info} = this.state;
    API_Order.getOrderDetail(this.sn).then(response => {
      let order = response;
      let isRetrace = order.is_retrace;
      let isRetraceBalance = order.is_retrace_balance;
      refund_info.refund_price = Foundation.formatPrice(order.order_price);
      let applyService =
        order.order_status === 'SHIPPED' && order.ship_status === 'SHIP_YES';
      this.setState({
        order,
        isRetrace,
        refund_info,
        applyService,
        isRetraceBalance,
      });
    });
  };

  /** 校验参数 */
  handleCheckParams = () => {
    const {refund_info, isRetrace , isRetraceBalance} = this.state;
    const {dispatch} = this.props;
    // 取消原因校验
    if (!refund_info.reason || refund_info.reason === '请选择取消原因') {
      dispatch(messageActions.error('请选择取消原因！'));
      return false;
    }
    // 联系方式校验
    if (!refund_info.mobile || !RegExp.mobile.test(refund_info.mobile)) {
      dispatch(messageActions.error('请输入正确格式的手机号码！'));
      return false;
    }

    // 如果不支持原路退款
    if (!isRetrace && !isRetraceBalance) {
      // 账户类型校验
      if (!refund_info.account_type) {
        dispatch(messageActions.error('请选择账户类型！'));
        return false;
      }

      // 如果账户类型不为银行卡
      if (refund_info.account_type !== 'BANKTRANSFER') {
        // 退款账号校验
        if (!refund_info.return_account) {
          dispatch(messageActions.error('请输入退款账号！'));
          return false;
        }
      } else {
        // 银行名称校验
        if (!refund_info.bank_name) {
          dispatch(messageActions.error('请输入银行名称！'));
          return false;
        }
        // 银行开户行校验
        if (!refund_info.bank_deposit_name) {
          dispatch(messageActions.error('请输入银行开户行！'));
          return false;
        }
        // 银行开户名校验
        if (!refund_info.bank_account_name) {
          dispatch(messageActions.error('请输入银行开户名！'));
          return false;
        }
        // 银行账号校验
        if (!refund_info.bank_account_number) {
          dispatch(messageActions.error('请输入银行账号！'));
          return false;
        }
      }
    }

    return true;
  };

  _handleSubmitApply = () => {
    const {navigation} = this.props;
    if (!this.handleCheckParams()) {
      return false;
    }
    let {refund_info} = this.state;
    const {dispatch} = this.props;
    refund_info.order_sn = this.sn;
    this.setState({loading: true});
    API_AfterSale.applyCancelOrder(refund_info).then(() => {
      dispatch(messageActions.success('提交成功'));
      navigation.state.params.callback();
      navigation.goBack();
    });
  };

  _handleApplyService = () => {
    API_Order.confirmReceipt(this.sn).then(() => {
      navigate('MyAfterSale');
    });
  };

  render() {
    const {
      reasonDialogShow,
      accountDialogShow,
      rog,
      applyService,
      isRetrace,
      isRetraceBalance,
      refund_info,
      reasonSelectActions,
      accountTypeSelectActions,
    } = this.state;
    return (
      <View style={styles.container}>
        <KeyboardAwareScrollView
          keyboardOpeningTime={0}
          enableOnAndroid
          extraScrollHeight={130}>
          <View>
            <Checkbox
              style={styles.cancel_checkbox}
              checked={rog === 'NO'}
              label="未收货"
              onPress={() => this.setState({rog: 'NO'})}
            />
            {applyService && (
              <Checkbox
                style={styles.cancel_checkbox}
                checked={rog === 'YES'}
                label="已收货"
                onPress={() => this.setState({rog: 'YES'})}
              />
            )}
          </View>
          {rog === 'NO' && (
            <View>
              <View style={styles.cancel_tips_view}>
                <Text style={styles.cancel_tips_text}>温馨提示：</Text>
                <Text style={styles.cancel_tips_text}>
                  1. 订单取消后无法恢复；
                </Text>
                <Text style={styles.cancel_tips_text}>
                  2. 订单取消后，使用的优惠券和积分等将不再返还；
                </Text>
                <Text style={styles.cancel_tips_text}>
                  3. 订单取消后，订单中的赠品要随商品一起返还给商家；
                </Text>
                <Text style={styles.cancel_tips_text}>
                  4.
                  订单取消后，如果使用了预存款，那么退款金额将退回到预存款余额中；
                </Text>
              </View>
              <View>
                <View style={styles.item_view}>
                  <Text style={styles.item_text}>退款方式</Text>
                  <Text style={styles.item_text}>
                    {isRetraceBalance
                      ? '退款至预存款'
                      : isRetrace
                      ? '原路退回'
                      : '账号退款'}
                  </Text>
                </View>
                <View style={styles.item_view}>
                  <Text style={styles.item_text}>退款金额</Text>
                  <Text style={styles.item_text}>
                    {refund_info.refund_price}
                  </Text>
                </View>
                <View style={styles.item_view}>
                  <Text style={styles.item_text}>取消原因</Text>
                  <View style={styles.row_view}>
                    <TouchableOpacity
                      onPress={() => {
                        this.setState({
                          reasonDialogShow: true,
                        });
                      }}>
                      <Text style={styles.item_text}>{refund_info.reason}</Text>
                    </TouchableOpacity>
                    <Icon name="ios-arrow-forward" color="#A8A9AB" size={20} />
                  </View>
                </View>
                {!isRetraceBalance && !isRetrace && (
                  <View>
                    <View style={styles.item_view}>
                      <Text style={styles.item_text}>账户类型</Text>
                      <View style={styles.row_view}>
                        <TouchableOpacity
                          onPress={() => {
                            this.setState({
                              accountDialogShow: true,
                            });
                          }}>
                          <Text style={styles.item_text}>
                            {this._accountType(refund_info.account_type)}
                          </Text>
                        </TouchableOpacity>
                        <Icon
                          name="ios-arrow-forward"
                          color="#A8A9AB"
                          size={20}
                        />
                      </View>
                    </View>
                    {refund_info.account_type === 'BANKTRANSFER' && (
                      <View>
                        <View style={styles.item_view}>
                          <Text style={styles.item_text}>银行名称</Text>
                          <TextInput
                            style={styles.item_input_text}
                            maxLength={50}
                            placeholder={'请输入银行名称'}
                            onChangeText={text => {
                              this.setState({
                                refund_info: {...refund_info, bank_name: text},
                              });
                            }}
                            placeholderColor="#777777"
                          />
                        </View>
                        <View style={styles.item_view}>
                          <Text style={styles.item_text}>银行开户行</Text>
                          <TextInput
                            style={styles.item_input_text}
                            maxLength={50}
                            placeholder={'请输入银行开户行'}
                            onChangeText={text => {
                              this.setState({
                                refund_info: {
                                  ...refund_info,
                                  bank_deposit_name: text,
                                },
                              });
                            }}
                            placeholderColor="#777777"
                          />
                        </View>
                        <View style={styles.item_view}>
                          <Text style={styles.item_text}>银行开户名</Text>
                          <TextInput
                            style={styles.item_input_text}
                            maxLength={50}
                            placeholder={'请输入银行开户名'}
                            onChangeText={text => {
                              this.setState({
                                refund_info: {
                                  ...refund_info,
                                  bank_account_name: text,
                                },
                              });
                            }}
                            placeholderColor="#777777"
                          />
                        </View>
                        <View style={styles.item_view}>
                          <Text style={styles.item_text}>银行账号</Text>
                          <TextInput
                            style={styles.item_input_text}
                            maxLength={50}
                            placeholder={'请输入银行账号'}
                            onChangeText={text => {
                              this.setState({
                                refund_info: {
                                  ...refund_info,
                                  bank_account_number: text,
                                },
                              });
                            }}
                            placeholderColor="#777777"
                          />
                        </View>
                      </View>
                    )}
                    <View style={styles.item_view}>
                      <Text style={styles.item_text}>退款账号</Text>
                      <TextInput
                        style={styles.item_input_text}
                        maxLength={50}
                        placeholder={'请输入退款账号'}
                        onChangeText={text => {
                          this.setState({
                            refund_info: {
                              ...refund_info,
                              return_account: text,
                            },
                          });
                        }}
                        placeholderColor="#777777"
                      />
                    </View>
                  </View>
                )}
                <View style={styles.item_view}>
                  <Text style={styles.item_text}>联系方式</Text>
                  <TextInput
                    style={styles.item_input_text}
                    maxLength={50}
                    placeholder={'请输入手机号码'}
                    onChangeText={text => {
                      this.setState({
                        refund_info: {
                          ...refund_info,
                          mobile: text,
                        },
                      });
                    }}
                    placeholderColor="#777777"
                  />
                </View>
              </View>
              <View style={styles.cancel_btn_view}>
                <TouchableOpacity
                  onPress={() => {
                    this.props.navigation.goBack();
                  }}
                  style={styles.cancel_btn}>
                  <Text>取消</Text>
                </TouchableOpacity>
                <TouchableOpacity
                  onPress={this._handleSubmitApply}
                  style={styles.submit_btn}>
                  <Text style={styles.submit_btn_text}>提交</Text>
                </TouchableOpacity>
              </View>
            </View>
          )}
          {rog === 'YES' && (
            <View>
              <View style={styles.cancel_tips_view}>
                <Text style={styles.cancel_tips_text}>温馨提示：</Text>
                <Text style={styles.cancel_tips_text}>
                  1. 当前订单还未确认收货，如果申请售后，则订单自动确认收货；
                </Text>
                <Text style={styles.cancel_tips_text}>
                  2. 如申请售后，使用的优惠券和积分等将不再返还；
                </Text>
                <Text style={styles.cancel_tips_text}>
                  3. 订单中的赠品要随申请售后的商品一起返还给商家；
                </Text>
              </View>
              <View style={styles.aftersale_btn_view}>
                <TouchableOpacity
                  onPress={() => this._handleApplyService()}
                  style={styles.submit_btn}>
                  <Text style={styles.submit_btn_text}>申请售后</Text>
                </TouchableOpacity>
              </View>
            </View>
          )}
          {/*取消原因*/}
          <Modal
            style={{height: Screen.height / 3}}
            position="center"
            title="选择取消原因"
            isOpen={reasonDialogShow}
            onClosed={() => {
              this.setState({reasonDialogShow: false});
            }}>
            <Picker
              selectedValue={refund_info.reason}
              onValueChange={itemValue =>
                this.setState({
                  refund_info: {...refund_info, reason: itemValue},
                  reasonDialogShow: Platform.OS === 'ios' ? true : false,
                })
              }>
              {reasonSelectActions &&
                reasonSelectActions.map(reason => (
                  <Picker.Item
                    key={reason.name}
                    label={reason.name}
                    value={reason.name}
                  />
                ))}
            </Picker>
          </Modal>
          <Modal
            style={{height: Screen.height / 3}}
            position="center"
            title="选择账户类型"
            isOpen={accountDialogShow}
            onClosed={() => {
              this.setState({accountDialogShow: false});
            }}>
            <Picker
              selectedValue={refund_info.account_type}
              onValueChange={itemValue =>
                this.setState({
                  refund_info: {...refund_info, account_type: itemValue},
                  accountDialogShow: Platform.OS === 'ios' ? true : false,
                })
              }>
              {accountTypeSelectActions &&
                accountTypeSelectActions.map(account => (
                  <Picker.Item
                    key={account.value}
                    label={account.name}
                    value={account.value}
                  />
                ))}
            </Picker>
          </Modal>
        </KeyboardAwareScrollView>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#FFFFFF',
  },
  order_sn: {
    width: Screen.width,
    height: 40,
    justifyContent: 'center',
    backgroundColor: '#FFFFFF',
    paddingHorizontal: 10,
  },
  items: {
    marginTop: 10,
  },
  goods_item: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 10,
    paddingVertical: 10,
    width: Screen.width,
    height: 90,
    backgroundColor: '#FFFFFF',
  },
  goods_item_image: {
    width: 70,
    height: 70,
    borderColor: colors.cell_line_backgroud,
    borderWidth: Screen.onePixel,
  },
  goods_item_info: {
    justifyContent: 'space-between',
    width: Screen.width - 20 - 70 - 10,
    height: 70,
  },
  goods_item_num: {
    marginTop: 5,
    color: '#a5a5a5',
  },
  goods_line: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    width: Screen.width,
    height: Screen.onePixel,
    backgroundColor: colors.cell_line_backgroud,
  },
  reason_view: {
    marginTop: 10,
    paddingHorizontal: 10,
    paddingVertical: 10,
    backgroundColor: '#FFFFFF',
    marginBottom: 50,
  },
  reason_input: {
    marginTop: 10,
    width: Screen.width - 20,
    height: 50,
    paddingLeft: 5,
    paddingRight: 5,
    borderColor: colors.cell_line_backgroud,
    borderWidth: Screen.onePixel,
  },
  confirm_btn: {
    position: 'absolute',
    bottom: isIphoneX() ? 30 : 0,
    left: 0,
    width: Screen.width,
    height: 45,
  },
  item_view: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    width: Screen.width,
    height: 40,
    paddingHorizontal: 15,
  },
  item_text: {
    textAlign: 'left',
    color: colors.text,
    marginRight: 5,
    fontSize: 16,
  },
  item_input_text: {
    width: Screen.width - 20 - 22 - 80,
    fontSize: 15,
    textAlign: 'right',
  },
  row_view: {
    flexDirection: 'row',
  },
  cancel_checkbox: {
    justifyContent: 'space-between',
    margin: 10,
  },
  cancel_tips_view: {
    backgroundColor: '#fcf6ed',
    paddingVertical: 10,
  },
  cancel_tips_text: {
    color: '#de8c17',
    paddingHorizontal: 5,
  },
  cancel_btn_view: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginVertical: 10,
    paddingHorizontal: 50,
    width: Screen.width,
  },
  cancel_btn: {
    borderColor: '#e5e5e5',
    justifyContent: 'center',
    alignItems: 'center',
    width: 120,
    height: 40,
    borderRadius: 4,
    borderWidth: 1,
  },
  submit_btn: {
    borderColor: '#4b0',
    backgroundColor: '#4b0',
    justifyContent: 'center',
    alignItems: 'center',
    width: 120,
    height: 40,
    borderRadius: 4,
    borderWidth: 1,
  },
  submit_btn_text: {
    color: '#fff',
  },
  aftersale_btn_view: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    width: Screen.width,
  },
});

export default connect()(ApplyCancelScene);
