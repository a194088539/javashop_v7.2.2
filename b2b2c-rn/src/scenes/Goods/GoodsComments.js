/**
 * Created by Andste on 2018/11/21.
 */
import React, {PureComponent} from 'react';
import {
  View,
  TouchableOpacity,
  Text,
  Modal,
  StyleSheet,
  Image,
} from 'react-native';
import {colors} from '../../../config';
import {Screen} from '../../utils';
import {FlatList} from '../../components';
import * as API_Members from '../../apis/members';
import CommentsItem from './GoodsCommentsItem';
import ImageViewer from 'react-native-image-zoom-viewer';
import Icon from 'react-native-vector-icons/Ionicons';
import {navigate} from '../../navigator/NavigationService';
import {F16Text} from '../../widgets/Text';
import * as API_Goods from '../../apis/goods';

export default class GoodsComments extends PureComponent {
  constructor(props) {
    super(props);
    this.goods_id = this.props.goodsId;
    this.params = {
      page_no: 1,
      page_size: 3,
    };
    this.state = {
      dataList: [],
      askDataList: [],
      noData: false,
      cur_key: 'all',
      count: {},
      showModal: false,
      goodPercent: 100,
      images: [],
    };
  }

  componentDidMount() {
    this._getCommentsData();
    this._getCommentsCount();
    this._getAskList();
  }

  _getCommentsCount = async () => {
    const res = await API_Members.getGoodsCommentsCount(this.goods_id);
    const goodPercent = Number(
      (res.good_count / res.all_count) * 100,
    ).toFixed();
    this.setState({count: res, goodPercent});
  };

  _getCommentsData = async () => {
    const {page_no} = this.params;
    const {dataList} = this.state;
    try {
      const res = await API_Members.getGoodsComments(
        this.goods_id,
        this.params,
      );
      this.setState({
        noData: !res.data || !res.data[0],
        dataList: page_no === 1 ? res.data : dataList.concat(res.data),
      });
    } catch (error) {}
  };

  //读取问答列表
  _getAskList = async () => {
    const {page_no} = this.params;
    const {askDataList} = this.state;
    try {
      const res =
        (await API_Goods.getAskList(this.params, this.goods_id)) || [];
      const {data = []} = res;
      await this.setState({
        askDataList: page_no === 1 ? data : askDataList.concat(data),
      });
    } catch (error) {}
  };

  _onFilterChange = async item => {
    await this.setState({cur_key: item.key});
    if (item.key === 'image') {
      this.params.grade = 'all';
      this.params.have_image = 1;
    } else {
      this.params.grade = item.key;
      delete this.params.have_image;
    }
  };

  _ItemSeparatorComponent = () => <View />;

  _onShowModal = (index, images) => {
    this.setState({showModal: true, imageIndex: index, images});
  };

  _onCloseModal = () => {
    this.setState({showModal: false, imageIndex: 0});
  };

  _renderItem = ({item}) => {
    return (
      <View>
        <CommentsItem
          data={item}
          simplify
          imageShow={this._onShowModal}
          imageClose={this._onCloseModal}
        />
      </View>
    );
  };

  _askRenderItem = ({item}) => {
    return (
      <TouchableOpacity
        style={styles.a_container_body}
        onPress={() => {
          navigate('GoodsAsk', {goodsId: this.goods_id});
        }}>
        <View style={styles.a_container_body_qa}>
          <Image
            style={styles.a_container_body_q_img}
            source={require('../../images/icon_goods_ask.png')}
          />
          <View>
            <F16Text style={styles.a_container_body_q} numberOfLines={1}>
              {item.content}
            </F16Text>
          </View>
        </View>
        <View style={styles.a_container_body_link}>
          <Text style={styles.a_container_body_link_text}>
            {item.reply_num}个回答
          </Text>
        </View>
      </TouchableOpacity>
    );
  }

  render() {
    const {dataList, goodPercent, count, askDataList} = this.state;
    console.log(askDataList)
    return (
      <View style={styles.container}>
        <View style={styles.filter_bar}>
          <TouchableOpacity
            style={styles.bar_btn}
            onPress={() => {
              this.props.onPress(2);
            }}>
            <View>
              <Text style={[styles.bar_text]}>评价</Text>
            </View>
            <View style={styles.bar_btn_view_right}>
              <Text style={[styles.bar_text]}>
                {dataList.length > 0
                  ? '好评率 ' + goodPercent + '%'
                  : '好评率 100%'}
              </Text>
              <Icon name="ios-arrow-forward" color="#A8A9AB" size={22} />
            </View>
          </TouchableOpacity>
        </View>
        <FlatList
          data={dataList}
          renderItem={this._renderItem}
          ListEmptyComponent={<View />}
          ItemSeparatorComponent={this._ItemSeparatorComponent}
        />
        {count.all_count > 3 && (
          <View style={styles.all_comment_view}>
            <TouchableOpacity
              style={styles.all_comment_btn}
              onPress={() => {
                this.props.onPress(2);
              }}>
              <Text style={styles.all_comment_btn_text}>查看全部评价</Text>
            </TouchableOpacity>
          </View>
        )}
        {/* 问答专区 */}
        <View style={styles.filter_bar}>
          <TouchableOpacity
            style={styles.bar_btn}
            onPress={() => {
              navigate('GoodsAsk', {goodsId: this.goods_id});
            }}>
            <View>
              <Text style={[styles.bar_text]}>问答专区</Text>
            </View>
            <View style={styles.bar_btn_view_right}>
              <Text style={[styles.bar_text]}>查看所有问答</Text>
              <Icon name="ios-arrow-forward" color="#A8A9AB" size={22} />
            </View>
          </TouchableOpacity>
        </View>
        <FlatList
          data={askDataList}
          renderItem={this._askRenderItem}
          ListEmptyComponent={<View />}
          ItemSeparatorComponent={this._ItemSeparatorComponent}
        />
        <Modal
          visible={this.state.showModal}
          transparent={true}
          animationType="slide"
          onRequestClose={this._onCloseModal}>
          <ImageViewer
            imageUrls={this.state.images}
            index={this.state.imageIndex}
            enablePreload={true}
            saveToLocalByLongPress={false}
            enableSwipeDown={true}
            onSwipeDown={this._onCloseModal}
            onCancel={this._onCloseModal}
            enableImageZoom={true}
          />
        </Modal>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  filter_bar: {
    flexDirection: 'row',
    width: Screen.width,
    paddingLeft: 10,
    alignItems: 'center',
    height: 40,
    backgroundColor: '#FFFFFF',
    marginTop: 1,
  },
  all_comment_view: {
    height: 40,
    backgroundColor: '#FFFFFF',
    width: Screen.width,
    justifyContent: 'center',
    alignItems: 'center',
  },
  all_comment_btn: {
    height: 30,
    width: 120,
    borderColor: 'black',
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 0.5,
    borderRadius: 20,
  },
  all_comment_btn_text: {
    color: 'black',
  },
  bar_btn: {
    flexDirection: 'row',
    width: Screen.width,
    justifyContent: 'space-between',
  },
  bar_btn_view_right: {
    flexDirection: 'row',
    paddingRight: 20,
  },
  bar_text: {
    fontSize: 14,
    lineHeight: 22,
    paddingRight: 10,
    color: colors.text,
  },
  bar_text_cur: {
    color: colors.main,
  },
  comment_item: {},

  a_container_body: {
    flexDirection: 'row',
    backgroundColor: '#FFFFFF',
    justifyContent: 'space-between',
  },
  a_container_body_qa: {
    flexDirection: 'row',
    height: 28,
    backgroundColor: '#FFFFFF',
    marginLeft: 10,
  },
  a_container_body_q_img: {
    width: 18,
    height: 18,
    fontWeight: '800',
    textAlign: 'center',
    fontSize: 14,
    borderRadius: 10,
    marginRight: 5,
  },
  a_container_body_link: {
    marginRight: 10,
  },
  a_container_body_link_text: {
    color: colors.tab_icon_inactive,
  },
  test: {
    height: 800,
  },
});
