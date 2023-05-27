/**
 * Created by lylyulei on 2020-04-29 11:02.
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
  ImageBackground,
} from 'react-native';
import {navigate} from '../../navigator/NavigationService';
import {Foundation, Screen} from '../../utils';
import {appId, colors} from '../../../config';
import * as API_Order from '../../apis/order';
import {BigButton, Loading} from '../../widgets';
import CountDown from '../Goods/CountDown';
import {ShareActionSheet} from '../../components';
import {web_domain} from '../../../config/api';

const default_bg_01 = require('../../images/background-pt-share_01.png');
const default_bg_02 = require('../../images/background-pt-share_02.png');
const default_bg_03 = require('../../images/background-pt-share_03.png');
const default_face = require('../../images/icon-no-face.png');
export default class AssembleScene extends Component {
  static navigationOptions = {
    title: '我的订单',
  };

  constructor(props, context) {
    super(props, context);
    const {params} = this.props.navigation.state;
    this.sn = params.order.sn;
    this.isPay = params.order.pay_status === 'PAY_YES';
    this.isFormed = false;
    //默认为未支付背景图
    this.default_bg = this.isPay ? default_bg_02 : default_bg_01;
    this.state = {
      order: '',
      loading: false,
      default_bg: this.default_bg,
    };
  }

  async componentDidMount() {
    this._getOrderData();
  }

  _getOrderData = async () => {
    this.setState({loading: true});
    try {
      const order = await API_Order.getPinTuanOrder(this.sn);
      this.isFormed = order.order_status === 'formed';
      this.default_bg = this.isFormed ? default_bg_03 : this.default_bg;
      await this.setState({loading: false, order, default_bg: this.default_bg});
    } catch (error) {
      this.setState({loading: false});
      navigate('MyOrder');
    }
  };

  /**
   * 显示分享
   * @private
   */
  _onShowShare = () => {
    this.shareActionSheet.open();
  };
  _onPay = () => {
    navigate('Cashier', {order_sn: this.sn});
  };

  _onHome = () => {
    navigate('Home');
  };

  render() {
    const {order, loading, default_bg} = this.state;
    const {shareConfig} = appId;
    const shareData = {
      title: '这个商品降价啦，快来参与拼团啊! 👉',
      description: shareConfig.description || order.goods_name,
      imageUrl: shareConfig.imageUrl || order.thumbnail,
      webpageUrl: `${web_domain}/member/my-order/assemble?order_sn=${
        this.sn
      }&goods_id=${order.goods_id}&sku_id=${order.sku_id}&fromnav=assemble`,
    };
    console.log(shareData);
    console.log(order);
    return (
      <View style={styles.container}>
        <GoodsItem data={order} />
        <View style={styles.pintuanContainer}>
          <View style={styles.pintuan}>
            <Image style={styles.pintuan_background} source={default_bg} />
            {this.isPay ? (
              <View style={styles.pintuan_content}>
                <Text style={styles.pintuan_num}>
                  {this.isFormed
                    ? '已成团'
                    : '仅剩' +
                      (order.required_num - order.offered_num) +
                      '个名额'}
                </Text>
                <View style={styles.pintuan_face}>
                  {order.participants &&
                    order.participants.map((part, index) => {
                      if (!part.face || part.face === 'null') {
                        return (
                          <View style={styles.faceContainer}>
                            <Image
                              style={styles.face_image}
                              source={default_face}
                            />
                            {part.is_master === 1 ? (
                              <Text style={styles.faceText}>团长</Text>
                            ) : (
                              undefined
                            )}
                          </View>
                        );
                      } else {
                        return (
                          <View style={styles.faceContainer}>
                            <Image
                              style={styles.face_image}
                              source={{uri: part.face}}
                            />
                            {part.is_master === 1 ? (
                              <Text style={styles.faceText}>团长</Text>
                            ) : (
                              undefined
                            )}
                          </View>
                        );
                      }
                    })}
                </View>
                {!this.isFormed && order.left_time ? (
                  <View style={styles.pro_time}>
                    <Text style={styles.pro_time_tit}>剩余</Text>
                    <CountDown endTime={order.left_time} />
                    <Text style={styles.pro_time_tit}>结束</Text>
                  </View>
                ) : (
                  undefined
                )}
              </View>
            ) : (
              undefined
            )}
          </View>
          {this.isFormed ? (
            <View style={styles.pintuanBtn}>
              <BigButton
                title="去逛逛"
                style={styles.shareBtn}
                onPress={this._onHome}
              />
            </View>
          ) : this.isPay ? (
            <View style={styles.pintuanBtn}>
              <BigButton
                title="邀请好友"
                style={styles.shareBtn}
                onPress={this._onShowShare}
              />
            </View>
          ) : (
            <View style={styles.pintuanBtn}>
              <BigButton
                title="去支付"
                style={styles.shareBtn}
                onPress={this._onPay}
              />
            </View>
          )}
          <Loading show={loading} />
        </View>
        <ShareActionSheet
          ref={_ref => (this.shareActionSheet = _ref)}
          data={shareData}
        />
      </View>
    );
  }
}

const GoodsItem = ({data}) => {
  return (
    <TouchableOpacity
      style={styles.goods_item}
      activeOpacity={1}
      onPress={() => navigate('Goods', {id: data.goods_id})}>
      <Image style={styles.goods_item_image} source={{uri: data.thumbnail}} />
      <View style={styles.goods_item_info}>
        <Text>{data.goods_name}</Text>
        <View style={styles.goods_item_view}>
          <Text>
            {data.required_num}人团 ⋅ 已有{data.offered_num}人参团
          </Text>
          <Text>
            <Text style={styles.pintuan_price}>
              ￥{Foundation.formatPrice(data.origin_price)}
            </Text>
            {'   '}拼团省￥
            {data.origin_price - data.sales_price}
          </Text>
        </View>
      </View>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  goods_view: {
    height: 100,
    width: Screen.width,
  },
  goods_item: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 10,
    width: Screen.width,
    minHeight: 150,
    backgroundColor: '#FFFFFF',
    borderBottomColor: colors.border_line,
    borderBottomWidth: 1,
  },
  goods_item_image: {
    width: 70,
    height: 70,
    borderColor: colors.cell_line_backgroud,
    borderWidth: Screen.onePixel,
  },
  goods_item_info: {
    width: Screen.width - 20 - 70 - 10,
    height: 80,
  },
  goods_item_view: {
    flexDirection: 'column',
    marginTop: 10,
  },
  pintuan_price: {
    color: colors.main,
    fontSize: 18,
  },
  pintuanContainer: {
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    width: Screen.width,
    backgroundColor: colors.tab_background,
  },
  pintuan_background: {
    width: Screen.width,
    height: 90,
    marginBottom: 15,
  },
  pintuan: {
    alignItems: 'center',
    justifyContent: 'center',
  },
  pintuan_content: {
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'space-between',
    width: Screen.width / 2,
  },
  pintuan_num: {
    backgroundColor: '#f7297c',
    borderRadius: 20,
    color: '#FFFFFF',
    paddingHorizontal: 10,
  },
  pintuan_face: {
    marginTop: 20,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    width: Screen.width,
  },
  faceContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    width: 75,
    height: 65,
  },
  face_image: {
    justifyContent: 'center',
    alignItems: 'center',
    width: 65,
    height: 65,
    borderRadius: 100,
  },
  faceText: {
    backgroundColor: '#fcc52d',
    height: 20,
    paddingHorizontal: 5,
    borderRadius: 15,
    marginLeft: -15,
    marginTop: -5,
  },
  pro_time: {
    marginTop: 20,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  pro_time_tit: {
    color: '#666',
    paddingHorizontal: 5,
  },
  pintuanBtn: {
    alignItems: 'flex-end',
    justifyContent: 'center',
    height: 20,
    marginTop: 30,
  },
  shareBtn: {
    width: (Screen.width / 10) * 9,
    borderRadius: 10,
    backgroundColor: '#f7297c',
  },
});
