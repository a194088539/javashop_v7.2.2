/**
 * Created by Andste on 2018/11/23.
 */
import React, {PureComponent} from 'react';
import {
  View,
  Text,
  Image,
  DeviceEventEmitter,
  TouchableOpacity,
  StyleSheet,
} from 'react-native';
import {connect} from 'react-redux';
import {messageActions, cartActions} from '../../redux/actions';
import {navigate} from '../../navigator/NavigationService';
import {isIphoneX} from 'react-native-iphone-x-helper';
import Icon from 'react-native-vector-icons/Ionicons';
import {colors} from '../../../config';
import {Foundation, Screen} from '../../utils';
import {BigButton} from '../../widgets';
import {CartBadge} from '../../components';
import * as API_Members from '../../apis/members';
import * as API_Trade from '../../apis/trade';

class GoodsFooter extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      favorited: false,
      // 已选规格
      selectedSku: '',
      // 购买数量
      buyNum: '1',
      // 是否禁用购买按钮
      disBuyBtn: false,
    };
  }

  async componentDidMount() {
    const {goods_id = ''} = this.props.goods;
    // 获取是否已收藏
    await this._getGoodsIsCollection();
    // 监听已选规格发生改变
    this.listenerSelectedSkuChange = DeviceEventEmitter.addListener(
      `SelectedSkuChange-${goods_id}`,
      sku => {
        this.setState({selectedSku: sku});
      },
    );
    // 监听购买数量发生改变
    this.listenerBuyNumUpdate = DeviceEventEmitter.addListener(
      `BuyNumUpdate-${goods_id}`,
      num => {
        this.setState({buyNum: num});
      },
    );
    // 监听购买按钮是否禁用
    this.listenerDisBuyBtn = DeviceEventEmitter.addListener(
      `DisBuyBtn-${goods_id}`,
      dis => {
        this.setState({disBuyBtn: dis});
      },
    );
    // 监听是否参与拼团活动
    this.listenerPinTuanBtn = DeviceEventEmitter.addListener(
      `PinTuan-${goods_id}`,
      pinTuan => {
        this.setState({pinTuan: pinTuan});
      },
    );
  }

  componentWillUnmount() {
    if (this.listenerSelectedSkuChange) {
      this.listenerSelectedSkuChange.remove();
    }
    if (this.listenerBuyNumUpdate) {
      this.listenerBuyNumUpdate.remove();
    }
    if (this.listenerDisBuyBtn) {
      this.listenerDisBuyBtn.remove();
    }
    if (this.listenerPinTuanBtn) {
      this.listenerPinTuanBtn.remove();
    }
  }

  /**
   * 获取商品是否已被收藏
   * @returns {Promise<void>}
   * @private
   */
  _getGoodsIsCollection = async () => {
    const {user, goods} = this.props;
    if (!user) {
      return;
    }
    const res = await API_Members.getGoodsIsCollect(goods.goods_id);
    this.setState({favorited: res.message});
  };

  /**
   * 收藏或取消收藏
   * @returns {Promise<void>}
   * @private
   */
  _onCollection = async () => {
    const {user, goods, dispatch} = this.props;
    const {favorited} = this.state;
    if (!user) {
      dispatch(messageActions.error('请先登录！'));
      return;
    }
    if (favorited) {
      await API_Members.deleteGoodsCollection(goods.goods_id);
      await this.setState({favorited: false});
      dispatch(messageActions.success('取消收藏成功！'));
    } else {
      try {
        await API_Members.collectionGoods(goods.goods_id);
        await this.setState({favorited: true});
        dispatch(messageActions.success('收藏成功！'));
      } catch (err) {
        const errorMessage = err.response.data.message;
        dispatch(messageActions.error(errorMessage));
      }
    }
  };

  /**
   *
   * @returns {Promise<void>}
   * @private
   */
  _onAddToCart = async () => {
    const {selectedSku, buyNum} = this.state;
    if (this._checkCanBuy()) {
      const {sku_id} = selectedSku;
      await API_Trade.addToCart(sku_id, buyNum);
      const {dispatch} = this.props;
      await dispatch(cartActions.getCartDataAction());
      dispatch(messageActions.success('加入购物车成功！'));
    }
  };

  /**
   * 立即购买
   * @returns {Promise<void>}
   * @private
   */
  _onBuyNow = async () => {
    const {selectedSku, buyNum, proBars} = this.state;
    if (this._checkCanBuy()) {
      const {sku_id, activity_id, promotion_type} = selectedSku;
      await API_Trade.buyNow(sku_id, buyNum, activity_id, promotion_type);
      navigate('Checkout', {way: 'BUY_NOW'});
    }
  };

  /**
   * 拼团购买
   * @returns {Promise<void>}
   * @private
   */
  _pinTuanBuyBow = async () => {
    const {selectedSku} = this.state;
    if (this._checkCanBuy()) {
      const {sku_id} = selectedSku;
      await API_Trade.pinTuanAddCart({sku_id, num: 1});
      navigate('Checkout', {way: 'PINTUAN'});
    }
  };

  /**
   * 检查是否可以购买
   * @private
   */
  _checkCanBuy = () => {
    const {user, goods} = this.props;
    const {selectedSku} = this.state;
    if (!user) {
      navigate('Login');
      return false;
    }
    if (!selectedSku) {
      DeviceEventEmitter.emit(`ShowGoodsSkusModal-${goods.goods_id}`);
      return false;
    }
    return true;
  };

  render() {
    const {goods} = this.props;
    const {favorited, disBuyBtn, pinTuan} = this.state;
    return (
      <View style={styles.container}>
        <ItemLabel
          icon="ios-heart-outline"
          activeIcon="ios-heart"
          label="收藏"
          activeLabel="已收藏"
          onPress={this._onCollection}
          activated={favorited}
        />
        <ItemLabel
          icon="ios-cart-outline"
          label="购物车"
          onPress={() => navigate('CartScene')}>
          <CartBadge />
        </ItemLabel>
        <ItemLabel
          icon={
            <Image
              source={require('../../images/icon-shop.png')}
              style={{width: 25, height: 25}}
            />
          }
          label="店铺"
          onPress={() => navigate('Shop', {id: goods.seller_id})}
        />
        {pinTuan ? (
          <View style={styles.btnContainer}>
            <BigButton
              title={
                '￥' +
                Foundation.formatPrice(pinTuan.origin_price) +
                '\n单独购买'
              }
              style={[styles.buy_btn, {backgroundColor: '#fd7e88'}]}
              disabled={disBuyBtn}
              textStyles={styles.buy_btn_text}
              onPress={this._onBuyNow}
            />
            <BigButton
              title={
                '￥' +
                Foundation.formatPrice(pinTuan.sales_price) +
                '\n' +
                pinTuan.required_num +
                '人团'
              }
              textStyles={styles.buy_btn_text}
              style={styles.buy_btn}
              disabled={disBuyBtn}
              onPress={this._pinTuanBuyBow}
            />
          </View>
        ) : (
          <View style={styles.btnContainer}>
            <BigButton
              title="加入购物车"
              style={[styles.buy_btn, {backgroundColor: '#f85'}]}
              disabled={disBuyBtn}
              onPress={this._onAddToCart}
            />
            <BigButton
              title="立即购买"
              style={styles.buy_btn}
              disabled={disBuyBtn}
              onPress={this._onBuyNow}
            />
          </View>
        )}
      </View>
    );
  }
}

const ItemLabel = ({
  icon,
  activeIcon,
  activated,
  label,
  activeLabel,
  children,
  onPress,
}) => {
  const IconEle =
    typeof icon === 'string' ? (
      <Icon
        style={activated && styles.item_label_act}
        name={activated ? activeIcon : icon}
        size={25}
      />
    ) : activated ? (
      activeIcon
    ) : (
      icon
    );
  return (
    <TouchableOpacity
      style={styles.item_label}
      activeOpacity={1}
      onPress={onPress}>
      <View style={styles.item_label_icon}>
        {IconEle}
        {children}
      </View>
      <Text style={styles.item_label_text}>
        {activated ? activeLabel : label}
      </Text>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  container: {
    position: 'absolute',
    zIndex: 100,
    bottom: 0,
    flexDirection: 'row',
    width: Screen.width,
    height: 50 + (isIphoneX() ? 35 : 0),
    paddingBottom: isIphoneX() ? 35 : 0,
    backgroundColor: '#FFFFFF',
  },
  item_label: {
    width: (Screen.width - 220) / 3,
    height: 50,
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
  },
  item_label_act: {
    color: colors.main,
  },
  item_label_icon: {
    height: 28,
  },
  item_label_text: {
    height: 15,
    fontSize: 12,
    color: colors.text,
  },
  buy_btn: {
    width: 110,
  },
  btnContainer: {
    flexDirection: 'row',
  },
  buy_btn_text: {
    fontSize: 15,
  },
});

export default connect(state => ({
  user: state.user.user,
}))(GoodsFooter);
