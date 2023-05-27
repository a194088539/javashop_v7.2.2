/**
 * Created by snow on 2020年03月07日
 */
import React, {Component} from 'react';
import {Alert, View, StatusBar, FlatList, StyleSheet, Text} from 'react-native';
import {connect} from 'react-redux';
import {messageActions} from '../../redux/actions';
import * as API_History from '../../apis/history';
import MyHistoryItem from './MyHistoryItem';
import {BigButton} from '../../widgets';
import {Foundation, Screen} from '../../utils';
import {isIphoneX} from 'react-native-iphone-x-helper';

class MyHistoryScene extends Component {
  static navigationOptions = ({navigation}) => {
    const {params = {}} = navigation.state;
    return {
      title: params.title || '我的足迹',
    };
  };

  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      dataList: [],
    };
    this.params = {
      page_no: 1,
      page_size: 10,
    };
  }

  componentDidMount(): void {
    this._getHistoryList();
  }

  /**
   * 获取足迹数据
   * @returns {Promise<void>}
   * @private
   */
  _getHistoryList = async () => {
    const {page_no} = this.params;
    const {dataList} = this.state;
    try {
      const res = (await API_History.getHistoryList(this.params)) || [];
      const {data = []} = res;
      await this.setState({
        loading: false,
        noData: !data[0],
        dataList: page_no === 1 ? data : dataList.concat(data),
        historyNum: data.length,
      });
    } catch (error) {
      await this.setState({
        loading: false,
      });
    }
  };

  _getItemLayout = (data, index) => ({length: 205, offset: 205 * index, index});

  _itemSeparatorComponent = () => <View style={styles.separator} />;

  _deleteGoods = async item => {
    Alert.alert('提示', '确认删除吗？', [
      {text: '取消', onPress: () => {}},
      {
        text: '确定',
        onPress: async () => {
          await API_History.delHistoryGoods(item.id);
          this.props.dispatch(messageActions.success('删除成功！'));
          this._onRefresh();
        },
      },
    ]);
  };

  /**
   * 刷新列表
   * @returns {Promise<void>}
   * @private
   */
  _onRefresh = async () => {
    this.params.page_no = 1;
    await this.setState({loading: true});
    this._getHistoryList();
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
    this._getHistoryList();
  };

  _renderItem = ({item}) => {
    return <MyHistoryItem data={item} delItem={this._deleteGoods} />;
  };
  _onDeleteHistory = () => {
    Alert.alert('提示', '确认清空吗？', [
      {text: '取消', onPress: () => {}},
      {
        text: '确定',
        onPress: async () => {
          await API_History.delAll();
          this.props.dispatch(messageActions.success('足迹清空成功！'));
          this._onRefresh();
        },
      },
    ]);
  }

  render() {
    const {dataList, loading, historyNum} = this.state;
    console.log(historyNum)
    return (
      <View style={styles.container}>
        <View style={{flex: 1, backgroundColor: '#FAFAFA', paddingBottom: 50}}>
          <StatusBar barStyle="dark-content" />
          <FlatList
            data={dataList}
            renderItem={this._renderItem}
            onRefresh={this._onRefresh}
            ItemSeparatorComponent={this._itemSeparatorComponent}
            getItemLayout={this._getItemLayout}
            refreshing={loading}
            onEndReached={this._onEndReached}
          />
        </View>
        <BigButton
          style={styles.clean_btn}
          disabled={historyNum == 0}
          title={`清空`}
          onPress={this._onDeleteHistory}
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  separator: {
    height: 10,
    backgroundColor: '#FAFAFA',
  },
  clean_btn: {
    position: 'absolute',
    bottom: 0,
    width: Screen.width,
    marginBottom: isIphoneX() ? 30 : 0,
  },
});

export default connect()(MyHistoryScene);
