/**
 * Created by lylyulei on 2020-04-27 17:45.
 */
import React, {Component} from 'react';
import {View, Image, StatusBar} from 'react-native';
import ScrollableTabView from 'react-native-scrollable-tab-view';
import TabBar from 'react-native-underline-tabbar';
import {HeaderBack} from '../../components';
import {colors} from '../../../config';
import * as API_Goods from '../../apis/goods';

import PinTuanFlatList from './PinTuanFlatList';

export default class PinTuanScene extends Component {
  static navigationOptions = ({navigationOptions}) => ({
    headerLeft: () => <HeaderBack tintColor="#FFFFFF" />,
    headerTintColor: '#FFFFFF',
    headerTitle: (
      <Image
        source={require('../../images/icon-pin-tuan-header.png')}
        style={{width: 100, height: 23}}
      />
    ),
    headerStyle: {
      ...navigationOptions.headerStyle,
      backgroundColor: colors.main,
    },
  });

  constructor(props) {
    super(props);
    this.state = {
      category: [{category_id: 0, name: '全部'}],
    };
    this._getCategroy();
  }

  componentDidMount() {
    // this._getCategroy();
  }

  _getCategroy = async () => {
    const {category} = this.state;
    const res = (await API_Goods.getCategory(0)) || [];
    this.setState({category: category.concat(res)});
  };

  _renderTabBar = () => (
    <TabBar
      underlineColor="#fff"
      underlineHeight={3}
      tabBarStyle={{
        marginTop: 0,
        paddingTop: 10,
        height: 40,
        alignItems: 'center',
      }}
      tabBarTextStyle={{
        fontWeight: 'bold',
        fontSize: 14,
      }}
    />
  );

  render() {
    const {category} = this.state;
    return (
      <View style={{flex: 1}}>
        <StatusBar barStyle="light-content" />
        <ScrollableTabView
          tabBarInactiveTextColor="#ACACAC"
          tabBarActiveTextColor="#FFFFFF"
          tabBarBackgroundColor="#333333"
          renderTabBar={this._renderTabBar}>
          {category.length > 1 &&
            category.map(item => (
              <PinTuanFlatList
                tabLabel={{label: item.name}}
                catId={item.category_id}
                key={item.category_id}
              />
            ))}
        </ScrollableTabView>
      </View>
    );
  }
}
