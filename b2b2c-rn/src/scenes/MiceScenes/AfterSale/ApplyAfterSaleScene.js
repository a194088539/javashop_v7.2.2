import React, {Component} from 'react';
import {
  Image,
  StyleSheet,
  NativeModules,
  NativeEventEmitter,
  TouchableOpacity,
  View,
  Text,
  Picker,
  TextInput,
  Platform,
  DeviceEventEmitter,
} from 'react-native';
import {connect} from 'react-redux';
import {navigate} from '../../../navigator/NavigationService';
import {colors} from '../../../../config';
import {Foundation, request, Screen, RegExp} from '../../../utils';
import {F16Text} from '../../../widgets/Text';
import {BigButton, Loading, Checkbox, TextLabel} from '../../../widgets';
import * as API_AfterSale from '../../../apis/after-sale';
import * as API_Common from '../../../apis/common';
import Icon from 'react-native-vector-icons/Ionicons';
import {Modal} from '../../../components';
import {isIphoneX} from 'react-native-iphone-x-helper';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import ImagePicker from 'react-native-image-crop-picker';
import {messageActions} from '../../../redux/actions';

class ApplyAfterSaleScene extends Component {
  static navigationOptions = {
    title: '申请售后',
  };

  constructor(props, context) {
    super(props, context);
    const {params} = this.props.navigation.state;
    this.sn = params.order_sn;
    this.sku_id = params.sku_id;
    this.service_type = params.service_type;

    this.state = {
      applyInfo: '',
      /** 申请表单数据 */
      applyForm: {
        reason: this._getReasonActions(this.service_type)[0].name,
        images: [],
        return_num: null,
        ship_addr: '',
        ship_name: '',
        ship_mobile: '',
        account_type: 'ALIPAY',
        base64Img: [],
        formDatas: [],
      },
      /** 取消原因下拉框数据 */
      reasonSelectActions: this._getReasonActions(this.service_type),
      /** 账户类型下拉框选中的值 */
      accountTypeText: '请选择账户类型',
      /** 账户类型下拉框数据 */
      accountTypeSelectActions: [
        {name: '支付宝', value: 'ALIPAY'},
        {name: '微信', value: 'WEIXINPAY'},
        {name: '银行卡', value: 'BANKTRANSFER'},
      ],
      /** 是否展示申请凭证弹出层 */
      defaultVoucherDialog: false,
      /** 是否展示申请原因下拉框 */
      defaultDialog: false,
      /** 是否展示账户类型下拉框 */
      defaultRefundDialog: false,
      /** 选择的申请凭证 */
      voucherText: '暂无凭证',
      /** 申请凭证CheckBox数据集合 */
      voucherList: [],
      /** 收货地址地区文字展示 */
      addrText: '',
      /** 收货地址地区信息 */
      regions: null,
    };
  }

  componentDidMount() {
    this._getAfterSaleData();
    const addressSelector = NativeModules.AddressSelectorModule;
    this.addressSelector = addressSelector;
    const eventManager = new NativeEventEmitter(addressSelector);
    this.subscription = eventManager.addListener(
      addressSelector.DataEvent,
      async id => {
        const res = await API_Common.getRegionsById(id);
        addressSelector.setData(
          res.map(item => {
            item.id = item.id;
            item.parentId = item.parent_id;
            item.name = item.local_name;
            item.type = item.region_grade;
            return item;
          }),
        );
      },
    );
  }

  /** 获取申请原因下拉框数据 */
  _getReasonActions = serviceType => {
    let actions = [];
    if (serviceType === 'RETURN_GOODS') {
      actions = [
        {name: '商品降价'},
        {name: '商品与页面描述不符'},
        {name: '缺少件'},
        {name: '质量问题'},
        {name: '发错货'},
        {name: '商品损坏'},
        {name: '不想要了'},
        {name: '其他'},
      ];
      return actions;
    } else if (serviceType === 'CHANGE_GOODS') {
      actions = [
        {name: '商品与页面描述不符'},
        {name: '缺少件'},
        {name: '质量问题'},
        {name: '发错货'},
        {name: '商品损坏'},
        {name: '不想要了'},
        {name: '大小/颜色/型号等不合适'},
        {name: '其他'},
      ];
      return actions;
    } else {
      actions = [{name: '商品发货数量不对'}, {name: '其他'}];
      return actions;
    }
  };

  _getAfterSaleData = async () => {
    this.setState({loading: true});
    const {applyForm} = this.state;
    try {
      const applyInfo = await API_AfterSale.getAfterSaleData(
        this.sn,
        this.sku_id,
      );
      let addrText =
        applyInfo.province + applyInfo.city + applyInfo.county + applyInfo.town;
      let regions =
        applyInfo.town_id && applyInfo.town_id > 0
          ? applyInfo.town_id
          : applyInfo.county_id;
      let voucherText = '暂无凭证';
      let voucherList = [];
      if (applyInfo.is_receipt) {
        voucherText = '有发票';
        voucherList = ['有发票'];
      }
      this.setState({
        applyInfo,
        addrText,
        applyForm: {
          ...applyForm,
          return_num: applyInfo.buy_num,
          ship_addr: applyInfo.ship_addr,
          ship_name: applyInfo.ship_name,
          ship_mobile: applyInfo.ship_mobile,
        },
        regions,
        voucherText,
        voucherList,
        loading: false,
      });
    } catch (error) {
      this.setState({
        loading: false,
      });
    }
  };

  _onSelectImage = () => {
    const {applyForm} = this.state;
    const {dispatch} = this.props;
    let {images, formDatas, base64Img} = applyForm;
    if (base64Img.length >= 5) {
      dispatch(messageActions.error('图片最多上传5张'));
      return;
    }

    ImagePicker.openPicker({
      multiple: true,
      forceJpg: true,
      includeBase64: true,
      compressImageQuality: 0.2,
      maxFiles: 5 - base64Img.length,
      loadingLabelText: '正在加载...',
    }).then(photos => {
      formDatas = [];
      //直接展示base64图片
      photos.map(async (photo, index) => {
        let {path, filename, size, mime, data} = photo;
        //base64直接展示
        base64Img.push(data);
        const formData = new FormData();
        formData.append('file', {uri: path, type: mime, name: filename});
        formDatas.push(formData);
      });
      this.setState({applyForm});
      //上传图片
      this._uploadPhoto(formDatas, images, applyForm);
    });

    // ImagePicker.showImagePicker(options, async response => {
    //   if (response.didCancel || response.error) {
    //     return;
    //   }
    //   let {uri, fileName, fileSize, error, type} = response;
    //   if (response.didCancel) {
    //     return;
    //   }
    //   if (error) {
    //     dispatch(messageActions.error(error));
    //     return;
    //   }
    //   if (fileSize > 1024 * 1024 * 5) {
    //     dispatch(messageActions.error('您选的照片过大'));
    //     return;
    //   }
    //   if (uri) {
    //     this.setState({loading: true});
    //   }
    //   const formData = new FormData();
    //   formData.append('file', {uri, type, name: fileName});
    //   try {
    //     const res = await request({
    //       url: API_Common.upload,
    //       data: formData,
    //       method: 'POST',
    //       headers: {
    //         'Content-Type': 'multipart/form-data',
    //       },
    //     });
    //     this.setState({loading: false});
    //     images.push(res.url);
    //     this.setState({applyForm});
    //   } catch (err) {
    //     this.setState({loading: false});
    //     dispatch(messageActions.error('照片上传失败'));
    //   }
    // });
  };

  //上传图片
  _uploadPhoto = (formDatas, images, applyForm) => {
    formDatas.map(async (formData, index) => {
      try {
        const res = await request({
          url: API_Common.upload,
          data: formData,
          method: 'POST',
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
        this.setState({loading: false});
        images.push(res.url);
        this.setState({applyForm});
      } catch (err) {
        this.setState({loading: false});
      }
    });
  };

  _delImage = imgIndex => {
    let {applyForm} = this.state;
    applyForm.base64Img.splice(imgIndex, 1);
    applyForm.images.splice(imgIndex, 1);
    this.setState({applyForm});
  };

  _updateNum = async symbol => {
    const {applyInfo, applyForm} = this.state;
    if (symbol === '-' && applyForm.return_num < 2) {
      return;
    }
    if (symbol === '+' && applyForm.return_num >= applyInfo.buy_num) {
      return;
    }
    let _num =
      symbol === '+' ? applyForm.return_num + 1 : applyForm.return_num - 1;
    this.setState({
      applyForm: {
        ...applyForm,
        return_num: _num,
      },
    });
  };

  _selectAddress = () => {
    const {dispatch} = this.props;
    this.addressSelector.create(async (result1, result2, result3, result4) => {
      if (!result1 || !result2 || !result3) {
        dispatch(messageActions.error('地区选择不完整，请重新选择！'));
        return;
      }
      const l1 = result1 && JSON.parse(result1);
      const l2 = result2 && JSON.parse(result2);
      const l3 = result3 && JSON.parse(result3);
      const l4 = result4 && JSON.parse(result4);
      const regions = [l1, l2, l3, l4].filter(item => !!item);
      const lastReg = regions[regions.length - 1];
      if (regions.length > 3) {
        this._confirmRegion(lastReg.id, regions);
      } else {
        try {
          // 如果能获取到数据，说明地区不是最后一级
          const res = await API_Common.getRegionsById(lastReg.id);
          if (res && !res[0]) {
            // 如果有返回值，但是为空数组，也是最后一级
            this._confirmRegion(lastReg.id, regions);
          } else {
            dispatch(messageActions.error('地区选择不完整，请重新选择！'));
          }
        } catch (e) {
          // 如果获取数据出错了，说明是最后一级
          this._confirmRegion(lastReg.id, regions);
        }
      }
    });
  };

  _confirmRegion = (id, regions) => {
    this.setState({
      applyForm: {
        ...this.state.applyForm,
        region: id,
      },
      addrText: regions.map(region => region.name).join(''),
    });
  };

  handleChooseVoucher = text => {
    let {voucherText, voucherList} = this.state;
    if (!voucherList.includes(text)) {
      voucherList.push(text);
    } else {
      voucherList = voucherList.filter(item => {
        if (item !== text) {
          return item;
        }
      });
    }
    if (voucherList.length > 0) {
      voucherText = voucherList.toString();
    } else {
      voucherText = '暂无凭证';
    }
    this.setState({voucherText, voucherList});
  };

  /** 校验参数 */
  handleCheckParams = () => {
    const {applyForm, applyInfo} = this.state;
    const {dispatch} = this.props;
    // 申请数量校验
    if (!applyForm.return_num) {
      dispatch(messageActions.error('请填写申请数量！'));
      return false;
    }
    if (applyForm.return_num <= 0 || applyForm.return_num > applyInfo.buy_num) {
      dispatch(messageActions.error('申请数量不能小于等于0或大于购买数量！'));
      return false;
    }
    // 申请原因校验
    if (!applyForm.reason || applyForm.reason === '请选择申请原因') {
      dispatch(messageActions.error('请选择申请原因！'));
      return false;
    }
    // 问题描述校验
    if (!applyForm.problem_desc) {
      dispatch(messageActions.error('请输入问题描述！'));
      return false;
    }

    // 如果当前申请的售后服务类型为退货，那么需要对退款相关信息进行校验
    if (this.service_type === 'RETURN_GOODS') {
      // 如果不支持原路退款
      if (!applyInfo.is_retrace && !applyInfo.is_retrace_balance) {
        // 账户类型校验
        if (!applyForm.account_type) {
          dispatch(messageActions.error('请选择账户类型！'));
          return false;
        }

        // 如果账户类型不为银行卡
        if (applyForm.account_type !== 'BANKTRANSFER') {
          // 退款账号校验
          if (!applyForm.return_account) {
            dispatch(messageActions.error('请输入退款账号！'));
            return false;
          }
        } else {
          // 银行名称校验
          if (!applyForm.bank_name) {
            dispatch(messageActions.error('请输入银行名称！'));
            return false;
          }
          // 银行开户行校验
          if (!applyForm.bank_deposit_name) {
            dispatch(messageActions.error('请输入银行开户行！'));
            return false;
          }
          // 银行开户名校验
          if (!applyForm.bank_account_name) {
            dispatch(messageActions.error('请输入银行开户名！'));
            return false;
          }
          // 银行账号校验
          if (!applyForm.bank_account_number) {
            dispatch(messageActions.error('请输入银行账号！'));
            return false;
          }
        }
      }
    }

    // 详细地址校验
    if (!applyForm.ship_addr) {
      dispatch(messageActions.error('请输入详细地址！'));
      return false;
    }

    // 联系人校验
    if (!applyForm.ship_name) {
      dispatch(messageActions.error('请输入联系人！'));
      return false;
    }

    // 联系方式校验
    if (!applyForm.ship_mobile || !RegExp.mobile.test(applyForm.ship_mobile)) {
      dispatch(messageActions.error('请输入正确格式的手机号码！'));
      return false;
    }

    return true;
  };

  _onSubmit = async () => {
    // 校验参数
    if (!this.handleCheckParams()) {
      return false;
    }
    let {applyForm, voucherText, regions} = this.state;
    const {dispatch} = this.props;
    applyForm.order_sn = this.sn;
    applyForm.sku_id = this.sku_id;
    applyForm.service_type = this.service_type;
    applyForm.apply_vouchers = voucherText;
    applyForm.region = regions;

    //判断是否正在上传图片
    if (
      applyForm.base64Img.length != 0 &&
      applyForm.images.length != applyForm.base64Img.length
    ) {
      this.props.dispatch(messageActions.error('正在上传图片，请稍等'));
      return;
    }
    applyForm.base64Img = [];
    if (this.service_type === 'RETURN_GOODS') {
      API_AfterSale.applyReturnGoods(applyForm).then(resp => {
        this.props.dispatch(messageActions.success('提交成功'));
        navigate('MyAfterSale');
        //发送刷新通知给列表
        DeviceEventEmitter.emit('refresh', {
          newMessage: 'refresh',
        });
      });
    } else if (this.service_type === 'CHANGE_GOODS') {
      API_AfterSale.applyChangeGoods(applyForm).then(resp => {
        this.props.dispatch(messageActions.success('提交成功'));
        navigate('MyAfterSale');
        //发送刷新通知给列表
        DeviceEventEmitter.emit('refresh', {
          newMessage: 'refresh',
        });
      });
    } else if (this.service_type === 'SUPPLY_AGAIN_GOODS') {
      API_AfterSale.applyReplaceGoods(applyForm).then(resp => {
        this.props.dispatch(messageActions.success('提交成功'));
        navigate('MyAfterSale');
        //发送刷新通知给列表
        DeviceEventEmitter.emit('refresh', {
          newMessage: 'refresh',
        });
      });
    } else {
      dispatch(messageActions.error('申请的售后服务类型不正确！'));
      return false;
    }
  };

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

  render() {
    const {
      loading,
      addrText,
      original_way,
      defaultDialog,
      defaultVoucherDialog,
      defaultRefundDialog,
      applyInfo,
      applyForm,
      voucherText,
      voucherList,
      reasonSelectActions,
      accountTypeSelectActions,
    } = this.state;
    return (
      <View style={styles.container}>
        <View style={styles.header_tips}>
          <View style={styles.header_tips_view}>
            <Text>
              本次售后服务将由
              <Text style={styles.header_tips_seller_text}>
                {applyInfo.seller_name}
              </Text>
              为您提供
            </Text>
          </View>
        </View>
        <View style={styles.goods_view}>
          <GoodsItem data={applyInfo} />
          <View style={styles.refund_num_view}>
            <View style={styles.num_header}>
              <F16Text>申请数量</F16Text>
            </View>
            <View style={styles.num_footer}>
              <TextLabel
                style={styles.footer_text}
                text="-"
                onPress={() => {
                  this._updateNum('-');
                }}
              />
              <TextLabel
                style={styles.footer_text}
                text={applyForm.return_num}
              />
              <TextLabel
                style={styles.footer_text}
                text="+"
                onPress={() => {
                  this._updateNum('+');
                }}
              />
            </View>
          </View>
        </View>
        <View style={styles.body}>
          <KeyboardAwareScrollView
            enableOnAndroid
            extraScrollHeight={130}
            keyboardOpeningTime={0}>
            <View style={styles.refund}>
              <TouchableOpacity
                style={styles.select_cell}
                onPress={() => {
                  this.setState({
                    defaultDialog: true,
                  });
                }}>
                <Text style={styles.add_input_label}>申请原因</Text>
                <View style={{width: Screen.width - 20 - 22 - 80}}>
                  <Text style={styles.text_size}>{applyForm.reason}</Text>
                </View>
                <Icon name="ios-arrow-forward" color="#A8A9AB" size={20} />
              </TouchableOpacity>
              <View style={styles.input_view}>
                <TextInput
                  multiline={true}
                  maxLength={200}
                  onChangeText={text =>
                    this.setState({
                      applyForm: {...applyForm, problem_desc: text},
                    })
                  }
                  placeholder={
                    '请描述申请售后服务的具体原因，不能超过200个字符，还可上传最多5张图片哦～'
                  }
                  placeholderColor="#777777"
                />
                <View style={styles.images_view}>
                  {applyForm.base64Img &&
                    applyForm.base64Img.map((uri, _index) => (
                      <RefundImage
                        key={_index}
                        uri={uri}
                        onPress={() => this._delImage(_index)}
                      />
                    ))}
                  {applyForm.images.length < 5 && (
                    <TouchableOpacity
                      style={styles.image_view}
                      activeOpacity={1}
                      onPress={() => this._onSelectImage()}>
                      <Image
                        style={styles.camera_image}
                        source={require('../../../images/icon-camera.png')}
                      />
                    </TouchableOpacity>
                  )}
                </View>
              </View>
            </View>
            {/*退款方式*/}
            {this.service_type === 'RETURN_GOODS' && (
              <View style={styles.refund}>
                <View style={styles.header}>
                  <Text style={styles.add_input_label}>退款方式</Text>
                  <View style={styles.refund_item_view}>
                    <Text style={styles.add_input_label}>
                      {applyInfo.is_retrace_balance
                        ? '退款至预存款'
                        : applyInfo.is_retrace
                        ? '原路退回 '
                        : '账号退款 '}
                    </Text>
                    <Image source={require('../../../images/icon-warn.png')} />
                  </View>
                </View>
                {!applyInfo.is_retrace_balance && !applyInfo.is_retrace && (
                  <View style={styles.header}>
                    <Text style={styles.add_input_label}>账户类型</Text>
                    <View style={styles.refund_item_view}>
                      <TouchableOpacity
                        onPress={() => {
                          if (!original_way) {
                            this.setState({
                              defaultRefundDialog: true,
                            });
                          }
                        }}>
                        {(applyInfo.is_retrace && <Text>原路退回</Text>) || (
                          <Text style={styles.add_input_label}>
                            {!applyForm.account_type
                              ? '请选择账户类型'
                              : this._accountType(applyForm.account_type)}
                          </Text>
                        )}
                      </TouchableOpacity>
                      {!original_way && (
                        <Icon
                          name="ios-arrow-forward"
                          color="#A8A9AB"
                          size={20}
                        />
                      )}
                    </View>
                  </View>
                )}

                {!!applyForm.account_type &&
                  applyForm.account_type !== 'BANKTRANSFER' &&
                  !original_way &&
                  !applyInfo.is_retrace_balance && (
                    <View style={styles.header}>
                      <Text style={styles.add_input_label}>退款账户</Text>
                      <View style={styles.refund_item_view}>
                        <TextInput
                          style={styles.text_input_style}
                          maxLength={50}
                          placeholder={'请输入退款账户'}
                          onChangeText={text => {
                            this.setState({
                              applyForm: {...applyForm, return_account: text},
                            });
                          }}
                          placeholderColor="#777777"
                        />
                      </View>
                    </View>
                  )}
                {applyForm.account_type === 'BANKTRANSFER' && (
                  <View>
                    <View style={styles.header}>
                      <Text style={styles.add_input_label}>银行名称</Text>
                      <TextInput
                        style={styles.text_input_style}
                        maxLength={50}
                        placeholder={'请输入银行名称'}
                        onChangeText={text => {
                          this.setState({
                            applyForm: {...applyForm, bank_name: text},
                          });
                        }}
                        placeholderColor="#777777"
                      />
                    </View>
                    <View style={styles.header}>
                      <Text style={styles.add_input_label}>开 户 行</Text>
                      <TextInput
                        style={styles.text_input_style}
                        maxLength={50}
                        placeholder={'请输入开户行'}
                        onChangeText={text => {
                          this.setState({
                            applyForm: {...applyForm, bank_deposit_name: text},
                          });
                        }}
                        placeholderColor="#777777"
                      />
                    </View>
                    <View style={styles.header}>
                      <Text style={styles.add_input_label}>银行户名</Text>
                      <TextInput
                        style={styles.text_input_style}
                        maxLength={50}
                        placeholder={'请输入银行户名'}
                        onChangeText={text => {
                          this.setState({
                            applyForm: {...applyForm, bank_account_name: text},
                          });
                        }}
                        placeholderColor="#777777"
                      />
                    </View>
                    <View style={styles.header}>
                      <Text style={styles.add_input_label}>银行账号</Text>
                      <TextInput
                        style={styles.text_input_style}
                        maxLength={50}
                        placeholder={'请输入银行账号'}
                        onChangeText={text => {
                          this.setState({
                            applyForm: {
                              ...applyForm,
                              bank_account_number: text,
                            },
                          });
                        }}
                        placeholderColor="#777777"
                      />
                    </View>
                  </View>
                )}
              </View>
            )}

            <View style={styles.refund}>
              <View style={styles.header}>
                <Text style={styles.add_input_label}>返回方式</Text>
                <View style={styles.refund_item_view}>
                  <Text style={styles.add_input_label}>快递至第三方卖家</Text>
                  <Image source={require('../../../images/icon-warn.png')} />
                </View>
              </View>
              <View style={styles.header}>
                <Text style={styles.add_input_label}>申请凭证</Text>
                <View style={styles.refund_item_view}>
                  <TouchableOpacity
                    onPress={() => {
                      this.setState({
                        defaultVoucherDialog: true,
                      });
                    }}>
                    <Text style={styles.add_input_label}>{voucherText}</Text>
                  </TouchableOpacity>
                  <Icon name="ios-arrow-forward" color="#A8A9AB" size={20} />
                </View>
              </View>
              <View style={styles.header}>
                <Text style={styles.add_input_label}>收货地址</Text>
                <View style={styles.refund_item_view}>
                  <TouchableOpacity onPress={this._selectAddress}>
                    <Text style={styles.add_input_label}>{addrText}</Text>
                  </TouchableOpacity>
                  <Icon name="ios-arrow-forward" color="#A8A9AB" size={20} />
                </View>
              </View>
              <View style={styles.header}>
                <Text style={styles.add_input_label}>详细地址</Text>
                <TextInput
                  defaultValue={applyForm.ship_addr}
                  style={styles.text_input_style}
                  maxLength={50}
                  placeholder={'请输入详细地址'}
                  onChangeText={text => {
                    this.setState({applyForm: {...applyForm, ship_addr: text}});
                  }}
                  placeholderColor="#777777"
                />
              </View>
              <View style={styles.header}>
                <Text style={styles.add_input_label}>联系人</Text>
                <TextInput
                  defaultValue={applyForm.ship_name}
                  style={styles.text_input_style}
                  maxLength={50}
                  placeholder={'请输入联系人名称'}
                  onChangeText={text => {
                    this.setState({applyForm: {...applyForm, ship_name: text}});
                  }}
                  placeholderColor="#777777"
                />
              </View>
              <View style={styles.header}>
                <Text style={styles.add_input_label}>联系方式</Text>
                <TextInput
                  defaultValue={applyForm.ship_mobile}
                  style={styles.text_input_style}
                  maxLength={50}
                  placeholder={'请输入联系方式'}
                  onChangeText={text => {
                    this.setState({
                      applyForm: {...applyForm, ship_mobile: text},
                    });
                  }}
                  placeholderColor="#777777"
                />
              </View>
              <Text style={styles.bottom_tips_text}>
                提交服务单后，售后专员可能与您电话沟通，请保持手机畅通
              </Text>
            </View>

            {/*售后原因*/}
            <Modal
              style={{height: Screen.height / 3}}
              position="center"
              title="选择售后原因"
              isOpen={defaultDialog}
              onClosed={() => {
                this.setState({defaultDialog: false});
              }}>
              <Picker
                selectedValue={this.state.applyForm.reason}
                onValueChange={itemValue => {
                  console.warn(itemValue);
                  this.setState({
                    applyForm: {...applyForm, reason: itemValue},
                    defaultDialog: Platform.OS === 'ios' ? true : false,
                  });
                }}>
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

            {/*申请凭证*/}
            <Modal
              style={{height: Screen.height / 3}}
              position="center"
              title="选择申请凭证"
              isOpen={defaultVoucherDialog}
              onClosed={() => {
                this.setState({defaultVoucherDialog: false});
              }}>
              <View style={styles.voucher_view}>
                <Checkbox
                  checked={voucherList.includes('有发票')}
                  label="有发票"
                  onPress={() => this.handleChooseVoucher('有发票')}
                />
                <Checkbox
                  checked={voucherList.includes('有检测报告')}
                  label="有检测报告"
                  onPress={() => this.handleChooseVoucher('有检测报告')}
                />
              </View>
            </Modal>
            {/*账户类型*/}
            <Modal
              style={{height: Screen.height / 3}}
              position="center"
              title="选择账户类型"
              isOpen={defaultRefundDialog}
              onClosed={() => {
                this.setState({defaultRefundDialog: false});
              }}>
              <Picker
                selectedValue={applyForm.account_type}
                onValueChange={itemValue =>
                  this.setState({
                    applyForm: {...applyForm, account_type: itemValue},
                    defaultRefundDialog: Platform.OS === 'ios' ? true : false,
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

        <BigButton
          title="提交申请"
          onPress={this._onSubmit}
          style={styles.big_btn}
        />

        <Loading show={loading} />
      </View>
    );
  }
}

const GoodsItem = ({data}) => {
  return (
    <TouchableOpacity
      style={styles.goods_item}
      activeOpacity={1}
      onPress={() => navigate('Goods', {id: data.good_id})}>
      <Image style={styles.goods_item_image} source={{uri: data.goods_img}} />
      <View style={styles.goods_item_info}>
        <Text>{data.goods_name}</Text>
        <View style={styles.goods_item_view}>
          <Text>￥{Foundation.formatPrice(data.goods_price)}</Text>
          <Text style={styles.goods_item_num}>购买数量：{data.buy_num}</Text>
        </View>
      </View>
    </TouchableOpacity>
  );
};

const RefundImage = ({uri, onPress}) => {
  return (
    <View style={styles.image_view}>
      <Image
        style={styles.comment_image}
        source={{uri: `data:image/jpeg;base64,${uri}`}}
      />
      <TouchableOpacity
        style={styles.del_btn}
        activeOpacity={1}
        onPress={onPress}>
        <Icon name="md-trash" size={20} color="red" />
      </TouchableOpacity>
    </View>
  );
};

const IMAGE_WIDTH = (Screen.width - 20 - 50) / 4;
const styles = StyleSheet.create({
  big_btn: {
    marginBottom: isIphoneX() ? 30 : 0,
    width: Screen.width,
  },
  input_view: {
    justifyContent: 'center',
    marginHorizontal: 10,
    marginVertical: 10,
    paddingHorizontal: 3,
    backgroundColor: '#F8F8F8',
  },
  //选择行 字符样式
  add_input_label: {
    textAlign: 'left',
    color: colors.text,
    marginRight: 5,
    fontSize: 16,
  },
  //选择售后类型样式
  select_cell: {
    flexDirection: 'row',
    alignItems: 'center',
    width: Screen.width,
    height: 50,
    borderColor: colors.cell_line_backgroud,
    borderBottomWidth: Screen.onePixel,
    marginLeft: 10,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    width: Screen.width,
    height: 40,
    paddingHorizontal: 15,
  },
  footer: {
    flexDirection: 'row',
    alignItems: 'center',
    width: Screen.width,
    height: 40,
    paddingHorizontal: 15,
  },
  footer_text: {
    width: 40,
    paddingVertical: 2,
    marginRight: 0,
    marginLeft: 1,
    marginBottom: 0,
  },
  container: {
    flex: 1,
  },
  header_tips: {
    height: 40,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#fff',
  },
  header_tips_view: {
    backgroundColor: 'rgb(246, 246, 246)',
    borderRadius: 55,
    borderWidth: 1,
    borderColor: 'rgb(246, 246, 246)',
    alignContent: 'center',
    alignItems: 'center',
    width: 290,
  },
  header_tips_seller_text: {
    fontSize: 14,
    color: 'red',
  },
  goods_view: {
    height: 150,
    width: Screen.width,
  },
  refund_num_view: {
    backgroundColor: '#FFFFFF',
    flexDirection: 'row',
    justifyContent: 'space-between',
    width: Screen.width,
    paddingVertical: 10,
  },
  num_header: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 15,
  },
  num_footer: {
    flexDirection: 'row',
    alignItems: 'center',
    height: 40,
    paddingHorizontal: 15,
  },
  images_view: {
    flexDirection: 'row',
    justifyContent: 'flex-start',
    alignItems: 'center',
    flexWrap: 'wrap',
    width: Screen.width,
    backgroundColor: '#FFFFFF',
    paddingHorizontal: 10,
    paddingTop: 10,
  },
  image_view: {
    justifyContent: 'center',
    alignItems: 'center',
    width: IMAGE_WIDTH + 10,
    height: IMAGE_WIDTH + 40,
    marginBottom: 10,
    borderColor: colors.cell_line_backgroud,
    borderWidth: Screen.onePixel,
    borderRadius: 3,
  },
  del_btn: {
    height: 20,
  },
  comment_image: {
    width: IMAGE_WIDTH,
    height: IMAGE_WIDTH + 10,
  },
  camera_image: {
    width: 35,
    height: 35,
  },
  body: {
    flex: 1,
  },
  refund: {
    marginTop: 10,
    backgroundColor: '#FFFFFF',
    paddingVertical: 5,
  },
  refund_goods: {
    marginTop: 10,
    backgroundColor: '#FFFFFF',
    padding: 10,
  },
  goods_item: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 10,
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
    left: 20,
  },
  text_size: {
    fontSize: 16,
  },
  text_input_style: {
    width: Screen.width - 20 - 22 - 80,
    fontSize: 15,
    textAlign: 'right',
  },
  goods_item_view: {
    flexDirection: 'row',
  },
  refund_item_view: {
    flexDirection: 'row',
  },
  bottom_tips_text: {
    fontSize: 12,
    marginHorizontal: 5,
    marginTop: 20,
  },
  voucher_view: {
    marginHorizontal: 10,
  },
});

export default connect()(ApplyAfterSaleScene);
