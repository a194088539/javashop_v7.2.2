import React, {Component} from 'react';
import {
  Alert,
  View,
  StatusBar,
  FlatList,
  StyleSheet,
  Text,
  TouchableOpacity,
  Image,
  Button,
} from 'react-native';
import {connect} from 'react-redux';
import ScrollableTabView from 'react-native-scrollable-tab-view';
import * as API_Member from '../../apis/members';
import {F16Text} from '../../widgets/Text';
import {GoodsItemPro} from '../../widgets/GoodsWidgets';
import {Foundation, Screen} from '../../utils';
import {navigate} from '../../navigator/NavigationService';
import {colors} from '../../../config';
import {DeviceEventEmitter} from 'react-native';

class MyAskScene extends Component {
  static navigationOptions = ({navigation}) => {
    const {params = {}} = navigation.state;
    return {
      title: '咨询管理',
    };
  };

  constructor(props) {
    super(props);
    this.params = {
      page_no: 1,
      page_size: 10,
      reply_status: 'YES',
    };
    this.state = {
      loading: false,
      askList: [],
      answerList: [],
      askNoData: false,
      answerNoData: false,
    };
  }

  /**
   * 接收刷新通知，刷新界面
   */
  componentDidMount() {
    this._onRefreshAskData();
    this.refreshSubScription = DeviceEventEmitter.addListener(
      'refresh',
      message => {
        if (message) {
          this._getMyAnswer();
        }
      },
    );
  }
  componentWillUnmount() {
    this.refreshSubScription.remove();
  }
  /**
   * 读取我的提问数据
   * @returns {Promise<void>}
   * @private
   */
  _onRefreshAskData = async () => {
    const {page_no} = this.params;
    const {askList} = this.state;
    try {
      const res = (await API_Member.getAsk(this.params)) || {};
      const {data = []} = res;
      this.setState({
        loading: false,
        refundNoData: !data[0],
        askList: page_no === 1 ? data : askList.concat(data),
      });
    } catch (error) {
      this.setState({
        loading: false,
      });
    }
  };

  /**
   * 读取我的回答或者邀我回答数据
   * @returns {Promise<void>}
   * @private
   */
  _getMyAnswer = async () => {
    const {page_no} = this.params;
    const {answerList} = this.state;
    try {
      const res = (await API_Member.getAnswers(this.params)) || {};
      const {data = []} = res;
      this.setState({
        loading: false,
        refundNoData: !data[0],
        answerList: page_no === 1 ? data : answerList.concat(data),
      });
    } catch (error) {
      this.setState({
        loading: false,
      });
    }
  };

  _changeTab = tab => {
    if (tab.i == 0) {
      this._onRefreshAskData();
    } else if (tab.i == 1) {
      this.params.reply_status = 'YES';
      this._getMyAnswer();
    } else {
      this.params.reply_status = 'NO';
      this._getMyAnswer();
    }
  };

  render() {
    let initialPage = 0;
    const {askList, answerList, loading} = this.state;
    return (
      <View style={styles.container}>
        <StatusBar barStyle="dark-content" />
        <ScrollableTabView
          initialPage={initialPage}
          tabBarInactiveTextColor={colors.text}
          tabBarActiveTextColor={colors.main}
          tabBarBackgroundColor="#FFFFFF"
          contentProps={{bounces: false}}
          tabBarUnderlineStyle={styles.tabBarUnderlineStyle}
          onChangeTab={this._changeTab}>
          <AdvisoryList
            tabLabel="我的提问"
            tabNum={0}
            dataList={askList}
            loading={loading}
            ItemSeparatorComponent={this._itemSeparatorComponent}
            getItemLayout={this._getItemLayout}
            onRefresh={this._onRefreshAskData}
            onEndReached={this._onAskListEndReached}
          />
          <AdvisoryList
            tabLabel="我的回答"
            tabNum={1}
            dataList={answerList}
            loading={loading}
            ItemSeparatorComponent={this._itemSeparatorComponent}
            getItemLayout={this._getItemLayout}
            onRefresh={this._onRefreshAskData}
            onEndReached={this._onAskListEndReached}
          />
          <AdvisoryList
            tabLabel="邀我回答"
            tabNum={2}
            dataList={answerList}
            loading={loading}
            ItemSeparatorComponent={this._itemSeparatorComponent}
            getItemLayout={this._getItemLayout}
            onRefresh={this._onRefreshAskData}
            onEndReached={this._onAskListEndReached}
          />
        </ScrollableTabView>
      </View>
    );
  }
}

const AdvisoryList = ({
  tabLabel,
  tabNum,
  dataList,
  loading,
  statusFilter,
  ...props
}) => {
  return (
    <View>
      <FlatList
        data={dataList}
        renderItem={({item}) => <GoodsItemCell data={item} tabNum={tabNum} />}
        ListFooterBgColor="#FAFAFA"
        refreshing={loading}
        {...props}
      />
    </View>
  );
};

const GoodsItemCell = ({data, tabNum}) => {
  return (
    <TouchableOpacity
      style={styles.g_view}
      onPress={() => {
        if (tabNum != 2) {
          navigate('GoodsAskDetail', {
            goods_data: data,
            ask_id: data.ask_id,
          });
        }
      }}>
      <GoodsItem goods_name={data.goods_name} goods_img={data.goods_img} />
      {tabNum == 0 ? (
        <View>
          <F16Text style={styles.g_item_content} numberOfLines={2}>
            {data.content}
          </F16Text>
          <View style={styles.g_item_food}>
            <Text style={styles.g_item_food_text}>
              {Foundation.unixToDate(data.create_time, 'yyyy-MM-dd')}
            </Text>
            <Text style={styles.g_item_food_text}>{data.reply_num}回答</Text>
          </View>
        </View>
      ) : tabNum == 1 ? (
        <View>
          <F16Text style={styles.g_item_content} numberOfLines={2}>
            {data.ask_content}
          </F16Text>
          <F16Text style={styles.g_item_content} numberOfLines={1}>
            我的回答：{data.content}
          </F16Text>
        </View>
      ) : (
        <View>
          <F16Text style={styles.g_item_content} numberOfLines={2}>
            {data.ask_content}
          </F16Text>
          <View style={styles.g_item_btn_view}>
            <TouchableOpacity
              style={styles.oper_btn}
              onPress={() => {
                navigate('MyAnswer', {
                  ask: data,
                });
              }}>
              <Text style={styles.g_item_btn_view_text}>去回答</Text>
            </TouchableOpacity>
          </View>
        </View>
      )}
    </TouchableOpacity>
  );
};

const GoodsItem = ({goods_img, goods_name}) => {
  return (
    <View style={styles.g_container}>
      <Image
        style={styles.g_item_image}
        source={{
          uri: goods_img,
        }}
      />
      <View style={styles.g_item_detail}>
        <F16Text numberOfLines={2}>{goods_name}</F16Text>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#FAFAFA',
  },
  g_view: {
    height: 130,
    backgroundColor: '#FFFFFF',
    marginTop: 1,
  },
  g_container: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    width: Screen.width,
    height: 60,
    paddingHorizontal: 10,
    paddingVertical: 15,
    backgroundColor: '#FFFFFF',
  },
  g_item_detail: {
    justifyContent: 'center',
    width: Screen.width - 20 - 60,
    height: 60,
    marginLeft: 15,
  },
  g_item_image: {
    width: 50,
    height: 50,
    borderRadius: 3,
  },
  g_item_content: {
    marginLeft: 10,
    marginTop: 5,
    maxHeight: 40,
    fontWeight: '600',
  },
  g_item_food: {
    marginBottom: 1,
    marginLeft: 10,
    marginRight: 10,
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  g_item_food_text: {
    color: '#666',
  },
  g_item_btn_view: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
  },
  oper_btn: {
    paddingRight: 10,
  },
  g_item_btn_view_text: {
    color: '#ff211c',
    height: 25,
    lineHeight: 25,
    width: 100,
    fontSize: 18,
    fontWeight: '400',
    borderStyle: 'solid',
    borderWidth: 1,
    borderColor: '#ff211c',
    textAlign: 'center',
    borderRadius: 10,
  },
  tabBarUnderlineStyle: {
    backgroundColor: colors.main,
  },
});

export default connect()(MyAskScene);
