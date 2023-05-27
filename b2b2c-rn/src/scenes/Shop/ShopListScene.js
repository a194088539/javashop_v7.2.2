import React, {Component} from 'react';
import {
  View,
  Image,
  StyleSheet,
  FlatList,
  Text,
  TouchableOpacity,
} from 'react-native';
import * as API_Shop from '../../apis/shop';
import {colors} from '../../../config';
import {navigate} from '../../navigator/NavigationService';
import {TextLabel} from '../../widgets';
import {connect} from 'react-redux';
import {Screen} from '../../utils';

/**
 * 店铺列表
 */
class ShopListScene extends Component {
  constructor(props) {
    super(props);
    this.shopParams = {
      page_no: 1,
      page_size: 10,
      name: this.props.keyword,
    };
    this.state = {
      shopList: [],
      filtering: false,
      loading: false,
      noData: false,
    };
  }

  UNSAFE_componentWillReceiveProps(nextProps) {
    let {keyword} = nextProps;
    this.shopParams = {
      ...this.shopParams,
      name: keyword,
      page_no: 1,
    };
    this.setState({filtering: true}, this._getShopList);
  }

  componentDidMount(): void {
    this._getShopList();
  }

  _getShopList = async () => {
    const {page_no} = this.shopParams;
    const {shopList} = this.state;
    try {
      const {data} = await API_Shop.getShopList(this.shopParams);
      this.setState({
        loading: false,
        shopList: page_no === 1 ? data : shopList.concat(data),
        noData: !data || !data[0],
        filtering: false,
      });
    } catch (e) {
      this.setState({
        loading: false,
      });
    }
  };

  _onEndReached = () => {
    let {loading, noData, shopList} = this.state;
    if (!shopList[9] || loading || noData) {
      return;
    }
    this.params.page_no++;
    this.setState({loading: true}, this._getShopList);
  };

  _renderItem = ({item}) => (
    <View style={styles.shop_view}>
      <TouchableOpacity
        style={styles.shop_detail_view}
        onPress={() => {
          navigate('Shop', {id: item.shop_id});
        }}>
        <View style={styles.shop_info_view}>
          <Image
            style={styles.shop_detail_image}
            source={{uri: item.shop_logo}}
          />
          <View style={styles.shop_detail_name}>
            <Text style={styles.shop_detail_text}>{item.shop_name}</Text>
            <Text style={styles.shop_detail_text}>
              关注数：{item.shop_collect}
            </Text>
          </View>
        </View>
        <TextLabel
          text={'进店'}
          style={styles.shop_detail_btn}
          textStyle={{color: colors.main}}
          onPress={() => {
            navigate('Shop', {id: item.shop_id});
          }}
        />
      </TouchableOpacity>
      <View style={styles.shop_goods_view}>
        {item.goods_list.map((goods, index) => {
          if (index > 2) {
            return;
          }
          return (
            <TouchableOpacity
              onPress={() => {
                navigate('Goods', {id: goods.goods_id});
              }}>
              <Image
                style={styles.shop_goods_image}
                source={{
                  uri: goods.thumbnail,
                }}
              />
            </TouchableOpacity>
          );
        })}
      </View>
    </View>
  );

  render() {
    const {shopList, filtering} = this.state;
    return filtering ? (
      <View style={styles.filtering}>
        <Image
          source={require('../../images/icon-filtering.gif')}
          style={{width: Screen.width / 5}}
          resizeMode="contain"
        />
      </View>
    ) : (
      <FlatList
        style={styles.shop_container}
        data={shopList}
        renderItem={this._renderItem}
        initialNumToRender={10}
        onEndReached={this._onEndReached}
        onEndReachedThreshold={0.1}
      />
    );
  }
}

const IMAGE_WIDTH = (Screen.width - 20 - 30) / 3;

const styles = StyleSheet.create({
  shop_container: {
    flex: 1,
    backgroundColor: colors.border_line,
  },
  shop_view: {
    height: 170,
    backgroundColor: '#FFFFFF',
    margin: 5,
    borderRadius: 10,
    padding: 10,
  },
  shop_detail_view: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    height: 50,
  },
  shop_info_view: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  shop_goods_view: {
    flexDirection: 'row',
  },
  shop_detail_image: {
    height: 40,
    width: 100,
  },
  shop_detail_name: {
    marginLeft: 8,
  },
  shop_detail_text: {
    fontSize: 15,
    height: 19,
  },
  shop_goods_image: {
    height: IMAGE_WIDTH,
    width: IMAGE_WIDTH,
    marginRight: 10,
    borderRadius: 10,
  },
});

const select = state => {
  return {
    keyword: state.search.keyword,
  };
};

export default connect(select)(ShopListScene);
