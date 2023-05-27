/**
 * Created by Andste on 2018/9/30.
 */
import React, {Component} from 'react';
import {
  View,
  Image,
  TouchableOpacity,
  StatusBar,
  Platform,
  BackHandler,
  StyleSheet,
} from 'react-native';
import {StackActions} from 'react-navigation';
import {connect} from 'react-redux';
import {searchActions} from '../../redux/actions';
import {colors} from '../../../config';

import SearchBar from '../../components/SearchBar';
import FilterBar from './FilterBar';
import FlatList from './FlatList';
import ShopFlatList from '../Shop/ShopListScene';

class GoodsList extends Component {
  static navigationOptions = ({navigation}) => {
    const {params = {}} = navigation.state;
    const viewType =
      params.view_type === 'double'
        ? require('../../images/icon-viewtype_double.png')
        : require('../../images/icon-viewtype_single.png');
    const toSearch = () =>
      navigation.dispatch(StackActions.push({routeName: 'Search'}));
    const viewTypeChanged = () =>
      navigation.setParams({
        view_type: params.view_type === 'double' ? 'single' : 'double',
      });
    return {
      headerTitle: <SearchBar onPress={toSearch} keywords={params.keyword} />,
      headerRight: (
        <TouchableOpacity onPress={viewTypeChanged} style={styles.header_right}>
          <Image source={viewType} style={styles.view_type_icon} />
        </TouchableOpacity>
      ),
    };
  };

  constructor(props) {
    super(props);
    // this.params = this.props.navigation.state.params || {};
    // const {cat_id, keyword} = this.params;
    // console.log(`cat_id =1= ${cat_id}`);
    // console.log(`keyword =1= ${keyword}`);
    // if (cat_id) {
    //   this.props.dispatch(searchActions.searchCatIdChanged(cat_id));
    // }
    // if (keyword) {
    //   this.props.dispatch(searchActions.searchKeywordChaned(keyword));
    // }
    this.isShop = false;
    this.state = {
      isShop: false,
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

  _showShopList(isShop) {
    if (isShop === 'shop') {
      this.setState({isShop: true});
    } else {
      this.setState({isShop: false});
    }
  }

  render() {
    const {isShop} = this.state;
    if (this.props.navigation.state.params != null) {
      let {view_type} = this.props.navigation.state.params;
      this.props.dispatch(searchActions.searchViewTypeChanged(view_type));
    }
    this.params = this.props.navigation.state.params || {};
    const {cat_id, keyword} = this.params;
    return (
      <View style={styles.container}>
        <StatusBar barStyle="dark-content" />
        <FilterBar _showShopList={this._showShopList.bind(this)} />
        {isShop ? (
          <ShopFlatList />
        ) : (
          <FlatList catId={cat_id} keywords={keyword} />
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
});

export default connect()(GoodsList);
