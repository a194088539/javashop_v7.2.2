/**
 * Created by Andste on 2018/11/6.
 */
import React from 'react';
import {View, Image, TouchableOpacity, StyleSheet, Text} from 'react-native';
import {navigate} from '../../navigator/NavigationService';
import {colors} from '../../../config';
import {Screen, Foundation} from '../../utils';
import {F16Text} from '../../widgets/Text';
import {TextLabel} from '../../widgets';
import Icon from 'react-native-vector-icons/Ionicons';

export default function({data, onRefresh, confirmShip}) {
  const {sku_list} = data;
  let countNum = 0;
  const {
    allow_apply_service,
    allow_cancel,
    allow_comment,
    allow_pay,
    allow_rog,
    allow_service_cancel,
  } = data.order_operate_allowable_vo;
  return (
    <TouchableOpacity
      activeOpacity={1}
      onPress={() => navigate('OrderDetail', {data, callback: onRefresh})}
      style={styles.container}>
      <View style={styles.header}>
        <F16Text>订单号：{data.sn}</F16Text>
        <F16Text style={styles.order_status}>
          {data.order_type === 'PINTUAN' ? data.ping_tuan_status : undefined}
        </F16Text>
      </View>
      <View style={styles.order_item_info}>
        <View style={styles.order_item_info_left}>
          <Text>
            状&nbsp;&nbsp;&nbsp;&nbsp;态：
            <Text style={{color: colors.tag}}>{data.order_status_text}</Text>
          </Text>
          <Text>
            总&nbsp;&nbsp;&nbsp;&nbsp;价：
            <Text style={{color: colors.main}}>
              ￥{Foundation.formatPrice(data.order_amount)}
            </Text>
          </Text>
        </View>
        <View style={styles.order_item_info_right}>
          {!allow_apply_service &&
          !allow_cancel &&
          !allow_comment &&
          !allow_pay &&
          !allow_rog &&
          !allow_service_cancel ? (
            <TextLabel
              style={styles.footer_btn}
              text="查看详情"
              onPress={() =>
                navigate('OrderDetail', {data, callback: onRefresh})
              }
            />
          ) : (
            undefined
          )}
          {allow_apply_service ? (
            <TextLabel
              style={styles.footer_btn}
              text="申请售后"
              onPress={() =>
                navigate('ApplyAfterSale', {data, callback: onRefresh})
              }
            />
          ) : (
            undefined
          )}
          {allow_service_cancel ? (
            <TextLabel
              style={styles.footer_btn}
              text="取消订单"
              onPress={() =>
                navigate('ApplyCancle', {
                  order_sn: data.sn,
                  callback: onRefresh,
                })
              }
            />
          ) : (
            undefined
          )}
          {allow_cancel ? (
            <TextLabel
              style={styles.footer_btn}
              text="取消订单"
              onPress={() =>
                navigate('CancelOrder', {data, callback: onRefresh})
              }
            />
          ) : (
            undefined
          )}
          {allow_pay ? (
            <TextLabel
              style={[styles.footer_btn, styles.footer_btn_topay]}
              textStyle={{color: '#FFFFFF'}}
              text="去支付"
              onPress={() =>
                navigate('Cashier', {order_sn: data.sn, callback: onRefresh})
              }
            />
          ) : (
            undefined
          )}
          {allow_rog ? (
            <TextLabel
              style={[styles.footer_btn, styles.footer_btn_topay]}
              textStyle={{color: '#FFFFFF'}}
              text="确认收货"
              onPress={() => confirmShip(data)}
            />
          ) : (
            undefined
          )}
          {allow_comment ? (
            <TextLabel
              style={styles.footer_btn}
              text="去评价"
              onPress={() =>
                navigate('CommentOrder', {data, callback: onRefresh})
              }
            />
          ) : (
            undefined
          )}
          {data.comment_status === 'WAIT_CHASE' ? (
            <TextLabel
              style={styles.footer_btn}
              text="追加评价"
              onPress={() =>
                navigate('CommentOrder', {
                  data,
                  append_comment: true,
                  callback: onRefresh,
                })
              }
            />
          ) : (
            undefined
          )}
        </View>
      </View>
      <View style={styles.store}>
        <TouchableOpacity
          style={[styles.shop_item]}
          onPress={() => {
            navigate('Shop', {id: data.seller_id});
          }}>
          <View style={styles.shop_item_title}>
            <Image
              style={styles.gift_title_img}
              source={require('../../images/icon-shop_comment.png')}
            />
            <F16Text style={styles.gift_title_text}>{data.seller_name}</F16Text>
          </View>
          <Icon name="ios-arrow-forward" color="#A8A9AB" size={20} />
        </TouchableOpacity>
      </View>
      <View style={styles.body}>
        <View style={styles.b_content_view}>
          {sku_list.map((goods, index) => {
            countNum += goods.num;
            if (index > 4) {
              return;
            }
            if (sku_list.length === 1) {
              const specs = goods.spec_list;
              return (
                <View key={index} style={styles.is_once}>
                  <View style={styles.goods_image_c}>
                    <Image
                      style={styles.goods_image}
                      source={{uri: goods.goods_image}}
                    />
                    {data.order_type === 'PINTUAN' ? (
                      <Text style={styles.goods_image_text}>多人拼团</Text>
                    ) : (
                      undefined
                    )}
                  </View>

                  <View style={styles.goods_name_title}>
                    <F16Text style={styles.goods_name} numberOfLines={3}>
                      {goods.name}
                    </F16Text>
                    <View key={index} style={styles.spec_view}>
                      {specs
                        ? specs.map((spec, spec_index) => {
                            let isFlat = spec_index != specs.length - 1;
                            return (
                              <Text
                                key={spec_index}
                                style={styles.spec_name}
                                numberOfLines={1}>
                                {spec.spec_value}
                                {isFlat ? ' - ' : ''}
                              </Text>
                            );
                          })
                        : undefined}
                    </View>
                  </View>
                </View>
              );
            }
            return (
              <Image
                key={index}
                style={styles.goods_image}
                source={{uri: goods.goods_image}}
              />
            );
          })}
        </View>
      </View>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'space-between',
    alignItems: 'center',
    height: 240,
    backgroundColor: '#FFFFFF',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    width: Screen.width,
    height: 40,
    paddingHorizontal: 15,
    borderBottomColor: colors.cell_line_backgroud,
    borderBottomWidth: Screen.onePixel,
  },
  order_item_info: {
    flexDirection: 'row',
    width: Screen.width,
    borderBottomColor: colors.cell_line_backgroud,
    borderBottomWidth: Screen.onePixel,
    padding: 10,
  },
  order_item_info_left: {
    flexDirection: 'column',
    width: Screen.width / 3,
    paddingHorizontal: 10,
  },
  order_item_info_right: {
    marginTop: 5,
    flexDirection: 'row',
    justifyContent: 'flex-end',
    width: (Screen.width / 3) * 2,
    paddingHorizontal: 25,
  },
  store: {
    marginTop: 5,
    backgroundColor: '#FFFFFF',
    paddingVertical: 10,
    width: Screen.width,
    borderBottomColor: colors.cell_line_backgroud,
    borderBottomWidth: Screen.onePixel,
  },
  shop_item: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginHorizontal: 10,
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
  order_status: {
    color: colors.main,
  },
  body: {
    width: Screen.width,
    height: 100,
  },
  b_content_view: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 15,
    paddingVertical: (205 - 120 - 60) / 2,
    backgroundColor: '#FFFFFF',
    height: 205 - 120,
  },
  goods_image_c: {
    flexDirection: 'column',
    height: 70,
  },
  goods_image: {
    width: 60,
    height: 60,
    borderColor: '#FFFFFF',
    borderWidth: 2,
    borderRadius: 2,
    marginRight: 15,
  },
  goods_image_text: {
    backgroundColor: colors.main,
    color: '#FFFFFF',
    width: 58,
    zIndex: 10,
    textAlign: 'center',
    position: 'absolute',
    bottom: 0,
  },
  is_once: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    width: Screen.width - 30,
    height: 'auto',
  },
  goods_name: {
    width: Screen.width - 30 - 60 - 15,
    lineHeight: 18,
  },
  goods_name_title: {
    flexDirection: 'column',
  },
  b_price_view: {
    justifyContent: 'center',
    alignItems: 'flex-end',
    height: 40,
    paddingHorizontal: 15,
    borderColor: colors.cell_line_backgroud,
    borderBottomWidth: Screen.onePixel,
  },
  footer: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    alignItems: 'center',
    width: Screen.width,
    height: 40,
    paddingHorizontal: 15,
  },
  footer_btn: {
    paddingVertical: 2,
    marginRight: 0,
    marginLeft: 10,
    marginBottom: 0,
  },
  footer_btn_topay: {
    backgroundColor: colors.main,
    borderColor: colors.main,
  },
  spec_view: {
    flexDirection: 'row',
    marginTop: 5,
  },
  spec_name: {
    color: colors.sku_spec,
  },
});
