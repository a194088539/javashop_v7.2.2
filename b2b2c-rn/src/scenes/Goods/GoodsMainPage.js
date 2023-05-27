/**
 * 商品页主页
 * Created by Andste on 2018/11/21.
 */
import React, {PureComponent} from 'react';
import {View, DeviceEventEmitter, StyleSheet} from 'react-native';
import {connect} from 'react-redux';
import {messageActions} from '../../redux/actions';
import {colors} from '../../../config';
import {TextCell, Price} from '../../widgets';
import * as API_Goods from '../../apis/goods';
import * as API_Promotions from '../../apis/promotions';
import * as API_Shop from '../../apis/shop';
import * as API_Trade from '../../apis/trade';
import ItemCell from './GoodsItemCell';
import GoodsProBar from './GoodsProBar';
import GoodsCoupons from './GoodsCoupons';
import GoodsComments from './GoodsComments';
import GoodsPromotions from './GoodsPromotions';
import MainShop from './GoodsMainShop';
import GoodsSkus from './GoodsSkus';
import GoodsShip from './GoodsShip';
import GoodsAssembleOrder from './GoodsAssembleOrder';

class GoodsMainPage extends PureComponent {
  constructor(props) {
    super(props);
    this.skuMap = new Map();
    this.state = {
      // skus
      skus: [],
      // 规格列表
      specList: [],
      // 优惠券列表展示
      coupons: [],
      // 优惠券列表
      couponsList: [],
      // 促销信息展示
      promotions: [],
      // 促销活动列表
      promotionList: [],
      // 积分商品、团购活动、限时抢购【其一】
      proBars: '',
      // 积分商品、团购活动、限时抢购活动列表
      proBarsList: [],
      // 店铺信息
      shop: '',
      // 已选中的规格值ID
      selectedSpecIds: [],
      // 已选中的规格值
      selectedSpecVals: [],
      // 已选规格
      selectedSku: '',
      // 购买数量
      buyNum: '1',
      // 显示信息
      showInfo: {},
      //拼团sku列表
      pinTuanList: [],
      //拼团信息
      pinTuan: undefined,
      //拼团订单信息
      pinTuanOrder: [],
    };
  }

  async componentDidMount() {
    await this._getOtherData();
  }

  /**
   * 获取其它数据
   * sku数据
   * 优惠券数据
   * 促销信息
   * 店铺数据
   * @returns {Promise<void>}
   * @private
   */
  _getOtherData = async () => {
    const {goods_id, seller_id} = this.props.goods;
    const {pintuan_id, sku_id} = this.props;

    const req_arrays = [
      API_Goods.getGoodsSkus(goods_id),
      API_Promotions.getShopCoupons(seller_id),
      API_Promotions.getGoodsPromotions(goods_id),
      API_Shop.getShopBaseInfo(seller_id),
      API_Promotions.getCoupons(goods_id),
      pintuan_id
        ? API_Promotions.getPinTuanSkus(goods_id, pintuan_id)
        : undefined,
      pintuan_id ? API_Trade.pinTuanOrder(goods_id, sku_id) : undefined,
    ];
    const values = await Promise.all(req_arrays);
    // ⬇筛选出促销活动（非积分商品，非团购商品，非秒杀商品）
    const promotionList = values[2]
      .map(item => {
        if (item.exchange || item.groupbuy_goods_vo || item.seckill_goods_vo) {
          return null;
        }
        return item;
      })
      .filter(item => !!item);
    // ⬇筛选出【积分商品、团购活动、限时抢购】
    const proBars = [];
    const proBarsList = values[2]
      .map(item => {
        if (item.exchange || item.groupbuy_goods_vo || item.seckill_goods_vo) {
          return item;
        }
        return null;
      })
      .filter(item => !!item);
    this.setState({
      couponsList: values[4],
      promotionList,
      proBars,
      shop: values[3],
      proBarsList: proBarsList,
      pinTuanList: values[5],
      pinTuanOrder: values[6],
    });
    //处理SKU
    await this._makeSkus(values[0], proBars);
  };

  /**
   * 处理skus
   * @param skus
   * @param pors 积分、团购、限时抢购
   * @returns {Promise<void>}
   * @private
   */
  _makeSkus = async (skus, pors) => {
    const specList = [];
    const {proBarsList, promotionList, pinTuan, pinTuanList} = this.state;
    const sku_id = this.props.sku_id;
    //形成规格列表
    skus.forEach(sku => {
      const {spec_list} = sku;
      if (!spec_list) {
        this.skuMap.set('no_spec', sku);
        return;
      }
      const spec_value_ids = [];
      spec_list.forEach(spec => {
        const _specIndex = specList.findIndex(
          _spec => _spec.spec_id === spec.spec_id,
        );
        const _spec = {
          spec_id: spec.spec_id,
          spec_name: spec.spec_name,
          spec_type: spec.spec_type,
        };
        const _value = {
          spec_value: spec.spec_value,
          spec_value_id: spec.spec_value_id,
          spec_value_img: {
            original: spec.spec_image,
            thumbnail: spec.thumbnail,
          },
        };
        spec_value_ids.push(spec.spec_value_id);
        if (_specIndex === -1) {
          specList.push({..._spec, valueList: [{..._value}]});
        } else if (
          specList[_specIndex].valueList.findIndex(
            __value => __value.spec_value_id === spec.spec_value_id,
          ) === -1
        ) {
          specList[_specIndex].valueList.push({..._value});
        }
      });
      this.skuMap.set(spec_value_ids.join('-'), sku);
    });
    await this.setState({skus, specList});
    const {showInfo} = this.state;
    let sku;
    // 如果没有规格，把商品第一个sku给已选择sku
    if (specList.length == 0) {
      sku = this.skuMap.get('no_spec');
      this._setSkuPromotionAnCoupons(sku, showInfo);
      pors = this.state.proBarsList;
    } else {
      sku = this.props.goods;
    }
    const _showInfo = {
      ...showInfo,
      image: sku.thumbnail,
      price: sku.price,
      sn: sku.sn,
      specVals: '默认 ',
    };
    // 如果有积分、团购、限时抢购
    if (pors.length) {
      let proPrice;
      const exchange = pors.filter(item => item.exchange)[0];
      const groupbuy = pors.filter(item => item.groupbuy_goods_vo)[0];
      const seckill = pors.filter(item => item.seckill_goods_vo)[0];
      if (exchange) {
        const ex = exchange.exchange;
        proPrice = ex.exchange_money;
        _showInfo.point = ex.exchange_point;
      } else if (groupbuy) {
        proPrice = groupbuy.groupbuy_goods_vo.price;
      } else if (seckill) {
        proPrice = seckill.seckill_goods_vo.seckill_price;
      }
      _showInfo.proPrce = proPrice;
    }

    await this.setState({showInfo: _showInfo});
    //如果传递过来的sku_id不为null
    if (sku_id != null) {
      skus.map((sku, index) => {
        if (sku.sku_id == sku_id) {
          let specs = sku.spec_list;
          if (specs == null) {
            return;
          }
          specs.map((spec, index) => {
            const _value = {
              spec_value: spec.spec_value,
              spec_value_id: spec.spec_value_id,
              spec_value_img: {
                original: spec.spec_image,
                thumbnail: spec.thumbnail,
              },
            };
            this._onSelectSpec(spec, index, _value);
          });
        }
      });
      this._makePinTuan(sku_id);
    } else {
      //设置第一个规格为默认
      specList.map((spec, index) => {
        this._onSelectSpec(spec, index, spec.valueList[0]);
      });
    }
  };

  /**
   * 处理拼团
   * @param sku_id
   * @private
   */
  _makePinTuan = async sku_id => {
    const {pinTuanList} = this.state;
    const {goods_id} = this.props.goods;
    const pinTuanSkuId = pinTuanList
      .map((sku, index) => {
        if (sku.sku_id === sku_id) {
          return sku_id;
        } else {
          return undefined;
        }
      })
      .filter(item => !!item);
    if (pinTuanSkuId.length > 0) {
      const pinTuan = await API_Promotions.getPinTuanInfo(pinTuanSkuId[0]);
      if (pinTuan) {
        DeviceEventEmitter.emit(`PinTuan-${pinTuan.goods_id}`, pinTuan);
        this.setState({pinTuan});
      }
    } else {
      DeviceEventEmitter.emit(`PinTuan-${goods_id}`);
      this.setState({pinTuan: null});
    }
  };

  /**
   * 选择规格
   * @param spec
   * @param specIndex
   * @param specVal
   * @returns {Promise<void>}
   * @private
   */
  _onSelectSpec = async (spec, specIndex, specVal) => {
    // 如果当前规格值已选中，则不做任何操作
    if (specVal.selected) {
      return;
    }
    const {showInfo, proBarsList, promotionList} = this.state;
    // ⬇设置选中效果
    const specList = JSON.parse(JSON.stringify(this.state.specList));
    let {valueList} = specList[specIndex];
    valueList = valueList.map(item => {
      item.selected = item.spec_value_id === specVal.spec_value_id;
      return item;
    });
    specList[specIndex].valueList = valueList;
    // ⬇选出选中的规格
    let {selectedSpecIds, selectedSpecVals} = this.state;
    selectedSpecIds[specIndex] = specVal.spec_value_id;
    selectedSpecVals[specIndex] = specVal.spec_value;
    // ⬇选出符合规格的sku
    const selectedSku = this._getSelectSku(selectedSpecIds);
    await this.setState({
      specList,
      selectedSpecIds,
      selectedSpecVals,
    });
    let _showInfo = {...showInfo};
    // ⬇如果规格是图片规格
    if (spec.spec_type === 1) {
      _showInfo.image = specVal.spec_value_img.thumbnail;
    }
    if (selectedSku) {
      // ⬇选择sku
      await this.setState({selectedSku});
      const {goods} = this.props;
      _showInfo.price = selectedSku.price;
      _showInfo.sn = selectedSku.sn;
      _showInfo.specVals = selectedSpecVals.join('-');
      this._setSkuPromotionAnCoupons(selectedSku, _showInfo);
    }
    // ⬇展示信息
    this.setState({showInfo: _showInfo});
  };

  /**
   * 筛选出符合规格的sku
   * @param specIds
   * @returns {Promise<*>}
   * @private
   */
  _getSelectSku = specIds => {
    let sku;
    if (specIds.length) {
      sku = this.skuMap.get(specIds.join('-'));
    } else {
      sku = this.skuMap.get('no_spec');
    }
    return sku;
  };

  /**
   * 设置当前选中sku的活动列表、优惠券列表等
   * @param sku
   * @private
   */
  _setSkuPromotionAnCoupons(sku, _showInfo) {
    const {goods} = this.props;
    const {proBarsList, promotionList, couponsList} = this.state;
    //团购，积分商品，秒杀商品轮播图下方展示的活动
    let proBars = [];
    proBarsList.map((item, index) => {
      if (item.sku_id == sku.sku_id) {
        proBars.push(item);
        _showInfo.price = item.price;
      }
      if (item.promotion_type == 'EXCHANGE') {
        proBars.push(item);
        _showInfo.price = item.price;
      }
    });
    //设置促销活动
    let promotions = [];
    promotionList.map((item, index) => {
      if (item.sku_id == sku.sku_id || item.goods_id == -1) {
        promotions.push(item);
      }
    });
    //设置优惠券
    let coupons = [];
    couponsList.map((item, index) => {
      if (item.sku_id == sku.sku_id) {
        coupons = item.coupon_list;
      }
    });
    //设置sku参与的活动
    if (proBars != null && proBars.length > 0) {
      sku.activity_id = proBars[0].activity_id;
      sku.promotion_type = proBars[0].promotion_type;
    }
    //设置sku参与的活动
    if (promotions != null && promotions.length > 0) {
      sku.activity_id = promotions[0].activity_id;
      sku.promotion_type = promotions[0].promotion_type;
    }
    DeviceEventEmitter.emit(`SelectedSkuChange-${goods.goods_id}`, sku);
    this.setState({
      selectedSku: sku,
      proBars,
      promotions,
      coupons,
    });
  }

  /**
   * 更新购买数量
   * @param symbol
   * @returns {Promise<void>}
   * @private
   */
  _updateBuyNum = async symbol => {
    const {buyNum} = this.state;
    let num = Number(buyNum);
    if (symbol === '-' && buyNum < 2) {
      return;
    }
    if (symbol === '-') {
      num -= 1;
    } else {
      num += 1;
    }
    await this.setState({buyNum: String(num)});
    const {goods} = this.props;
    DeviceEventEmitter.emit(`BuyNumUpdate-${goods.goods_id}`, String(num));
  };

  /**
   * 输入购买数量
   * @param event
   * @returns {Promise<void>}
   * @private
   */
  _editingBuyNum = async event => {
    event.persist();
    let num = Number(event.nativeEvent.text);
    if (num < 1 || num > 999999) {
      return;
    }
    const {dispatch} = this.props;
    if (isNaN(num)) {
      dispatch(messageActions.error('您的输入有误，请输入正整数！'));
      return;
    }
    await this.setState({buyNum: String(num)});
    const {goods} = this.props;
    DeviceEventEmitter.emit(`BuyNumUpdate-${goods.goods_id}`, String(num));
  };

  render() {
    const {goods, onPress} = this.props;
    let {
      skus,
      specList,
      coupons,
      promotions,
      proBars,
      shop,
      selectedSku,
      buyNum,
      showInfo,
      pinTuan,
      pinTuanOrder,
    } = this.state;
    return (
      <View style={styles.container}>
        {pinTuan ? (
          <GoodsProBar pinTuan={pinTuan} />
        ) : proBars.length ? (
          <GoodsProBar data={proBars} />
        ) : (
          undefined
        )}
        <TextCell text={goods.goods_name} />
        {pinTuan ? (
          undefined
        ) : !proBars.length ? (
          <ItemCell
            content={
              <Price
                price={showInfo.price}
                advanced
                scale={1.75}
                style={styles.price}
              />
            }
          />
        ) : (
          undefined
        )}
        {coupons.length ? <GoodsCoupons data={coupons} /> : undefined}
        {promotions.length ? <GoodsPromotions data={promotions} /> : undefined}
        {skus.length ? (
          <GoodsSkus
            goodsId={goods.goods_id}
            specList={specList}
            buyNum={buyNum}
            selectedSku={selectedSku}
            showInfo={showInfo}
            onSelectSpec={this._onSelectSpec}
            onQuantityPress={this._updateBuyNum}
            onQuantityEditing={this._editingBuyNum}
          />
        ) : (
          undefined
        )}
        <GoodsShip goodsId={goods.goods_id} />
        {shop ? <MainShop data={shop} /> : undefined}
        {pinTuanOrder && pinTuanOrder.length > 0 ? (
          <GoodsAssembleOrder
            goodsId={goods.goods_id}
            onPress={onPress}
            skuId={pinTuanOrder[0].sku_id}
          />
        ) : (
          undefined
        )}
        <GoodsComments goodsId={goods.goods_id} onPress={onPress} />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  price: {
    fontSize: 20,
    color: colors.main,
    fontWeight: '600',
  },
  margin_vert: {
    marginTop: 10,
  },
});

export default connect()(GoodsMainPage);
