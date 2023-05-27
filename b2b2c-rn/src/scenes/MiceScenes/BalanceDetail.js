/**
 * Created by lylyulei on 2020/4/16 11:19.
 */
import React, {Component} from 'react';
import ScrollableTabView, {
  DefaultTabBar,
} from 'react-native-scrollable-tab-view';
import {StatusBar, StyleSheet, TouchableOpacity, View} from 'react-native';
import {colors} from '../../../config';
import {Screen} from '../../utils';
import {connect} from 'react-redux';
import BalanceLogFlatList from './BalanceLogFlatList';

class BalanceDetail extends Component {
  static navigationOptions = {
    title: '余额明细',
  };
  _renderTabBar = () => (
    <DefaultTabBar style={styles.tabBar} tabStyle={{paddingBottom: 0}} />
  );

  render() {
    return (
      <View style={styles.container}>
        <StatusBar barStyle="dark-content" />
        <ScrollableTabView
          locked
          tabBarUnderlineStyle={styles.tabBarUnderlineStyle}
          tabBarActiveTextColor={colors.main}
          renderTabBar={this._renderTabBar}
          contentProps={{bounces: false}}>
          <BalanceLogFlatList tabLabel="余额日志" type="log" />
          <BalanceLogFlatList tabLabel="充值记录" type="recharge" />
        </ScrollableTabView>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  tabBar: {
    height: 40,
    borderColor: colors.cell_line_backgroud,
    backgroundColor: '#FFFFFF',
  },
  tabBarUnderlineStyle: {
    backgroundColor: colors.main,
    height: 1,
    width: 80,
    left: (Screen.width / 2 - 80) / 2,
  },
});

export default connect()(BalanceDetail);
