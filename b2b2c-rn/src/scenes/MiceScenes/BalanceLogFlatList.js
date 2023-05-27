/**
 * Created by lylyulei on 2020-04-16 11:26.
 */
import React, {PureComponent} from 'react';
import * as API_Member from '../../apis/members';
import {StyleSheet, Text, TouchableOpacity, View} from 'react-native';
import {FlatList} from '../../components';
import {isIphoneX} from 'react-native-iphone-x-helper';
import {DataEmpty} from '../../components/EmptyViews';
import {Foundation, Screen} from '../../utils';
import {F14Text} from '../../widgets/Text';
import {colors} from '../../../config';

export default class BalanceLogFlatList extends PureComponent {
  constructor(props) {
    super(props);
    this.isLog = props.type === 'log';
    this.params = {
      page_no: 1,
      page_size: 20,
    };
    this.state = {
      loading: false,
      noData: false,
      dataList: [],
      open_id: null,
    };
  }

  componentDidMount() {
    this._onRefresh();
  }

  /**
   * 刷新数据
   * @private
   */
  _onRefresh = async () => {
    this.setState({loading: true});
    this.params.page_no = 1;
    this._getDataList();
  };

  _getDataList = async () => {
    let {params, state} = this;
    const {dataList} = state;
    try {
      let res;
      if (this.isLog) {
        res = (await API_Member.depositeLog(params)) || {};
      } else {
        res = (await API_Member.rechargeList(params)) || {};
      }
      this.setState({
        loading: false,
        noData: !res.data || !res.data[0],
        dataList: params.page_no === 1 ? res.data : dataList.concat(res.data),
      });
    } catch (error) {
      this.setState({
        loading: false,
      });
    }
  };

  /**
   * 滚动到底部触发
   * @private
   */
  _onEndReached = () => {
    let {dataList, loading, noData} = this.state;
    if (loading || noData || !dataList[9]) {
      return;
    }
    this.params.page_no++;
    this._getDataList();
  };

  _renderItem = ({item}) => {
    let money_color = item.money && item.money > 0;
    return this.isLog ? (
      <View style={styles.balance_item}>
        <View style={styles.balance_left}>
          <F14Text numberOfLines={2}>{item.detail}</F14Text>
          <F14Text style={styles.balance_item_time}>
            {Foundation.unixToDate(item.time)}
          </F14Text>
        </View>
        <View style={styles.balance_right}>
          <Text style={money_color ? styles.balance_red : styles.balance_blue}>
            {item.money > 0 ? '+' : ''}
            {Foundation.formatPrice(Number(item.money).toFixed(2))}元
          </Text>
        </View>
      </View>
    ) : (
      <View style={styles.recharge_item}>
        <View style={styles.recharge_left}>
          <Text style={styles.balance_item_time}>
            充值单号：{item.recharge_sn}
          </Text>
          <Text style={styles.balance_item_time}>
            充值时间：{Foundation.unixToDate(item.recharge_time)}
          </Text>
          <Text style={styles.balance_item_time}>
            支付方式：{item.recharge_way}
          </Text>
        </View>
        <View style={styles.balance_right}>
          <Text style={styles.balance_red}>
            {Foundation.formatPrice(item.recharge_money)}元
          </Text>
        </View>
      </View>
    );
  };

  render() {
    const {loading, dataList} = this.state;
    return (
      <View style={styles.container}>
        <FlatList
          data={dataList}
          renderItem={this._renderItem}
          ListHeaderComponent={() => <View style={{height: 10}} />}
          ListFooterComponent={() => (
            <View style={{height: isIphoneX() ? 25 : 0}} />
          )}
          ListEmptyComponent={<DataEmpty />}
          onRefresh={this._onRefresh}
          refreshing={loading}
          onEndReached={this._onEndReached}
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  balance_item: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    width: Screen.width,
    maxHeight: 100,
    backgroundColor: '#FFFFFF',
    paddingHorizontal: 10,
    paddingVertical: 10,
    borderBottomWidth: 1,
    borderBottomColor: colors.border_line,
  },
  balance_left: {
    width: ((Screen.width - 20) / 4) * 3 - 20,
    height: 60,
    justifyContent: 'space-between',
  },
  balance_item_time: {
    color: '#666666',
  },
  balance_right: {
    width: (Screen.width - 20) / 4 + 20,
    justifyContent: 'center',
    alignItems: 'flex-end',
  },
  balance_red: {
    fontSize: 20,
    fontWeight: '600',
    color: colors.main,
  },
  balance_blue: {
    fontSize: 20,
    fontWeight: '600',
    color: colors.balance_blue,
  },
  balance_item_num_tit: {
    fontSize: 12,
    color: '#777777',
  },
  separator: {
    width: Screen.width - 10,
    height: Screen.onePixel,
    marginLeft: 10,
    backgroundColor: colors.cell_line_backgroud,
  },
  recharge_item: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    width: Screen.width,
    maxHeight: 120,
    backgroundColor: '#FFFFFF',
    paddingHorizontal: 10,
    paddingVertical: 10,
    borderBottomWidth: 1,
    borderBottomColor: colors.border_line,
  },
  recharge_left: {
    width: ((Screen.width - 20) / 4) * 3 - 20,
    height: 80,
    justifyContent: 'space-between',
  },
});
