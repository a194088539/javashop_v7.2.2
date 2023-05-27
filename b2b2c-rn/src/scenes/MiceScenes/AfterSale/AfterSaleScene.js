import React, {Component} from 'react';
import {Image, StyleSheet, TouchableOpacity, View, Text} from 'react-native';
import {connect} from 'react-redux';
import {navigate} from '../../../navigator/NavigationService';
import {colors} from '../../../../config';
import {Foundation, Screen} from '../../../utils';
import {DashLine} from '../../../widgets';
import * as API_AfterSale from '../../../apis/after-sale';
import Icon from 'react-native-vector-icons/Ionicons';

class AfterSaleScene extends Component {
  static navigationOptions = {
    title: '申请售后',
  };

  constructor(props, context) {
    super(props, context);
    const {params} = this.props.navigation.state;
    this.sn = params.order_sn;
    this.sku_id = params.sku_id;
    this.state = {
      applyInfo: {},
    };
  }

  componentDidMount() {
    this._getAfterSaleData();
  }

  _getAfterSaleData = async () => {
    const applyInfo = await API_AfterSale.getAfterSaleData(
      this.sn,
      this.sku_id,
    );
    this.setState({applyInfo});
  };

  render() {
    const {sn, sku_id} = this;
    const {applyInfo} = this.state;
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
        </View>
        <View style={styles.type_view}>
          {applyInfo.allow_return_goods && (
            <View>
              <TypeItem
                icon={require('../../../images/icon-tuihuo.png')}
                title="退货"
                intro="退回收到的商品"
                onPress={() =>
                  navigate('ApplyAfterSale', {
                    order_sn: sn,
                    sku_id: sku_id,
                    service_type: 'RETURN_GOODS',
                  })
                }
              />
              <DashLine />
            </View>
          )}
          <TypeItem
            icon={require('../../../images/icon-huanhuo.png')}
            title="换货"
            intro="更换收到的商品"
            onPress={() =>
              navigate('ApplyAfterSale', {
                order_sn: sn,
                sku_id: sku_id,
                service_type: 'CHANGE_GOODS',
              })
            }
          />
          <DashLine />
          <TypeItem
            icon={require('../../../images/icon-bufa.png')}
            title="补发商品"
            intro="商家少发商品"
            onPress={() =>
              navigate('ApplyAfterSale', {
                order_sn: sn,
                sku_id: sku_id,
                service_type: 'SUPPLY_AGAIN_GOODS',
              })
            }
          />
        </View>
      </View>
    );
  }
}

const TypeItem = ({icon, title, intro, onPress}) => {
  return (
    <TouchableOpacity style={styles.type_item} onPress={onPress}>
      <View style={styles.type_item_left}>
        <Image style={styles.type_item_image} source={icon} />
        <Text style={styles.type_item_title}>{title}</Text>
      </View>
      <View style={styles.type_item_right}>
        <Text>{intro}</Text>
        <Icon name="ios-arrow-forward" color="#A8A9AB" size={20} />
      </View>
    </TouchableOpacity>
  );
};

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

const styles = StyleSheet.create({
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
    height: 100,
    width: Screen.width,
  },
  goods_item: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 10,
    width: Screen.width,
    height: 100,
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
  goods_item_view: {
    flexDirection: 'row',
  },
  type_view: {
    marginHorizontal: 10,
    marginVertical: 10,
    borderRadius: 13,
    backgroundColor: '#fff',
    height: 200,
  },
  type_item: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginHorizontal: 10,
    marginVertical: 15,
  },
  type_item_left: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    width: 80,
  },
  type_item_right: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    width: 120,
    marginRight: 10,
  },
  type_item_image: {
    width: 38,
    height: 35,
  },
  type_item_title: {
    fontSize: 16,
    color: '#000',
    marginLeft: 8,
  },
});

export default connect()(AfterSaleScene);
