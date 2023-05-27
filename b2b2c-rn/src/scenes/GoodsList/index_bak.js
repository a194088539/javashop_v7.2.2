/**
 * Created by Andste on 2018/9/30.
 */
import React, {Component} from 'react';
import {View, StyleSheet, Text, StatusBar, TouchableOpacity} from 'react-native';
import {connect} from 'react-redux';
import {colors} from '../../../config';
import SearchBar from './SearchBar';
import FilterBar from './FilterBar';
import FlatList from './FlatList';
import ShopFlatList from '../Shop/ShopListScene';
import {searchActions} from '../../redux/actions';

class GoodsList extends Component {
  static navigationOptions = {headerShown: false};

  constructor(props) {
    super(props);
    this.params = this.props.navigation.state.params || {};
    const {cat_id, keyword} = this.params;
    if (cat_id) {
      this.props.dispatch(searchActions.searchCatIdChanged(cat_id));
    }
    if (keyword) {
      this.props.dispatch(searchActions.searchKeywordChaned(keyword));
    }
    this.state = {
      opt: 'goods',
    };
  }

  optGoodsOrShop = item => {
    this.setState({
      opt: item,
    });
  };

  render() {
    const {opt} = this.state;
    return (
      <View style={styles.container}>
        <StatusBar barStyle="dark-content" />
        <SearchBar onKeywordChange={this._onKeywordChange} />
        <View style={styles.goods_or_shop_view}>
          <TouchableOpacity
            style={styles.goods_or_shop_text_view}
            onPress={() => {
              this.optGoodsOrShop('goods');
            }}>
            <Text>商品</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={styles.goods_or_shop_text_view}
            onPress={() => {
              this.optGoodsOrShop('shop');
            }}>
            <Text>店铺</Text>
          </TouchableOpacity>
        </View>
        {opt == 'goods' ? (
          <View style={styles.container}>
            <FilterBar />
            <FlatList />
          </View>
        ) : (
          <ShopFlatList />
        )}
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.border_line,
  },
  header_right: {
    width: 34,
    height: 44,
    justifyContent: 'center',
    alignItems: 'center',
  },
  view_type_icon: {
    width: 20,
    height: 20,
  },
  goods_or_shop_view: {
    backgroundColor: '#FAFAFA',
    flexDirection: 'row',
    justifyContent: 'center',
    height: 30,
  },
  goods_or_shop_text_view: {
    width: 100,
    height: 30,
    lineHeight: 35,
    fontSize: 16,
    alignItems: 'center',
  },
});

export default connect()(GoodsList);
