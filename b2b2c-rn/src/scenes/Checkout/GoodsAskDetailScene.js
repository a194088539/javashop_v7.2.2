import React, {Component} from 'react';
import {View, Image, StyleSheet, Text, TouchableOpacity} from 'react-native';
import {connect} from 'react-redux';
import {F16Text} from '../../widgets/Text';
import {Foundation, Screen} from '../../utils';
import * as API_Goods from '../../apis/goods';
import {navigate} from '../../navigator/NavigationService';
import {defaultOriginWhitelist} from 'react-native-webview/lib/WebViewShared';

/**
 * 商品问答详情
 * Created by snow on 2020年03月11日
 */
class GoodsAskDetail extends Component {
  static navigationOptions = ({navigation}) => {
    const {params = {}} = navigation.state;
    return {
      title: params.title || '问题详情',
    };
  };

  constructor(props) {
    super(props);
    this.goods = this.props.navigation.state.params.goods_data;
    this.ask_id = this.props.navigation.state.params.ask_id;
    this.params = {
      page_no: 1,
      page_size: 10,
    };
    this.state = {
      ask_detail: {},
      ask_list: [],
    };
  }

  componentDidMount(): void {
    this._getAskDetail();
    this._getAskReplyList();
  }

  //读取问答详情
  _getAskDetail = async () => {
    const res = await API_Goods.getAskDetail(this.ask_id);
    this.setState({ask_detail: res});
  };

  _getAskReplyList = async () => {
    const {page_no} = this.params;
    const {ask_list} = this.state;
    try {
      const res = await API_Goods.getAskReplyList(this.params, this.ask_id);
      this.setState({
        noData: !res.data || !res.data[0],
        ask_list: page_no === 1 ? res.data : ask_list.concat(res.data),
      });
    } catch (error) {}
  }

  render() {
    return (
      <View style={styles.container}>
        {/*商品名称*/}
        <TouchableOpacity
          style={styles.g_container}
          onPress={() => navigate('Goods', {id: this.goods.goods_id})}>
          <Image
            style={styles.g_item_image}
            source={{
              uri:
                this.goods.thumbnail == null
                  ? this.goods.goods_img
                  : this.goods.thumbnail,
            }}
          />
          <View style={styles.g_item_detail}>
            <F16Text numberOfLines={2}>{this.goods.goods_name}</F16Text>
          </View>
        </TouchableOpacity>
        <View style={styles.a_d_container_question}>
          <View style={styles.a_d_ontainer_header}>
            <F16Text style={styles.a_d_container_header_name}>
              {this.state.ask_detail.member_name}用户的提问:
            </F16Text>
            <F16Text style={styles.a_d_container_header_time}>
              {Foundation.unixToDate(
                this.state.ask_detail.create_time,
                'yyyy-MM-dd',
              )}
            </F16Text>
          </View>
          <View style={styles.a_d_container_body_qa}>
            <Text style={styles.a_d_container_body_q_img}>问</Text>
            <Text style={styles.a_d_container_body_q} numberOfLines={5}>
              {this.state.ask_detail.content}
            </Text>
          </View>
        </View>
        {/*判断会员回复*/}
        {this.state.ask_list.length > 0 ? (
          <View>
            <Text style={styles.a_d_total}>共1个回答</Text>
            {this.state.ask_list.map((asks, index) => {
              return (
                <View style={styles.a_d_container_answer}>
                  <View style={styles.a_d_ontainer_header}>
                    <F16Text style={styles.a_d_container_header_name}>
                      {asks.member_name}回复
                    </F16Text>
                    <F16Text style={styles.a_d_container_header_time}>
                      {Foundation.unixToDate(
                        this.state.ask_detail.reply_time,
                        'yyyy-MM-dd',
                      )}
                    </F16Text>
                  </View>
                  <View style={styles.a_d_container_body_qa}>
                    <Text style={styles.a_d_container_body_a_img}>答</Text>
                    <Text style={styles.a_d_container_body_q} numberOfLines={5}>
                      {asks.content}
                    </Text>
                  </View>
                </View>
              );
            })}
          </View>
        ) : (
          <View>
            {/*判断商家回复*/}
            {this.state.ask_detail.reply != null ? (
              <View>
                <Text style={styles.a_d_total}>共1个回答</Text>
                <View style={styles.a_d_container_answer}>
                  <View style={styles.a_d_ontainer_header}>
                    <F16Text style={styles.a_d_container_header_name}>
                      商家回复:
                    </F16Text>
                    <F16Text style={styles.a_d_container_header_time}>
                      {Foundation.unixToDate(
                        this.state.ask_detail.reply_time,
                        'yyyy-MM-dd',
                      )}
                    </F16Text>
                  </View>
                  <View style={styles.a_d_container_body_qa}>
                    <Text style={styles.a_d_container_body_a_img}>答</Text>
                    <F16Text
                      style={styles.a_d_container_body_q}
                      numberOfLines={1}>
                      {this.state.ask_detail.reply}
                    </F16Text>
                  </View>
                </View>
              </View>
            ) : (
              <Text style={styles.a_d_total}>共0个回答</Text>
            )}
          </View>
        )}
      </View>
    );
  }
}

//样式
const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  g_container: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    width: Screen.width,
    height: 70,
    paddingHorizontal: 10,
    paddingVertical: 15,
    backgroundColor: '#FFFFFF',
  },
  g_item_detail: {
    justifyContent: 'center',
    width: Screen.width - 20 - 70,
    height: 70,
    marginLeft: 15,
  },
  g_item_image: {
    width: 60,
    height: 60,
    borderRadius: 3,
  },
  a_d_container_question: {
    width: Screen.width,
    height: 'auto',
    backgroundColor: '#FFFFFF',
    marginTop: 10,
    paddingBottom: 5,
  },
  a_d_container_answer: {
    width: Screen.width,
    height: 'auto',
    backgroundColor: '#FFFFFF',
  },
  a_d_ontainer_header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    width: Screen.width,
    height: 20,
    paddingHorizontal: 15,
  },
  a_d_container_header_name: {
    color: '#a4a6a8',
    fontSize: 13,
  },
  a_d_container_header_time: {
    color: '#a4a6a8',
    fontSize: 13,
  },
  a_d_container_body_qa: {
    flexDirection: 'row',
    marginLeft: 10,
    height: 'auto',
    marginTop: 5,
  },
  a_d_container_body_q_img: {
    width: 18,
    height: 18,
    backgroundColor: '#ff9600',
    color: '#FFFFFF',
    fontWeight: '800',
    textAlign: 'center',
    fontSize: 12,
    borderRadius: 10,
  },
  a_d_container_body_a_img: {
    width: 18,
    height: 18,
    backgroundColor: '#18c461',
    color: '#FFFFFF',
    fontWeight: '800',
    textAlign: 'center',
    fontSize: 12,
    borderRadius: 10,
  },
  a_d_container_body_q: {
    width: Screen.width - 20 - 18 - 25,
    marginLeft: 5,
    height: 'auto',
    paddingBottom: 5,
  },
  a_d_total: {
    height: 30,
    lineHeight: 30,
    marginLeft: 20,
    fontWeight: '600',
  },
});

export default connect()(GoodsAskDetail);
