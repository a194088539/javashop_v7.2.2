/**
 * Created by Andste on 2018/11/6.
 */
import React, {Component} from 'react';
import {
  FlatList,
  Image,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  View,
  Text,
} from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';
import {navigate} from '../../navigator/NavigationService';
import {colors} from '../../../config';
import {Foundation, Screen} from '../../utils';
import {F16Text, F14Text} from '../../widgets/Text';
import {Loading, TextLabel, TextItem, DashLine} from '../../widgets';
import * as API_Order from '../../apis/order';
import {main} from '../../../config/colors';

export default class OrderDetailScene extends Component {
  static navigationOptions = {
    title: '订单详情',
  };

  constructor(props, context) {
    super(props, context);
    const {params} = this.props.navigation.state;
    this.sn = params.data.sn;
    this.state = {
      order: '',
      loading: false,
    };
  }

  async componentDidMount() {
    this._getOrderData();
  }

  _getOrderData = async () => {
    this.setState({loading: true});
    try {
      const order = await API_Order.getOrderDetail(this.sn);
      this.setState({loading: false, order});
    } catch (error) {
      this.setState({loading: false});
      navigate('MyOrder');
    }
  };

  _getItemLayout = (data, index) => ({
    length: 90 + Screen.onePixel,
    offset: (90 + Screen.onePixel) * index,
    index,
  });

  _renderItem = ({item}) => (
    <GoodsItem
      data={item}
      order_sn={this.sn}
      orderDetail={this.state.order}
      refreshDetail={this._getOrderData}
      nav={this.nav}
    />
  );

  _receiptType = type => {
    switch (type) {
      case 'VATORDINARY':
        return '增值税普通发票';
      case 'ELECTRO':
        return '电子普通发票';
      case 'VATOSPECIAL':
        return '增值税专用发票';
      default:
        return '不开发票';
    }
  };

  render() {
    const {order, loading} = this.state;
    return (
      <View style={styles.container}>
        {order.service_status == 'APPLY' ? (
          <View style={styles.tip_view}>
            <Text style={styles.tip_view_text}>
              当前订单已经申请售后，您可以进入售后管理中查看取消进度
            </Text>
          </View>
        ) : (
          undefined
        )}
        <View style={styles.body}>
          <ScrollView>
            {order.order_type === 'PINTUAN' ? (
              <View style={styles.trade_status}>
                <F16Text>交易状态</F16Text>
                <F16Text style={{color: colors.main}}>
                  {order.ping_tuan_status}
                </F16Text>
              </View>
            ) : (
              undefined
            )}
            <View style={styles.order_sn}>
              <F16Text>订单号：{order.sn}</F16Text>
            </View>
            <View style={styles.order_address}>
              <View style={styles.order_address_icon}>
                <Icon name="ios-pin-outline" size={18} />
              </View>
              <View style={styles.order_address_content}>
                <View style={styles.order_address_info}>
                  <F16Text>{order.ship_name} </F16Text>
                  <F16Text>
                    {Foundation.secrecyMobile(order.ship_mobile)}
                  </F16Text>
                </View>
                <View style={styles.order_address_add}>
                  <F16Text>
                    地址：
                    {order.ship_province +
                      order.ship_city +
                      order.ship_town +
                      order.ship_county}{' '}
                    {order.ship_addr}
                  </F16Text>
                </View>
              </View>
            </View>
            <View style={styles.order_items}>
              <TouchableOpacity
                style={[styles.shop_item]}
                onPress={() => {
                  navigate('Shop', {id: order.seller_id});
                }}>
                <View style={styles.shop_item_title}>
                  <Image
                    style={styles.gift_title_img}
                    source={require('../../images/icon-shop_comment.png')}
                  />
                  <F16Text style={styles.gift_title_text}>
                    {order.seller_name}
                  </F16Text>
                </View>
                <Icon name="ios-arrow-forward" color="#A8A9AB" size={20} />
              </TouchableOpacity>
              <DashLine />
              <FlatList
                data={order.order_sku_list}
                renderItem={this._renderItem}
                keyExtractor={(_, index) => String(index)}
                getItemLayout={this._getItemLayout}
              />
              {order.order_status === 'SHIPPED' && (
                <View
                  style={{flexDirection: 'row', justifyContent: 'flex-end'}}>
                  <TextLabel
                    style={styles.footer_btn}
                    onPress={() =>
                      navigate('Express', {order, callback: this._getOrderData})
                    }
                    text="查看物流"
                  />
                </View>
              )}
            </View>
            {((order.gift_list && order.gift_list.length > 0) ||
              order.gift_coupon) && (
              <View style={styles.order_items}>
                <View style={[styles.gift_item]}>
                  <Image
                    style={styles.gift_title_img}
                    source={require('../../images/icon-order-gift.png')}
                  />
                  <F16Text style={styles.gift_title_text}>赠品列表</F16Text>
                </View>
                <DashLine />
                {order.gift_list &&
                  order.gift_list.map((gift, giftIndex) => (
                    <View key={giftIndex} style={styles.gift_item}>
                      <Image
                        style={styles.gift_img}
                        source={{uri: gift.gift_img}}
                      />
                      <View style={styles.gift_name}>
                        <F16Text>
                          {' '}
                          价值 ￥{Foundation.formatPrice(
                            gift.gift_price,
                          )} 元的 {gift.gift_name}
                        </F16Text>
                      </View>
                    </View>
                  ))}
                {order.gift_coupon && (
                  <View style={styles.gift_item}>
                    <Image
                      style={styles.gift_img}
                      source={require('../../images/icon-color-coupon.png')}
                    />
                    <View style={styles.gift_name}>
                      <F16Text>
                        {' '}
                        价值 ￥
                        {Foundation.formatPrice(order.gift_coupon.amount)}{' '}
                        的优惠券{' '}
                      </F16Text>
                    </View>
                  </View>
                )}
              </View>
            )}
            <View style={styles.order_other}>
              <TextItem title="订单状态" content={order.order_status_text} />
              <TextItem
                title="下单时间"
                content={Foundation.unixToDate(order.create_time)}
              />
              <ItemLine />
              <TextItem
                title="支付方式"
                content={
                  order.payment_type === 'ONLINE' ? '在线支付' : '货到付款'
                }
              />
              <TextItem
                title="支付状态"
                content={order.pay_status_text || '无'}
              />
              <ItemLine />
              <TextItem
                title="配送方式"
                content={order.ship_status_text || '无'}
              />
              <TextItem title="配送时间" content={order.receive_time} />
              <ItemLine />
              {order.remark ? (
                <View>
                  <TextItem title="订单备注" content={order.remark} />
                  <ItemLine />
                </View>
              ) : (
                undefined
              )}
              {order.receipt_history ? (
                <View>
                  <TextItem
                    title="发票类型"
                    content={this._receiptType(
                      order.receipt_history.receipt_type,
                    )}
                  />
                  <TextItem
                    title="发票抬头"
                    content={order.receipt_history.receipt_title}
                  />
                  <TextItem
                    title="发票内容"
                    content={order.receipt_history.receipt_content}
                  />
                  {order.receipt_history.receipt_title !== '个人' && (
                    <TextItem
                      title="发票税号"
                      content={order.receipt_history.tax_no}
                    />
                  )}
                </View>
              ) : (
                <TextItem title="发票类型" content="无需发票" />
              )}
            </View>
            <View style={styles.order_price}>
              <PriceItem
                title="商品总价"
                content={`￥${Foundation.formatPrice(order.goods_price)}`}
              />
              {!!order.cash_back && (
                <PriceItem
                  title="返现金额"
                  content={`-￥${Foundation.formatPrice(order.cash_back)}`}
                />
              )}
              <PriceItem
                title="运费"
                content={`+￥${Foundation.formatPrice(order.shipping_price)}`}
              />
              {order.coupon_price > 0 ? (
                <PriceItem
                  title="优惠券抵扣"
                  content={`-￥${Foundation.formatPrice(order.coupon_price)}`}
                />
              ) : (
                undefined
              )}
              {order.use_point ? (
                <PriceItem
                  title="积分抵扣"
                  content={`-${order.use_point}积分`}
                />
              ) : (
                undefined
              )}

              <ItemLine />
              <View style={styles.pay_money}>
                <F16Text>
                  {`${order.pay_status === 'PAY_NO' ? '应' : '实'}  付  款  ：`}
                  <F16Text style={{color: colors.main}}>
                    {`￥${Foundation.formatPrice(order.need_pay_money)}`}
                  </F16Text>
                </F16Text>
                {order.balance ? (
                  <F16Text>
                    预存款支付：
                    <F16Text style={{color: colors.main}}>
                      {`￥${Foundation.formatPrice(order.balance)}`}
                    </F16Text>
                  </F16Text>
                ) : (
                  undefined
                )}
              </View>
              {order.order_type === 'PINTUAN' ? (
                <View style={styles.pintuan}>
                  <TextLabel
                    style={styles.pintuan_btn}
                    text="查看拼团详情"
                    onPress={() => navigate('Assemble', {order})}
                  />
                </View>
              ) : (
                undefined
              )}
            </View>
          </ScrollView>
        </View>
        <Loading show={loading} />
      </View>
    );
  }
}

const GoodsItem = ({data, order_sn, refreshDetail, orderDetail}) => {
  const _toApplySale = () => {
    navigate('AfterSale', {
      order_sn,
      sku_id: data.sku_id,
      callback: refreshDetail,
    });
  };

  const _toApplyComplaint = () => {
    navigate('ApplyComplaint', {
      order_sn,
      sku_id: data.sku_id,
      callback: refreshDetail,
    });
  };
  let specs = [];
  const {
    allow_apply_service,
    allow_order_complain,
  } = data.goods_operate_allowable_vo;
  data.spec_list &&
    data.spec_list.forEach(item => {
      specs.push(`${item.spec_name}: ${item.spec_value}`);
    });
  return (
    <TouchableOpacity
      style={styles.goods_item}
      activeOpacity={1}
      onPress={() => navigate('Goods', {id: data.goods_id})}>
      <View style={styles.goods_image_c}>
        <Image
          style={styles.goods_item_image}
          source={{uri: data.goods_image}}
        />
        {orderDetail.order_type === 'PINTUAN' ? (
          <Text style={styles.goods_image_text}>多人拼团</Text>
        ) : (
          undefined
        )}
      </View>
      <View style={styles.goods_item_info}>
        <F16Text numberOfLines={1}>{data.name}</F16Text>
        {data.promotion_tags &&
          data.promotion_tags.map((item, index) => (
            <View key={index} style={styles.goods_promotion_tags}>
              <F16Text style={{color: colors.main, fontSize: 12}}>
                {item}
              </F16Text>
            </View>
          ))}
        <F14Text numberOfLines={2}>
          <F14Text style={styles.goods_item_num}>{specs.join(';')} </F14Text>
          <F14Text style={styles.goods_item_num}>数量：{data.num}</F14Text>
        </F14Text>
        <View style={styles.goods_item_other_view}>
          <F16Text>￥{Foundation.formatPrice(data.purchase_price)}</F16Text>
          {allow_apply_service ? (
            <TextLabel
              text="申请售后"
              tintColor={colors.main}
              onPress={_toApplySale}
            />
          ) : (
            undefined
          )}
          {((orderDetail.order_status_text !== '待付款' &&
            data.goods_operate_allowable_vo.allow_order_complain &&
            data.complain_status === 'NO_APPLY') ||
            data.complain_status === 'COMPLETE') && (
            <TextLabel
              text="交易投诉"
              tintColor={colors.main}
              onPress={_toApplyComplaint}
            />
          )}
        </View>
      </View>
    </TouchableOpacity>
  );
};

const PriceItem = ({title, content}) => {
  return (
    <View style={[styles.item_label, {justifyContent: 'space-between'}]}>
      <F16Text>{title}</F16Text>
      <F16Text>{content}</F16Text>
    </View>
  );
};

const ItemLine = () => <View style={styles.item_line} />;

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  body: {
    flex: 1,
  },
  trade_status: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    width: Screen.width,
    height: 40,
    backgroundColor: '#FFFFFF',
    paddingHorizontal: 10,
    marginBottom: 10,
  },
  order_sn: {
    width: Screen.width,
    height: 40,
    justifyContent: 'center',
    backgroundColor: '#FFFFFF',
    paddingHorizontal: 10,
  },
  order_address: {
    flexDirection: 'row',
    width: Screen.width,
    minHeight: 65,
    marginTop: 10,
    paddingHorizontal: 10,
    paddingVertical: 10,
    backgroundColor: '#FFFFFF',
  },
  order_address_icon: {
    alignItems: 'center',
    width: 20,
    minHeight: 65,
  },
  order_address_content: {
    width: Screen.width - 20 - 20,
    minHeight: 65,
  },
  order_address_info: {
    flexDirection: 'row',
    height: 20,
  },
  order_address_add: {
    marginTop: 7,
  },
  order_items: {
    marginTop: 10,
    backgroundColor: '#FFFFFF',
    paddingVertical: 10,
  },
  goods_item: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 10,
    paddingVertical: 10,
    width: Screen.width,
    backgroundColor: '#FFFFFF',
  },
  goods_item_image: {
    width: 70,
    height: 70,
    borderColor: colors.cell_line_backgroud,
    borderWidth: Screen.onePixel,
  },
  goods_image_c: {
    flexDirection: 'column',
    height: 70,
  },
  goods_image: {
    width: 70,
    height: 70,
    borderColor: '#FFFFFF',
    borderWidth: 2,
    borderRadius: 2,
    marginRight: 15,
  },
  goods_image_text: {
    backgroundColor: colors.main,
    color: '#FFFFFF',
    width: 70,
    zIndex: 10,
    textAlign: 'center',
    position: 'absolute',
    bottom: 0,
  },
  goods_item_info: {
    justifyContent: 'space-between',
    width: Screen.width - 20 - 70 - 10,
  },
  goods_item_num: {
    marginTop: 5,
    color: '#a5a5a5',
  },
  goods_item_line: {
    width: Screen.width - 10,
    height: Screen.onePixel,
    marginLeft: 10,
    backgroundColor: colors.gray_background,
  },
  goods_item_other_view: {
    justifyContent: 'space-between',
    flexDirection: 'row',
    alignItems: 'center',
  },
  goods_promotion_tags: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    width: 80,
    borderRadius: 2,
    borderWidth: 1,
    borderColor: colors.main,
  },
  item_line: {
    width: Screen.width,
    height: Screen.onePixel,
    backgroundColor: colors.gray_background,
  },
  order_other: {
    marginTop: 10,
    backgroundColor: '#FFFFFF',
    paddingVertical: 5,
  },
  item_label: {
    flexDirection: 'row',
    paddingHorizontal: 10,
    paddingVertical: 10,
    backgroundColor: '#FFFFFF',
  },
  item_label_text: {
    color: '#727272',
  },
  gift_item: {
    flexDirection: 'row',
    alignItems: 'center',
    marginLeft: 10,
    marginVertical: 10,
  },
  gift_item_title: {
    flexDirection: 'row',
    alignItems: 'center',
    marginLeft: 10,
  },
  shop_item: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginHorizontal: 10,
    marginBottom: 10,
  },
  shop_item_title: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  gift_img: {
    width: 60,
    height: 60,
  },
  gift_title_img: {
    width: 18,
    height: 18,
  },
  gift_title_text: {
    marginLeft: 5,
  },
  gift_name: {
    width: Screen.width - 120,
    marginLeft: 10,
  },
  order_price: {
    marginTop: 10,
    backgroundColor: '#FFFFFF',
    paddingBottom: 30,
  },
  pay_money: {
    alignItems: 'flex-end',
    paddingHorizontal: 10,
    paddingVertical: 15,
  },
  footer: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    alignItems: 'center',
    width: Screen.width,
    height: 50,
    backgroundColor: colors.gray_background,
  },
  footer_btn: {
    justifyContent: 'center',
    alignItems: 'center',
    borderColor: '#232327',
    borderWidth: Screen.onePixel,
    borderRadius: 2,
    backgroundColor: '#FFFFFF',
  },
  footer_text: {
    color: '#232327',
  },
  footer_btn_topay: {
    backgroundColor: colors.main,
    borderColor: colors.main,
  },
  pintuan: {
    alignItems: 'flex-end',
    paddingHorizontal: 10,
    paddingVertical: 15,
  },
  pintuan_btn: {
    paddingVertical: 2,
    marginRight: 0,
    marginLeft: 10,
    marginBottom: 0,
    width: 120,
  },
  tip_view: {
    backgroundColor: '#fff7cc',
    height: 30,
    marginBottom: 2,
    justifyContent: 'center',
    alignItems: 'center',
  },
  tip_view_text: {
    color: '#ff621d',
    fontSize: 13,
  },
});
