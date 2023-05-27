import React, {Component} from 'react';
import {
  FlatList,
  Image,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Text,
  View,
  Picker,
  TextInput,
} from 'react-native';
import {navigate} from '../../../navigator/NavigationService';
import {colors} from '../../../../config';
import {Foundation, RegExp, Screen} from '../../../utils';
import {Cell, CellGroup, Loading, TextItem, TextLabel} from '../../../widgets';
import * as API_AfterSale from '../../../apis/after-sale';
import Icon from 'react-native-vector-icons/Ionicons';
import {Modal} from '../../../components';
import DatePicker from 'react-native-datepicker';
import ImageViewer from 'react-native-image-zoom-viewer';
import {connect} from 'react-redux';
import {messageActions} from '../../../redux/actions';

class AfterSaleDetailScene extends Component {
  static navigationOptions = {
    title: '服务单详情',
  };

  constructor(props, context) {
    super(props, context);
    const {params} = this.props.navigation.state;
    this.sn = params.sn;
    this.state = {
      allowable: {},
      log: {},
      change_info: {},
      serviceDetail: {},
      express_info: {},
      loading: false,
      showReturnModal: false,
      showExpressModal: false,
      showExpressCompanyModal: false,
      companyName: '请选择快递公司',
      shipDate: '请选择发货时间',
      logiList: [],
      audit_remark: '',
    };
    this.shipParams = {
      courier_company_id: {},
      courier_number: {},
      ship_time: {},
      service_sn: {},
    };
  }

  _showReturnModal = () => {
    this.setState({showReturnModal: true});
  };

  _onModalClosed = () => {
    this.setState({showReturnModal: false});
  };

  _showExpressModal = () => {
    this.setState({showExpressModal: true});
  };

  _onExpressModalClosed = () => {
    this.setState({showExpressModal: false});
  };

  _showExpressCompanyModal = () => {
    this.setState({showExpressCompanyModal: true});
  };

  _onExpressCompanyModalClosed = () => {
    this.setState({showExpressCompanyModal: false});
  };

  componentDidMount() {
    this._getAfterSaleData();
  }

  statusFilter = val => {
    switch (val) {
      case 'APPLY':
        return '售后服务申请成功，等待商家审核';
      case 'PASS':
        return '售后服务申请审核通过';
      case 'REFUSE':
        return '售后服务申请已被商家拒绝，如有疑问请及时联系商家';
      case 'FULL_COURIER':
        return '申请售后的商品已经寄出，等待商家收货';
      case 'STOCK_IN':
        return '商家已将售后商品入库';
      case 'WAIT_FOR_MANUAL':
        return '等待平台进行人工退款';
      case 'REFUNDING':
        return '商家退款中，请您耐心等待';
      case 'COMPLETED':
        return '售后服务已完成，感谢您对Javashop的支持';
      case 'ERROR_EXCEPTION':
        return '系统生成新订单异常，等待商家手动创建新订单';
      case 'CLOSED':
        return '售后服务已关闭';
      default:
        return '';
    }
  };

  refundWayFilter = val => {
    switch (val) {
      case 'ACCOUNT':
        return <Text>账户退款</Text>;
      case 'OFFLINE':
        return <Text>线下退款</Text>;
      case 'ORIGINAL':
        return <Text>原路退回</Text>;
      default:
        return <Text>其它</Text>;
    }
  };

  accountTypeFilter = val => {
    switch (val) {
      case 'WEIXINPAY':
        return <Text>微信</Text>;
      case 'ALIPAY':
        return <Text>支付宝</Text>;
      case 'BANKTRANSFER':
        return <Text>银行卡</Text>;
      default:
        return <Text>其它</Text>;
    }
  };

  _getAfterSaleData = async () => {
    this.setState({loading: true});
    try {
      const afterSaleData = await API_AfterSale.getServiceDetail(this.sn);
      let imagesList = [];
      afterSaleData.images.forEach(item => {
        imagesList.push(item.img);
      });
      let images = imagesList.map(item => ({
        url: item,
      }));
      this.setState({
        loading: false,
        serviceDetail: afterSaleData,
        log: afterSaleData.logs[0],
        allowable: afterSaleData.allowable,
        goodsList: afterSaleData.goods_list,
        change_info: afterSaleData.change_info,
        refund_info: afterSaleData.refund_info,
        express_info: afterSaleData.express_info,
        logiList: afterSaleData.logi_list,
        refundShow:
          afterSaleData.service_type === 'RETURN_GOODS' ||
          afterSaleData.service_type === 'ORDER_CANCEL',
        accountShow:
          (afterSaleData.service_type === 'RETURN_GOODS' ||
            afterSaleData.service_type === 'ORDER_CANCEL') &&
          afterSaleData.refund_info.refund_way === 'ACCOUNT',
        bankShow:
          (afterSaleData.service_type === 'RETURN_GOODS' ||
            afterSaleData.service_type === 'ORDER_CANCEL') &&
          afterSaleData.refund_info.refund_way === 'ACCOUNT' &&
          afterSaleData.refund_info.account_type === 'BANKTRANSFER',
        imagesList,
        showModal: false,
        imageIndex: 0,
        images,
        audit_remark: afterSaleData.audit_remark,
      });
    } catch (error) {
      this.setState({loading: false});
      navigate('MyAfterSale');
    }
  };

  _getItemLayou = (data, index) => ({
    length: 90 + Screen.onePixel,
    offset: (90 + Screen.onePixel) * index,
    index,
  });

  //选择快递公司
  _selectExpressCompany = (companyId, companyName) => {
    this.state.companyName = companyName;
    this.shipParams.courier_company_id = companyId;
    this._onExpressCompanyModalClosed();
  };

  //保存发货信息
  _saveShip = async () => {
    const {dispatch} = this.props;
    if (!RegExp.integer.test(this.shipParams.courier_number)) {
      dispatch(messageActions.error('快递单号格式有误！'));
      return;
    }
    this.shipParams.service_sn = this.sn;
    await API_AfterSale.saveShip(this.shipParams);
  };

  _onShowModal = index => {
    this.setState({showModal: true, imageIndex: index});
  };
  _onCloseModal = () => {
    this.setState({showModal: false, imageIndex: 0});
  };

  _onDateChange = date => {
    let _birthday = date.replace(/\D/g, '-').substr(0, date.length - 1);
    let _unix = Foundation.dateToUnix(_birthday);
    let _today = parseInt(
      (new Date(new Date().toLocaleDateString()).getTime() +
        24 * 60 * 60 * 1000 -
        1) /
        1000,
    );
    this.setState({shipDate: date});
    this.shipParams.ship_time = _unix;
  };

  _onCodeText = text => {
    this.shipParams.courier_number = text;
  };

  _renderItem = ({item}) => <GoodsItem data={item} nav={this.nav} />;

  render() {
    const {
      serviceDetail,
      allowable,
      log,
      goodsList,
      change_info,
      refundShow,
      refund_info,
      express_info,
      accountShow,
      bankShow,
      loading,
      showReturnModal,
      showExpressModal,
      showExpressCompanyModal,
      logiList,
      showModal,
      imageIndex,
      images,
      audit_remark,
    } = this.state;

    return (
      <View style={styles.container}>
        <ScrollView>
          <View style={styles.header_tips}>
            <View style={styles.header_tips_view}>
              <Text>
                本次售后服务将由
                <Text style={styles.header_tips_seller_text}>
                  {serviceDetail.seller_name}
                </Text>
                为您提供
              </Text>
            </View>
          </View>
          <View style={styles.refund_service_status_view}>
            <Text style={styles.refund_service_status_title_text}>
              {serviceDetail.service_status_text}
            </Text>
            {allowable.allow_ship && (
              <Text style={styles.refund_service_status_text}>
                请您尽快将申请售后的商品退还给卖家
              </Text>
            )}
            {!allowable.allow_ship && (
              <Text style={styles.refund_service_status_text}>
                {this.statusFilter(serviceDetail.service_status)}
              </Text>
            )}
          </View>
          <TouchableOpacity
            style={styles.refund_service_log_view}
            onPress={() =>
              navigate('AfterSaleLog', {service_sn: serviceDetail.sn})
            }>
            <View style={styles.refund_service_log_view_head}>
              <Text style={styles.refund_service_log_title}>操作日志</Text>
              <Text>{log.log_detail}</Text>
            </View>
            <Icon name="ios-arrow-forward" color="#A8A9AB" size={20} />
          </TouchableOpacity>
          <View style={styles.refund_goods}>
            <FlatList
              data={goodsList}
              renderItem={this._renderItem}
              keyExtractor={(_, index) => String(index)}
              getItemLayout={this._getItemLayout}
            />
          </View>
          {/*物流信息*/}
          {allowable.allow_ship && (
            <View style={styles.express_info_view}>
              <Text style={styles.express_info_text}>填写物流信息</Text>
              <View style={styles.express_info_company_view}>
                <Text style={styles.express_info_company_text}>快递公司：</Text>
                <TouchableOpacity onPress={this._showExpressCompanyModal}>
                  <Text>{this.state.companyName} ></Text>
                </TouchableOpacity>
              </View>
              <View style={styles.express_info_company_view}>
                <Text style={styles.express_info_company_text}>快递单号：</Text>
                <TextInput
                  style={styles.express_info_company_textInput}
                  placeholder={'请输入快递单号'}
                  maxLength={20}
                  keyboardType="numeric"
                  onChangeText={this._onCodeText}
                />
              </View>
              <View style={styles.express_info_company_view}>
                <Text style={styles.express_info_company_text}>发货时间：</Text>
                <TouchableOpacity
                  onPress={() => this._datepicker.onPressDate()}>
                  <Text>{this.state.shipDate} ></Text>
                </TouchableOpacity>
              </View>
              <TextLabel
                text="提交"
                style={styles.goods_item_apply_btn}
                tintColor={colors.main}
                onPress={this._saveShip}
              />
            </View>
          )}
          <View style={styles.refund}>
            <TextItem title="服务单号" content={serviceDetail.sn} />
            <TextItem title="订单编号" content={serviceDetail.order_sn} />
            {!!serviceDetail.new_order_sn && (
              <TextItem
                title="新订单编号"
                content={serviceDetail.new_order_sn}
              />
            )}
            <TextItem
              title="服务类型"
              content={serviceDetail.service_type_text}
            />
            <TextItem title="申请原因" content={serviceDetail.reason} />
            {!!serviceDetail.apply_vouchers && (
              <TextItem
                title="申请凭证"
                content={serviceDetail.apply_vouchers}
              />
            )}
            {!!serviceDetail.problem_desc && (
              <TextItem title="问题描述" content={serviceDetail.problem_desc} />
            )}
            <View style={styles.goods_item_image_view}>
              {serviceDetail.images &&
                serviceDetail.images.map((img, _index) => (
                  <TouchableOpacity
                    onPress={() => this._onShowModal(_index)}
                    key={_index}
                    activeOpacity={1}>
                    <Image
                      style={styles.goods_item_image}
                      source={{uri: img.img}}
                    />
                  </TouchableOpacity>
                ))}
            </View>
            <TextItem
              title="收货地址"
              content={`${change_info.province || ''} ${change_info.city ||
                ''} ${change_info.county || ''} ${change_info.town ||
                ''} ${change_info.ship_addr || ''} `}
            />
            <TextItem title="联系人" content={change_info.ship_name} />
            <TextItem title="联系方式" content={change_info.ship_mobile} />
            {allowable.allow_show_return_addr && (
              <TextItem
                title="退货地址信息"
                componentsContent={
                  <TouchableOpacity onPress={this._showReturnModal}>
                    <Text>点击查看</Text>
                  </TouchableOpacity>
                }
              />
            )}
            {allowable.allow_show_ship_info && (
              <TextItem
                title="发货单信息"
                componentsContent={
                  <TouchableOpacity onPress={this._showExpressModal}>
                    <Text>点击查看</Text>
                  </TouchableOpacity>
                }
              />
            )}
            <ItemLine />
            {refundShow && (
              <View>
                <TextItem
                  title="申请退款金额"
                  content={'¥' + refund_info.refund_price}
                />
                {refund_info.agree_price && (
                  <TextItem
                    title="同意退款金额"
                    content={'¥' + refund_info.agree_price}
                  />
                )}
                {refund_info.actual_price && (
                  <TextItem
                    title="实际退款金额"
                    content={'¥' + refund_info.actual_price}
                  />
                )}
                {refund_info.refund_time && (
                  <TextItem
                    title="退款时间"
                    content={Foundation.unixToDate(refund_info.refund_time)}
                  />
                )}
                {refund_info.refund_way && (
                  <TextItem
                    title="退款方式"
                    content={this.refundWayFilter(refund_info.refund_way)}
                  />
                )}
                {accountShow && (
                  <TextItem
                    title="账户类型"
                    content={this.accountTypeFilter(refund_info.account_type)}
                  />
                )}
                {accountShow && !bankShow && (
                  <TextItem
                    title="退款账号"
                    content={refund_info.return_account}
                  />
                )}
                {audit_remark && (
                  <TextItem title="商家备注" content={audit_remark} />
                )}
                {bankShow && (
                  <View>
                    <TextItem
                      title="银行名称"
                      content={refund_info.bank_name}
                    />
                    <TextItem
                      title="银行账号"
                      content={refund_info.bank_account_number}
                    />
                    <TextItem
                      title="银行开户名"
                      content={refund_info.bank_account_name}
                    />
                    <TextItem
                      title="银行开户行"
                      content={refund_info.bank_deposit_name}
                    />
                  </View>
                )}
              </View>
            )}
          </View>
        </ScrollView>
        <Loading show={loading} />
        {/*退货地址*/}
        <Modal
          title="退货地址"
          style={styles.modal_style}
          isOpen={showReturnModal}
          onClosed={this._onModalClosed}>
          <View style={styles.pros_item}>
            <Text style={styles.promotion_tag}>
              {serviceDetail != null ? (
                <Text>{serviceDetail.return_addr}</Text>
              ) : (
                <Text>暂无退货信息</Text>
              )}
            </Text>
          </View>
        </Modal>
        {/*发货单信息*/}
        <Modal
          title="发货单信息"
          style={styles.modal_style}
          isOpen={showExpressModal}
          onClosed={this._onExpressModalClosed}>
          <View style={styles.pros_item}>
            <Text style={styles.promotion_tag}>
              {express_info != null ? (
                `快递公司:${express_info.courier_company}, 快递单号:${
                  express_info.courier_number
                }, 发货时间:${Foundation.unixToDate(
                  express_info.ship_time,
                  'yyyy-MM-dd hh:mm:ss',
                )}`
              ) : (
                <Text>暂无发货单信息</Text>
              )}
            </Text>
          </View>
        </Modal>
        {/*发货单信息*/}
        <Modal
          title="快递公司"
          style={styles.modal_style}
          isOpen={showExpressCompanyModal}
          onClosed={this._onExpressCompanyModalClosed}>
          <View style={styles.pros_item}>
            <CellGroup marginBottom={true}>
              {logiList.map((logi, index) => {
                return (
                  <Cell
                    title={logi.name}
                    onPress={() =>
                      this._selectExpressCompany(logi.id, logi.name)
                    }
                  />
                );
              })}
            </CellGroup>
          </View>
        </Modal>
        <Modal
          title="问题描述"
          isOpen={showModal}
          onClosed={this._onCloseModal}
          transparent={false}>
          <ImageViewer
            style={styles.issue_img}
            imageUrls={images}
            index={imageIndex}
            enablePreload={true}
            saveToLocalByLongPress={false}
            enableSwipeDown={true}
            onSwipeDown={this._onCloseModal}
            onCancel={this._onCloseModal}
            enableImageZoom={true}
          />
        </Modal>
        <DatePicker
          style={styles.datepicker}
          ref={datepicker => (this._datepicker = datepicker)}
          showIcon={false}
          hideText={true}
          format="YYYY年MM月DD日"
          confirmBtnText="确定"
          cancelBtnText="取消"
          customStyles={{
            dateTouchBody: {
              borderWidth: 0,
            },
            dateText: {
              color: '#BCBDBF',
              fontSize: 16,
              borderWidth: 0,
            },
          }}
          onDateChange={this._onDateChange}
        />
      </View>
    );
  }
}

const GoodsItem = ({data}) => {
  return (
    <View style={styles.refund_order_item_view}>
      <TouchableOpacity
        style={[styles.shop_item]}
        onPress={() => {
          navigate('Goods', {id: data.goods_id});
        }}>
        <View style={styles.goods_item} activeOpacity={1}>
          <Image
            style={styles.goods_item_image}
            source={{uri: data.goods_image}}
          />
          <View style={styles.goods_item_info}>
            <Text>{data.goods_name}</Text>
            <View>
              <Text style={styles.goods_item_num}>单价：¥{data.price}</Text>
            </View>
          </View>
        </View>
      </TouchableOpacity>
    </View>
  );
};

const ItemLine = () => <View style={styles.item_line} />;
const IMAGE_WIDTH = (Screen.width - 20 - 40) / 3;
const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  refund: {
    marginTop: 10,
    backgroundColor: '#FFFFFF',
    paddingVertical: 5,
  },
  refund_goods: {
    maxHeight: 250,
    marginTop: 100,
    backgroundColor: '#FFFFFF',
    padding: 10,
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
    width: IMAGE_WIDTH,
    height: IMAGE_WIDTH,
    margin: 3,
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
  item_line: {
    width: Screen.width,
    height: Screen.onePixel,
    backgroundColor: colors.gray_background,
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
  refund_service_status_view: {
    backgroundColor: '#ff614d',
    height: 120,
  },
  refund_service_status_text: {
    color: '#ffffff',
    fontSize: 14,
    marginTop: 15,
    marginLeft: 15,
  },
  refund_service_status_title_text: {
    color: '#ffffff',
    fontSize: 18,
    marginTop: 10,
    marginLeft: 15,
  },
  refund_service_log_view: {
    flexDirection: 'row',
    position: 'absolute',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: '#FFFFFF',
    width: Screen.width - 20,
    height: 120,
    margin: 10,
    padding: 10,
    top: 120,
    zIndex: 100,
    borderRadius: 11,
  },
  refund_service_log_title: {
    color: '#2e2d2d',
  },
  goods_item_image_view: {
    flexDirection: 'row',
    width: Screen.width,
    justifyContent: 'flex-start',
    alignItems: 'center',
    flexWrap: 'wrap',
  },
  promotion_cell: {
    flexDirection: 'row',
  },
  modal_style: {
    backgroundColor: colors.gray_background,
  },
  pros_item: {
    height: 100,
  },
  promotion_tag: {
    height: 80,
    fontSize: 16,
  },
  express_info_view: {
    backgroundColor: '#FFFFFF',
    padding: 10,
    marginTop: 10,
    height: 200,
  },
  express_info_text: {
    fontSize: 20,
    fontWeight: '400',
  },
  express_info_company_view: {
    flex: 1,
    flexDirection: 'row',
  },
  express_info_company_text: {
    fontSize: 14,
    fontWeight: '400',
  },
  express_info_company_textInput: {
    backgroundColor: '#FFFFFF',
    marginTop: -25,
    width: Screen.width - 20 - 70 - 10,
  },
  datepicker: {
    width: 0,
    height: 0,
  },
  express_info_company_btn: {
    paddingLeft: 100,
  },
  goods_item_apply_btn: {
    width: 100,
    borderRadius: 20,
    marginLeft: 100,
  },
  issue_img: {
    width: Screen.width,
    height: Screen.width,
  },
});

export default connect()(AfterSaleDetailScene);
