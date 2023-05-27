/**
 * Created by Andste on 2018/11/12.
 */
import React, {PureComponent} from 'react';
import {View, Text, StyleSheet, TouchableOpacity} from 'react-native';
import {Foundation} from '../../utils';
import {isIphoneX} from 'react-native-iphone-x-helper';
import {FlatList} from '../../components';
import {DataEmpty} from '../../components/EmptyViews';
import {SwipeListView} from 'react-native-swipe-list-view';
import * as API_Message from '../../apis/message';

export default class SiteMessageFlatlist extends PureComponent {
  constructor(props) {
    super(props);
    this.unread = props.type === 'unread';
    this.params = {
      page_no: 1,
      page_size: 10,
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

  _getDataList = async () => {
    let {params, state} = this;
    const {dataList} = state;
    if (this.unread) {
      params.read = 0;
    }
    try {
      const res = (await API_Message.getMessages(params)) || {};
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
   * 刷新数据
   * @private
   */
  _onRefresh = async () => {
    this.setState({loading: true});
    this.params.page_no = 1;
    this._getDataList();
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
    return (
      <View>
        <View style={styles.separator}>
          <View style={styles.separator_time}>
            <Text style={styles.separator_time_text}>
              {Foundation.unixToDate(item.receive_time)}
            </Text>
          </View>
        </View>
        <SwipeListView
          data={[item]}
          keyExtractor={data => data.id}
          disableRightSwipe={item.is_read === 1}
          directionalDistanceChangeThreshold={1}
          swipeToOpenPercent={1}
          renderItem={data => {
            return (
              <View style={styles.standaloneRowFront}>
                <Text style={styles.standaloneRowFrontText}>
                  {data.item.content}
                </Text>
              </View>
            );
          }}
          renderHiddenItem={(data, rowMap) => (
            <View style={styles.rowBack}>
              <TouchableOpacity
                style={[styles.backBtn, styles.backBtnLeft]}
                onPress={async () => {
                  rowMap[data.item.id].closeRow();
                  await API_Message.messageMarkAsRead(data.item.id);
                  this._onRefresh();
                }}>
                <Text style={styles.backTextWhite}>已读</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.backBtn, styles.backBtnRight]}
                onPress={async () => {
                  rowMap[data.item.id].closeRow();
                  await API_Message.deleteMessage(data.item.id);
                  this._onRefresh();
                }}>
                <Text style={styles.backTextWhite}>删除</Text>
              </TouchableOpacity>
            </View>
          )}
          style={{marginBottom: 10}}
          leftOpenValue={75}
          rightOpenValue={-75}
        />
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
    paddingHorizontal: 10,
  },
  separator: {
    height: 35,
    justifyContent: 'center',
    alignItems: 'center',
  },
  separator_time: {
    backgroundColor: '#DFDEDE',
    paddingHorizontal: 5,
    paddingVertical: 3,
    borderRadius: 10,
  },
  separator_time_text: {
    color: '#FFFFFF',
    fontSize: 12,
  },
  item: {
    backgroundColor: '#FFFFFF',
    borderRadius: 5,
    overflow: 'hidden',
  },
  item_content: {
    paddingHorizontal: 10,
    paddingVertical: 5,
    minHeight: 60,
  },
  standaloneRowFront: {
    alignItems: 'center',
    backgroundColor: '#FFF',
    justifyContent: 'center',
    minHeight: 50,
  },
  standaloneRowFrontText: {
    letterSpacing: 2, //字间距
    lineHeight: 25, //行间距
    margin: 15,
    fontFamily: 'Courier', //字体
  },
  backTextWhite: {
    color: '#FFF',
  },
  backBtn: {
    alignItems: 'center',
    bottom: 0,
    justifyContent: 'center',
    position: 'absolute',
    top: 0,
    width: 75,
  },
  backBtnLeft: {
    backgroundColor: 'blue',
    left: 0,
  },
  backBtnRight: {
    backgroundColor: 'red',
    right: 0,
  },
  rowBack: {
    alignItems: 'center',
    backgroundColor: '#DDD',
    flex: 1,
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingLeft: 15,
  },
});
