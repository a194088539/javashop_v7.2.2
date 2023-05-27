import React, {Component} from 'react';
import {
  View,
  StyleSheet,
  StatusBar,
  TouchableOpacity,
  Image,
  Text,
  DeviceEventEmitter,
} from 'react-native';
import * as API_Order from '../../../apis/order';
import * as API_AfterSale from '../../../apis/after-sale';
import {FlatList} from '../../../components';
import {colors} from '../../../../config';
import {Screen} from '../../../utils';
import {navigate} from '../../../navigator/NavigationService';
import {TextLabel, DashLine, GoodsToolBar} from '../../../widgets';
import Icon from 'react-native-vector-icons/Ionicons';
import ScrollableTabView, {
  DefaultTabBar,
} from 'react-native-scrollable-tab-view';

export default class MyAfterSale extends Component {
  static navigationOptions = {
    title: '售后管理',
  };

  constructor(props) {
    super(props);
    this.params = {
      page_no: 1,
      page_size: 10,
    };
    this.state = {
      loading: false,
      refundList: [],
      orderList: [],
      refundNoData: false,
      orderNoData: false,
    };
  }

  componentDidMount() {
    this._onRefreshOrderList();
    this.refreshSubScription = DeviceEventEmitter.addListener(
      'refresh',
      message => {
        if (message) {
          this._getOrderList();
        }
      },
    );
  }
  componentWillUnmount() {
    this.refreshSubScription.remove();
  }

  _getOrderList = async () => {
    const {page_no} = this.params;
    const {orderList} = this.state;
    try {
      const res = (await API_Order.getOrderList(this.params)) || {};
      const {data = []} = res;
      this.setState({
        loading: false,
        orderNoData: !data[0],
        orderList: page_no === 1 ? data : orderList.concat(data),
      });
    } catch (error) {
      this.setState({
        loading: false,
      });
    }
  };

  /**
   * 获取订单列表数据
   * @returns {Promise<void>}
   * @private
   */
  _getRefoundData = async () => {
    const {page_no} = this.params;
    const {refundList} = this.state;
    try {
      const res = (await API_AfterSale.getAfterSaleList(this.params)) || {};
      const {data = []} = res;
      this.setState({
        loading: false,
        refundNoData: !data[0],
        refundList: page_no === 1 ? data : refundList.concat(data),
      });
    } catch (error) {
      this.setState({
        loading: false,
      });
    }
  };

  /**
   * 刷新列表
   * @returns {Promise<void>}
   * @private
   */
  _onRefresh = async () => {
    this.params.page_no = 1;
    this.setState({loading: true});
    this._getRefoundData();
  };

  /**
   * 刷新列表
   * @returns {Promise<void>}
   * @private
   */
  _onRefreshOrderList = async () => {
    this.params.page_no = 1;
    this.setState({loading: true});
    this._getOrderList();
  };

  /**
   * 列表滚动到底部触发
   * @private
   */
  _onEndReached = () => {
    let {loading, refundNoData, refundList} = this.state;
    if (loading || refundNoData || !refundList[9]) {
      return;
    }
    this.params.page_no++;
    this._getRefoundData();
  };

  /**
   * 列表滚动到底部触发
   * @private
   */
  _onOrderListEndReached = () => {
    let {loading, orderNoData, orderList} = this.state;
    if (loading || orderNoData || !orderList[9]) {
      return;
    }
    this.params.page_no++;
    this._getOrderList();
  };

  _getItemLayout = (data, index) => ({length: 205, offset: 205 * index, index});

  _itemSeparatorComponent = () => <View style={styles.separator} />;

  _renderTabBar = () => (
    <DefaultTabBar style={styles.tabBar} tabStyle={styles.tab_style} />
  );

  _changeTab = tab => {
    if (tab.i === 0) {
      this._onRefreshOrderList();
    } else if (tab.i === 1) {
      this._onRefresh();
    }
  };

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

  render() {
    let initialPage = 0;
    const {refundList, orderList, loading} = this.state;
    return (
      <View style={styles.container}>
        <StatusBar barStyle="dark-content" />
        <ScrollableTabView
          initialPage={initialPage}
          tabBarInactiveTextColor={colors.text}
          tabBarActiveTextColor={colors.main}
          tabBarBackgroundColor="#FFFFFF"
          renderTabBar={this._renderTabBar}
          tabBarUnderlineStyle={styles.tabBarUnderlineStyle}
          onChangeTab={this._changeTab}
          contentProps={{bounces: false}}>
          <RefundOrderList
            tabLabel="申请售后"
            dataList={orderList}
            loading={loading}
            ItemSeparatorComponent={this._itemSeparatorComponent}
            getItemLayout={this._getItemLayout}
            onRefresh={this._onRefreshOrderList}
            onEndReached={this._onOrderListEndReached}
            statusFilter={this.statusFilter}
          />
          <RefundOrderList
            tabLabel="申请记录"
            dataList={refundList}
            loading={loading}
            ItemSeparatorComponent={this._itemSeparatorComponent}
            getItemLayout={this._getItemLayout}
            onRefresh={this._onRefresh}
            onEndReached={this._onEndReached}
            statusFilter={this.statusFilter}
          />
        </ScrollableTabView>
      </View>
    );
  }
}

const OrdersItem = ({data}) => {
  const {item} = data;
  const {sku_list} = item;
  return (
    <View style={styles.refund_order_item_view}>
      <TouchableOpacity
        style={[styles.shop_item]}
        onPress={() => {
          navigate('Shop', {id: item.seller_id});
        }}>
        <View style={styles.shop_item_title}>
          <Image
            style={styles.gift_title_img}
            source={require('../../../images/icon-shop_comment.png')}
          />
          <Text style={styles.gift_title_text}>{item.seller_name}</Text>
        </View>
        <Icon name="ios-arrow-forward" color="#A8A9AB" size={20} />
      </TouchableOpacity>
      <DashLine />
      {sku_list &&
        sku_list.map((goods, _index) => (
          <GoodsToolBar
            key={'orders_' + _index}
            goods_img={goods.goods_image}
            goods_name={goods.name}
            goods_id={goods.goods_id}
            goods_num={goods.num}
            btn_title={'申请售后'}
            routeName={'AfterSale'}
            params={{order_sn: item.sn, sku_id: goods.sku_id}}
            disable={goods.goods_operate_allowable_vo.allow_apply_service}
          />
        ))}
    </View>
  );
};

const RefundsItem = ({data, statusFilter}) => {
  const {item} = data;
  const {goods_list} = item;
  return (
    <TouchableOpacity
      style={styles.refund_order_item_view}
      onPress={() => {
        navigate('AfterSaleDetail', {sn: item.service_sn});
      }}>
      <View style={[styles.shop_item]}>
        <View style={styles.shop_item_title}>
          <Text style={styles.gift_title_text}>
            服务单号：{item.service_sn}
          </Text>
        </View>
        <View style={styles.shop_item_right}>
          {item.service_type === 'CHANGE_GOODS' && (
            <Image
              style={styles.gift_title_img}
              source={require('../../../images/icon-huanhuo.png')}
            />
          )}
          {item.service_type === 'SUPPLY_AGAIN_GOODS' && (
            <Image
              style={styles.gift_title_img}
              source={require('../../../images/icon-bufa.png')}
            />
          )}
          {(item.service_type === 'RETURN_GOODS' ||
            item.service_type === 'ORDER_CANCEL') && (
            <Image
              style={styles.gift_title_img}
              source={require('../../../images/icon-tuihuo.png')}
            />
          )}
          <Text>{item.service_type_text}</Text>
        </View>
      </View>
      <DashLine />
      {goods_list &&
        goods_list.map((goods, _index) => (
          <View key={'refunds' + _index}>
            <View style={styles.goods_item} activeOpacity={1}>
              <Image
                style={styles.goods_item_image}
                source={{uri: goods.goods_image}}
              />
              <View style={styles.goods_item_info}>
                <Text>{goods.goods_name}</Text>
                <View>
                  <Text style={styles.goods_item_num}>
                    数量：{goods.return_num}
                  </Text>
                </View>
              </View>
            </View>
            <View style={styles.refund_status_view}>
              <Text>{item.service_status_text}</Text>
              <View>
                {item.allowable.allow_ship && (
                  <Text>请您尽快将申请售后的商品退还给卖家</Text>
                )}
                {!item.allowable.allow_ship && (
                  <Text>{statusFilter(item.service_status)}</Text>
                )}
              </View>
              <Icon name="ios-arrow-forward" color="#A8A9AB" size={20} />
            </View>
          </View>
        ))}
    </TouchableOpacity>
  );
};

const RefundOrderList = ({
  tabLabel,
  dataList,
  loading,
  statusFilter,
  ...props
}) => {
  if (tabLabel === '申请售后') {
    return (
      <View>
        <FlatList
          data={dataList}
          renderItem={item => <OrdersItem data={item} />}
          ListHeaderComponent={<View style={{height: 10}} />}
          ListFooterBgColor="#FAFAFA"
          refreshing={loading}
          {...props}
        />
      </View>
    );
  } else {
    return (
      <View>
        <FlatList
          data={dataList}
          renderItem={item => (
            <RefundsItem data={item} statusFilter={statusFilter} />
          )}
          ListHeaderComponent={<View style={{height: 10}} />}
          ListFooterBgColor="#FAFAFA"
          refreshing={loading}
          {...props}
        />
      </View>
    );
  }
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#FAFAFA',
  },
  separator: {
    height: 10,
    backgroundColor: '#FAFAFA',
  },
  tabBar: {
    height: 40,
    borderColor: colors.cell_line_backgroud,
    backgroundColor: '#FFFFFF',
  },
  tab_style: {
    paddingBottom: 0,
  },
  tabBarUnderlineStyle: {
    backgroundColor: colors.main,
  },
  goods_item: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 10,
    width: Screen.width,
    height: 90,
  },
  goods_item_image: {
    width: 70,
    height: 70,
    borderColor: colors.cell_line_backgroud,
    borderWidth: Screen.onePixel,
    borderRadius: 8,
  },
  goods_item_info: {
    justifyContent: 'space-between',
    width: Screen.width - 20 - 70 - 10,
    height: 70,
  },
  shop_item: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    margin: 8,
  },
  shop_item_title: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  gift_title_img: {
    width: 18,
    height: 18,
  },
  gift_title_text: {
    marginLeft: 5,
  },
  goods_item_apply_view: {
    alignItems: 'flex-end',
  },
  goods_item_apply_btn: {
    width: 100,
    borderRadius: 20,
  },
  refund_order_item_view: {
    backgroundColor: '#FFFFFF',
    marginVertical: 1,
  },
  shop_item_right: {
    flexDirection: 'row',
    marginRight: 10,
  },
  refund_status_view: {
    flexDirection: 'row',
    backgroundColor: '#f6f6f6',
    borderColor: '#f6f6f6',
    justifyContent: 'space-between',
    alignItems: 'center',
    height: 54,
    margin: 15,
    padding: 10,
  },
});
