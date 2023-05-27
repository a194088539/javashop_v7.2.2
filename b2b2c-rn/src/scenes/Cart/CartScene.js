/**
 * Created by Andste on 2018/11/19.
 */
import React, {Component} from 'react';
import {
  Alert,
  View,
  Image,
  Text,
  StatusBar,
  StyleSheet,
  Platform,
  TouchableOpacity,
  BackHandler,
} from 'react-native';
import {connect} from 'react-redux';
import {cartActions, messageActions} from '../../redux/actions';
import {NavigationEvents} from 'react-navigation';
import {KeyboardAwareSectionList} from 'react-native-keyboard-aware-scroll-view';
import {ActionSheetCustom as ActionSheet} from 'react-native-actionsheet';
import {colors} from '../../../config';
import {Screen} from '../../utils';
import {Loading} from '../../widgets';
import {HeaderBack} from '../../components';
import CartHeader from './CartHeader';
import CartFooter from './CartFooter';
import CartNotLogin from './CartNotLogin';
import SectionHeader from './CartSectionHeader';
import SeactionItem from './CartSeactionItem';
import * as API_Trade from '../../apis/trade';

class Cart extends Component {
  static navigationOptions = {
    headerShown: false,
    tabBarLabel: '购物车',
  };

  constructor(props) {
    super(props);
    this.state = {
      tagGoods: [],
      open_id: null,
      activitySku: '',
      topRightText: '编辑',
      loading: false,
      editShow: false,
    };
  }

  componentDidMount() {
    if (Platform.OS === 'android') {
      BackHandler.addEventListener('hardwareBackPress', () => {
        if (this.props.navigation) {
          return this.props.navigation.goBack();
        }
      });
    }
  }

  componentWillUnmount() {
    if (Platform.OS === 'android') {
      BackHandler.removeEventListener('hardwareBackPress', this.onBackAndroid);
    }
  }

  /**
   * 页面将要获得焦点
   * @returns {Promise<void>}
   * @private
   */
  _onWillFocus = async () => {
    const {user} = this.props;
    if (user.user) {
      this._getCartData();
    }
  };

  /**
   * 获取购物车数据
   * @private
   */
  _getCartData = async () => {
    this.props.dispatch(cartActions.getCartDataAction());
    this.setState({activitySku: ''});
  };

  _ItemSeparatorComponent = () => <View style={styles.separator} />;
  _renderSectionHeader = ({section}) => (
    <SectionHeader
      data={section}
      onPress={() => this._onCheckShop(section, section.checked)}
    />
  );
  _ListEmptyComponent = () => (
    <View style={styles.empty_icon_view}>
      <Image
        style={styles.empty_image}
        source={require('../../images/icon-empty-cart.png')}
      />
      <Text style={styles.empty_text}>购物车是空的</Text>
    </View>
  );

  /**
   * 移除商品
   * @param item
   * @returns {Promise<void>}
   * @private
   */
  _deleteItem = async item => {
    try {
      this.setState({loading: true});
      await API_Trade.deleteSkuItem(item.sku_id);
      this._getCartData();
    } finally {
      this.setState({loading: false});
    }
  };

  /**
   * 移除商品
   * @param item
   * @returns {Promise<void>}
   * @private
   */
  _deleteGoods = () => {
    const checkIds = this.props.skuList
      .filter(item => item.checked)
      .map(item => item.sku_id);
    Alert.alert('提示', '确认要移除选中商品吗？', [
      {text: '取消', onPress: () => {}},
      {
        text: '确认',
        onPress: async () => {
          try {
            this.setState({loading: true});
            await API_Trade.deleteSkuItem(checkIds);
            this._getCartData();
          } finally {
            this.setState({loading: false});
          }
        },
      },
    ]);
  };

  /**
   * 更新商品数量【通过加减】
   * @param sku
   * @param symbol
   * @returns {Promise<void>}
   * @private
   */
  _updateNumBySymbol = async (sku, symbol) => {
    if (symbol === '-' && sku.num < 2) {
      return;
    }
    const {dispatch} = this.props;
    if (symbol === '+' && sku.num >= sku.enable_quantity) {
      dispatch(messageActions.error('超过最大库存！'));
      return;
    }
    let _num = symbol === '+' ? sku.num + 1 : sku.num - 1;
    try {
      this.setState({loading: true});
      await API_Trade.updateSkuNum(sku.sku_id, _num);
      this._getCartData();
    } finally {
      this.setState({loading: false});
    }
  };

  /**
   * 更新商品数量【通过输入框】
   * @param sku
   * @param event
   * @returns {Promise<void>}
   * @private
   */
  _updateNumByInput = async (sku, event) => {
    let num = Number(event.nativeEvent.text);
    const {dispatch} = this.props;
    if (isNaN(num)) {
      dispatch(messageActions.error('您的输入有误，请输入正整数！'));
      return;
    }
    if (num < 1) {
      return;
    }
    if (num >= sku.enable_quantity) {
      dispatch(messageActions.error('超过最大库存，将调至最大库存数量！'));
      num = sku.enable_quantity;
    }
    try {
      this.setState({loading: true});
      await API_Trade.updateSkuNum(sku.sku_id, num);
      this._getCartData();
    } finally {
      this.setState({loading: false});
    }
  };

  /**
   * 选择店铺所有商品
   * @param checked
   * @param shop
   * @returns {Promise<void>}
   * @private
   */
  _onCheckShop = async (shop, checked) => {
    try {
      this.setState({loading: true});
      await API_Trade.checkShop(shop.seller_id, checked ? 0 : 1);
      this._getCartData();
    } finally {
      this.setState({loading: false});
    }
  };

  /**
   * 选择所有商品
   * @param checked
   * @returns {Promise<void>}
   * @private
   */
  _onCheckAll = async checked => {
    try {
      this.setState({loading: true});
      await API_Trade.checkAll(checked ? 0 : 1);
      this._getCartData();
    } finally {
      this.setState({loading: false});
    }
  };

  /**
   * 选择商品
   * @param sku
   * @returns {Promise<void>}
   * @private
   */
  _onCheckSku = async sku => {
    try {
      this.setState({loading: true});
      await API_Trade.updateSkuChecked(sku.sku_id, sku.checked ? 0 : 1);
      this._getCartData();
    } finally {
      this.setState({loading: false});
    }
  };

  /**
   * 促销活动切换
   * @param sku
   * @param index
   * @returns {Promise<void>}
   * @private
   */
  _onActivityChange = async (sku, index) => {
    const activity = sku.single_list[index];
    const {promotion_id, promotion_type} = activity;
    const {sku_id, seller_id} = sku;
    try {
      this.setState({loading: true});
      await API_Trade.changeActivity({
        seller_id,
        sku_id,
        promotion_id,
        promotion_type,
      });
      this._getCartData();
    } finally {
      this.setState({loading: false});
    }
  };

  /**
   * 不参与促销活动
   * @returns {Promise<void>}
   * @private
   */
  _cancelActivity = async (seller_id, sku_id) => {
    try {
      this.setState({loading: true});
      await API_Trade.cleanActivity({seller_id, sku_id});
      this._getCartData();
    } finally {
      this.setState({loading: false});
    }
  };

  /**
   * 展示促销活动
   * @private
   */
  _onShowActivity = async item => {
    await this.setState({activitySku: item});
    this.ActionSheet.show();
  };

  _onEditPress = () => {
    const {topRightText, editShow} = this.state;
    this.setState({
      topRightText: topRightText === '编辑' ? '完成' : '编辑',
      editShow: !editShow,
    });
  };

  /**
   *
   * @param index
   * @private
   */
  _onActionSheetPress = index => {
    const {activitySku} = this.state;
    const noJoin = index === activitySku.single_list.length + 1;
    // index为0点的是【取消】
    if (index === 0) {
      return;
    }
    if (noJoin) {
      this._cancelActivity(activitySku.seller_id, activitySku.sku_id);
    } else {
      this._onActivityChange(activitySku, index - 1);
    }
  };

  /**
   * 渲染商品数据
   * @param item
   * @returns {*}
   * @private
   */
  _renderItem = ({item}) => {
    return (
      <SeactionItem
        data={item}
        onCheck={() => this._onCheckSku(item)}
        onUpdateNumBySymbol={symbol => this._updateNumBySymbol(item, symbol)}
        onUpdateNumByInput={event => {
          event.persist();
          this._updateNumByInput(item, event);
        }}
        showActivitySelect={() => this._onShowActivity(item)}
        deleteItem={_item => {
          Alert.alert('提示', '确认要移除这件商品吗？', [
            {text: '取消', onPress: () => {}},
            {
              text: '确认',
              onPress: () => {
                this._deleteItem(_item);
              },
            },
          ]);
        }}
      />
    );
  };

  render() {
    const {shopList, navigation, user} = this.props;
    const {activitySku, topRightText, editShow, loading} = this.state;
    let actionSheetOptions = [];
    if (activitySku.single_list) {
      let isCheck = 0;
      activitySku.single_list.map(item => {
        if (item.is_check === 1) {
          isCheck = 1;
        }
        actionSheetOptions.push(
          <Text
            style={
              item.is_check === 0
                ? styles.unselectedActionSheet
                : styles.selectedActionSheet
            }>
            {item.promotion_name}
          </Text>,
        );
      });
      actionSheetOptions.push(
        <Text
          style={
            isCheck === 1
              ? styles.unselectedActionSheet
              : styles.selectedActionSheet
          }>
          不参加活动
        </Text>,
      );
      actionSheetOptions.unshift(
        <Text style={styles.unselectedActionSheet}>取消</Text>,
      );
    }
    return (
      <View style={styles.container}>
        <StatusBar barStyle="dark-content" />
        <NavigationEvents onWillFocus={this._onWillFocus} />
        <CartHeader
          backEle={navigation ? <HeaderBack /> : null}
          onEditPress={this._onEditPress}
          topRightText={topRightText}
          logined={!!this.props.user.user}
        />
        {user.user ? (
          <KeyboardAwareSectionList
            keyboardOpeningTime={0}
            extraHeight={0}
            sections={shopList}
            renderItem={this._renderItem}
            ItemSeparatorComponent={this._ItemSeparatorComponent}
            renderSectionHeader={this._renderSectionHeader}
            ListEmptyComponent={this._ListEmptyComponent}
          />
        ) : (
          <CartNotLogin />
        )}
        {user.user ? (
          <CartFooter
            root={!navigation}
            onCheckAll={this._onCheckAll}
            onDeleteGoods={this._deleteGoods}
            editShow={editShow}
          />
        ) : (
          undefined
        )}
        {actionSheetOptions.length ? (
          <ActionSheet
            ref={o => (this.ActionSheet = o)}
            options={actionSheetOptions}
            cancelButtonIndex={0}
            destructiveButtonIndex={actionSheetOptions.length - 1}
            onPress={this._onActionSheetPress}
          />
        ) : (
          undefined
        )}
        <Loading show={loading} />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    zIndex: -1,
  },
  separator: {},
  empty_icon_view: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    width: Screen.width,
    height: 75,
    backgroundColor: '#FFFFFF',
  },
  empty_image: {
    width: 35,
    height: 35,
  },
  unselectedActionSheet: {
    color: 'blue',
    fontSize: 16,
  },
  selectedActionSheet: {
    color: 'red',
    fontSize: 16,
  },
  empty_text: {
    fontSize: 16,
    color: colors.text,
    marginLeft: 10,
  },
  actionButtonItemText: {
    color: 'white',
  },
});

export default connect(state => ({
  ...state.cart,
  user: state.user,
}))(Cart);
