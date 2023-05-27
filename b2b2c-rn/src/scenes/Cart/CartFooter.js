/**
 * Created by Andste on 2018/11/19.
 */
import React, {PureComponent} from 'react';
import {View, Text, StyleSheet} from 'react-native';
import {isIphoneX} from 'react-native-iphone-x-helper';
import {navigate} from '../../navigator/NavigationService';
import {connect} from 'react-redux';
import {colors} from '../../../config';
import {Screen} from '../../utils';
import {Checkbox, Price, BigButton} from '../../widgets';

class CartFooter extends PureComponent {
  _toCheckout = () => navigate('Checkout', {way: 'CART'});

  render() {
    const {
      root,
      onCheckAll,
      onDeleteGoods,
      totalNum,
      selectedNum,
      cartPrice,
      editShow,
    } = this.props;
    const increaseHeight = isIphoneX() && !root ? 35 : 0;
    const allChecked = totalNum === selectedNum && totalNum !== 0;
    return (
      <View
        style={[
          styles.container,
          {height: 50 + increaseHeight, paddingBottom: increaseHeight},
        ]}>
        <View style={styles.left}>
          <Checkbox
            checked={allChecked}
            label="全选"
            onPress={() => onCheckAll(allChecked)}
          />
          {!editShow && (
            <View style={styles.price_view}>
              <Text style={{fontSize: 13}}>
                合计:
                <Price price={cartPrice.total_price} scale={0.8} />
              </Text>
              <Text style={{fontSize: 11}}>
                返现:
                <Price price={cartPrice.cash_back} scale={0.7} />
              </Text>
            </View>
          )}
        </View>

        {editShow && (
          <View style={styles.edit_view}>
            {/* <BigButton
            style={styles.edit_btn}
            textStyles={styles.edit_text}
            disabled={selectedNum < 1}
            title={'移入收藏'}
            onPress={this._toCheckout}
          /> */}
            <BigButton
              style={[styles.edit_btn, styles.edit_btn_del]}
              textStyles={styles.edit_text}
              disabled={selectedNum < 1}
              title={'删除'}
              onPress={onDeleteGoods}
            />
          </View>
        )}

        {!editShow && (
          <BigButton
            style={styles.checkout_btn}
            disabled={selectedNum < 1}
            title={`去结算(${selectedNum})`}
            onPress={this._toCheckout}
          />
        )}
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    width: Screen.width,
    backgroundColor: '#FFFFFF',
    borderColor: colors.cell_line_backgroud,
    borderTopWidth: Screen.onePixel,
  },
  left: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    width: Screen.width - 100,
    paddingLeft: 10,
  },
  price_view: {
    alignItems: 'flex-end',
    marginLeft: 5,
    paddingRight: 10,
  },
  edit_view: {
    width: 100,
    flexDirection: 'row',
    justifyContent: 'flex-end',
    alignItems: 'center',
  },
  edit_btn: {
    width: 100,
  },
  edit_text: {
    fontSize: 16,
  },
  checkout_btn: {
    width: 100,
  },
});

export default connect(state => ({...state.cart}))(CartFooter);
