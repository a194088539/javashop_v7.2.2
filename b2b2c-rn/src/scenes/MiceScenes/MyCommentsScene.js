import React, {Component} from 'react';
import {
  Alert,
  View,
  StatusBar,
  FlatList,
  StyleSheet,
  Text,
  TouchableOpacity,
  Picker,
  Image,
} from 'react-native';
import {connect} from 'react-redux';
import {GoodsItemPro, GoodsBtnItem} from '../../widgets/index';
import ScrollableTabView from 'react-native-scrollable-tab-view';
import {colors} from '../../../config';
import {Screen} from '../../utils';
import {navigate} from '../../navigator/NavigationService';
import * as API_Member from '../../apis/members';
import {F16Text} from '../../widgets/Text';
import {TextLabel} from '../../widgets';

/**
 * 我的评价页
 */
class MyCommentsScene extends Component {
  static navigationOptions = ({navigation}) => {
    const {params = {}} = navigation.state;
    return {
      title: params.title || '评论管理',
    };
  };

  constructor(props) {
    super(props);
    this.params = {
      page_no: 1,
      page_size: 10,
      order_status: '',
      audit_status: '',
      comments_type: '',
      comment_status: '',
    };
    this.state = {
      loading: false,
      waitCommentList: [],
      commentsList: [],
      askNoData: false,
      answerNoData: false,
    };
  }

  componentDidMount() {
    this._onRefreshWaitCommentData();
  }

  /**
   * 读取待评论数据
   * @returns {Promise<void>}
   * @private
   */
  _onRefreshWaitCommentData = async () => {
    this.params.order_status = 'WAIT_COMMENT';
    const {page_no} = this.params;
    const {waitCommentList} = this.state;
    try {
      const res = (await API_Member.waitComment(this.params)) || {};
      const {data = []} = res;
      this.setState({
        loading: false,
        refundNoData: !data[0],
        waitCommentList: page_no === 1 ? data : waitCommentList.concat(data),
      });
    } catch (error) {
      this.setState({
        loading: false,
      });
    }
  };

  /**
   * 待追评
   * @returns {Promise<void>}
   * @private
   */
  _onRefreshCommentData = async () => {
    const {page_no} = this.params;
    const {commentsList} = this.state;
    try {
      const res = (await API_Member.commentsList(this.params)) || {};
      const {data = []} = res;
      let commentsList = page_no === 1 ? data : commentsList.concat(data);
      let newCommentsList = [];
      commentsList.forEach(function(value, index) {
        console.log(`ordersn = ${value.order_sn}`);
        value.sn = value.order_sn;
        let sku = {
          sku_id: value.sku_id,
          goods_image: value.goods_img,
          name: value.goods_name,
        };
        let skuList = [];
        skuList.push(sku);
        value.sku_list = skuList;
        //如果为空时则新增
        if (newCommentsList.length == 0) {
          newCommentsList.push(value);
          return;
        }
        try {
          console.log(newCommentsList[newCommentsList.length - 1].order_sn);
          let newValue = newCommentsList[newCommentsList.length - 1];
          if (newValue.order_sn == value.order_sn) {
            newCommentsList[newCommentsList.length - 1].sku_list.push(sku);
          } else {
            newCommentsList.push(value);
          }
        } catch (e) {
          console.log(e);
        }
      })
      this.setState({
        loading: false,
        refundNoData: !data[0],
        commentsList: newCommentsList,
      });
    } catch (error) {
      this.setState({
        loading: false,
      });
    }
  };

  _changeTab = tab => {
    if (tab.i == 0) {
      this._onRefreshWaitCommentData();
    } else if (tab.i == 1) {
      this.params.order_status = '';
      this.params.audit_status = 'PASS_AUDIT';
      this.params.comments_type = 'INITIAL';
      this.params.comment_status = 'WAIT_CHASE';
      this._onRefreshCommentData();
    } else {
      this.params.order_status = '';
      this.params.audit_status = '';
      this.params.comments_type = '';
      this.params.comment_status = 'FINISHED';
      this._onRefreshCommentData();
    }
  };

  render() {
    let initialPage = 0;
    const {waitCommentList, commentsList, loading} = this.state;
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
            tabLabel="待评论"
            tabNum={0}
            dataList={waitCommentList}
            loading={loading}
            ItemSeparatorComponent={this._itemSeparatorComponent}
            getItemLayout={this._getItemLayout}
            onRefresh={this._onRefreshWaitCommentData}
            onEndReached={this._onAskListEndReached}
          />
          <AdvisoryList
            tabLabel="待追评"
            tabNum={1}
            dataList={commentsList}
            loading={loading}
            ItemSeparatorComponent={this._itemSeparatorComponent}
            getItemLayout={this._getItemLayout}
            onRefresh={this._onRefreshAskData}
            onEndReached={this._onAskListEndReached}
          />
          <AdvisoryList
            tabLabel="已评价"
            tabNum={2}
            dataList={commentsList}
            loading={loading}
            ItemSeparatorComponent={this._itemSeparatorComponent}
            getItemLayout={this._getItemLayout}
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
  let orderItem = data;
  // console.log(`orderItem=${JSON.stringify(orderItem)}`)
  return (
    <View>
      {tabNum == 0 ? (
        <View style={styles.goods_cell_container}>
          <View style={styles.goods_cell_header}>
            <F16Text>订单号：{orderItem.sn}</F16Text>
            <View style={{flexDirection: 'row'}}>
              <TextLabel
                text="评价"
                style={[styles.g_item_label_btn, styles.g_item_label_btn_del]}
                textStyle={{color: '#FFFFFF'}}
                onPress={() => navigate('CommentOrder', {data: orderItem})}
              />
            </View>
          </View>
          <View style={styles.goods_cell_body}>
            {orderItem.sku_list.map((item, index) => {
              return (
                <GoodsItemPro
                  goods_img={item.goods_image}
                  goods_name={item.name}
                />
              );
            })}
          </View>
        </View>
      ) : tabNum == 1 ? (
        <View style={styles.goods_cell_container}>
          <View style={styles.goods_cell_header}>
            <F16Text>订单号：{orderItem.order_sn}</F16Text>
            <View style={{flexDirection: 'row'}}>
              <TextLabel
                text="追评"
                style={[styles.g_item_label_btn, styles.g_item_label_btn_del]}
                textStyle={{color: '#FFFFFF'}}
                onPress={() =>
                  navigate('CommentOrder', {
                    data: orderItem,
                    append_comment: true,
                  })
                }
              />
            </View>
          </View>
          <View style={styles.goods_cell_body}>
            {orderItem.sku_list.map((item, index) => {
              return (
                <GoodsItemPro
                  goods_img={item.goods_image}
                  goods_name={item.name}
                />
              );
            })}
          </View>
        </View>
      ) : (
        <View style={styles.goods_cell_container}>
          <View style={styles.goods_cell_header}>
            <F16Text>订单号：{orderItem.order_sn}</F16Text>
          </View>
          <View style={styles.goods_cell_body}>
            {orderItem.sku_list.map((item, index) => {
              return (
                <GoodsBtnItem
                  goods_img={item.goods_image}
                  goods_name={item.name}
                  btn_title={'查看评论'}
                  routeName={'MyCommentDetail'}
                  params={{comment_id: orderItem.comment_id}}
                />
              );
            })}
          </View>
        </View>
      )}
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
    height: 90,
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
    width: 70,
    height: 70,
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
  goods_cell_container: {
    marginTop: 10,
    backgroundColor: '#FFFFFF',
  },
  goods_cell_header: {
    flex: 1,
    flexDirection: 'row',
    justifyContent: 'space-between',
    margin: 10,
  },
  goods_cell_body: {
    marginTop: 1,
  },
  g_item_label_btn: {
    height: 25,
    marginRight: 10,
    marginBottom: 0,
  },
  g_item_label_btn_del: {
    borderColor: colors.main,
    backgroundColor: colors.main,
  },
});

export default connect()(MyCommentsScene);
