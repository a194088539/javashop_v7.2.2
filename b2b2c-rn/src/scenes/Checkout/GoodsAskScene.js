import React, {Component} from 'react';
import {View, Image, StyleSheet, Text, TouchableOpacity} from 'react-native';
import {navigate} from '../../navigator/NavigationService';
import {Screen, Foundation} from '../../utils';
import {F16Text} from '../../widgets/Text';
import {connect} from 'react-redux';
import * as API_Goods from '../../apis/goods';

/**
 * 商品问答专区
 * Created by snow on 2020-03-10 17:03:24.
 */
class GoodsAskScene extends Component {
  static navigationOptions = ({navigation}) => {
    const {params = {}} = navigation.state;
    return {
      title: params.title || '问答专区',
    };
  };

  /**
   * 初始化参数
   * @param props
   */
  constructor(props) {
    super(props);
    this.goodsId = this.props.navigation.state.params.goodsId;
    this.state = {
      loading: false,
      goods: {},
      dataList: [],
    };
    this.params = {
      page_no: 1,
      page_size: 10,
    };
  }

  componentDidMount(): void {
    this._getGoods();
    this._getAskList();
  }

  //读取商品详细
  _getGoods = async () => {
    const res = await API_Goods.getGoods(this.goodsId);
    this.setState({goods: res});
    this.params.goods_id = this.state.goods.goods_id;
  };

  //读取问答列表
  _getAskList = async () => {
    const {page_no} = this.params;
    const {dataList} = this.state;
    try {
      const res = (await API_Goods.getAskList(this.params, this.goodsId)) || [];
      const {data = []} = res;
      await this.setState({
        loading: false,
        noData: !data[0],
        dataList: page_no === 1 ? data : dataList.concat(data),
      });
    } catch (error) {
      await this.setState({
        loading: false,
      });
    }
  };

  render() {
    return (
      <View style={styles.container}>
        {/*商品名称*/}
        <TouchableOpacity
          style={styles.g_container}
          onPress={() => navigate('Goods', {id: this.state.goods.goods_id})}>
          <Image
            style={styles.g_item_image}
            source={{
              uri: this.state.goods.thumbnail,
            }}
          />
          <View style={styles.g_item_detail}>
            <F16Text numberOfLines={2}>{this.state.goods.goods_name}</F16Text>
          </View>
        </TouchableOpacity>
        {/*问答列表*/}
        <View style={styles.a_container}>
          {this.state.dataList.map((ask, index) => {
            return (
              <TouchableOpacity
                key={index}
                style={styles.a_container_answer}
                onPress={() => {
                  navigate('GoodsAskDetail', {
                    goods_data: this.state.goods,
                    ask_id: ask.ask_id,
                  });
                }}>
                <View style={styles.a_container_header}>
                  <F16Text style={styles.a_container_header_name}>
                    {ask.member_name}用户提问:
                  </F16Text>
                  <F16Text style={styles.a_container_header_time}>
                    {Foundation.unixToDate(ask.create_time, 'yyyy-MM-dd')}
                  </F16Text>
                </View>
                <View style={styles.a_container_body}>
                  <View style={styles.a_container_body_qa}>
                    <Text style={styles.a_container_body_q_img}>问</Text>
                    <View>
                      <F16Text
                        style={styles.a_container_body_q}
                        numberOfLines={1}>
                        {ask.content}？
                      </F16Text>
                    </View>
                  </View>
                  <View style={styles.a_container_body_qa}>
                    <Text style={styles.a_container_body_a_img}>答</Text>
                    <View>
                      <F16Text
                        style={styles.a_container_body_a}
                        numberOfLines={1}>
                        {ask.first_reply != null
                          ? ask.first_reply.content
                          : ask.reply}
                      </F16Text>
                    </View>
                  </View>
                </View>
                <Text style={styles.a_container_foot}>
                  查看全部{ask.reply_num}个回答 >
                </Text>
              </TouchableOpacity>
            );
          })}
        </View>
        <TouchableOpacity
          style={styles.already_btn}
          onPress={() => {
            navigate('MyQuestion', {goods: this.state.goods});
          }}>
          <Text style={styles.already_btn_text}>向已购买用户提问</Text>
        </TouchableOpacity>
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
    height: 90,
    paddingHorizontal: 10,
    paddingVertical: 15,
    backgroundColor: '#FFFFFF',
  },
  g_item_detail: {
    justifyContent: 'center',
    width: Screen.width - 20 - 90,
    height: 80,
    marginLeft: 15,
  },
  g_item_image: {
    width: 80,
    height: 80,
    borderRadius: 3,
  },
  a_container: {
    flex: 1,
    width: Screen.width,
    backgroundColor: '#FAFAFA',
    paddingTop: 10,
    height: 150,
  },
  a_container_answer: {
    width: Screen.width,
    height: 130,
    backgroundColor: '#FFFFFF',
    marginTop: 10,
  },
  a_container_header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    width: Screen.width,
    height: 40,
    paddingHorizontal: 15,
  },
  a_container_header_name: {
    color: '#a4a6a8',
    fontSize: 13,
  },
  a_container_header_time: {
    color: '#a4a6a8',
    fontSize: 13,
  },
  a_container_body: {
    height: 60,
    width: Screen.width - 30,
    marginTop: 5,
  },
  a_container_body_qa: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginLeft: 20,
    height: 20,
    marginTop: 5,
  },
  a_container_body_q_img: {
    width: 18,
    height: 18,
    backgroundColor: '#ff9600',
    color: '#FFFFFF',
    fontWeight: '800',
    textAlign: 'center',
    fontSize: 14,
    borderRadius: 10,
  },
  a_container_body_a_img: {
    width: 18,
    height: 18,
    backgroundColor: '#18c461',
    color: '#FFFFFF',
    fontWeight: '800',
    textAlign: 'center',
    fontSize: 14,
    borderRadius: 10,
  },
  a_container_body_q: {
    width: Screen.width - 20 - 18 - 25,
    height: 18,
    marginLeft: 5,
    fontWeight: '800',
  },
  a_container_body_a: {
    width: Screen.width - 20 - 18 - 25,
    marginLeft: 5,
    height: 18,
  },
  a_container_foot: {
    textAlign: 'right',
    color: '#2c41ff',
    marginTop: -8,
    marginRight: 10,
  },
  already_btn: {
    height: 50,
    width: Screen.width,
    backgroundColor: '#446cff',
    textAlign: 'center',
    marginBottom: 20,
  },
  already_btn_text: {
    height: 50,
    width: Screen.width,
    textAlign: 'center',
    marginTop: 15,
    fontSize: 20,
    color: '#FFFFFF',
  },
});

export default connect()(GoodsAskScene);
