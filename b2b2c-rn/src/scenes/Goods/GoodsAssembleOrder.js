/**
 * Created by lylyulei on 2020-04-30 14:33.
 */
import React, {PureComponent} from 'react';
import * as API_Trade from '../../apis/trade';
import {
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
  Image,
  ScrollView,
} from 'react-native';
import {Screen} from '../../utils';
import Icon from 'react-native-vector-icons/Ionicons';
import {colors} from '../../../config';
import {FlatList} from '../../components';
import BigButton from '../../widgets/BigButton';
import {Modal} from '../../components';
import {navigate} from '../../navigator/NavigationService';

const default_bg = require('../../images/icon-no-face.png');
export default class GoodsAssembleOrder extends PureComponent {
  constructor(props) {
    super(props);
    this.goods_id = this.props.goodsId;
    this.sku_id = this.props.skuId;
    this.state = {
      dataList: [],
      showModal: false,
    };
  }

  componentDidMount() {
    this._getPinTuanOrder();
  }

  /**
   * 获取拼团订单
   * @private
   */
  _getPinTuanOrder = async () => {
    try {
      const res = await API_Trade.pinTuanOrder(this.goods_id, this.sku_id);
      await this.setState({
        dataList: res,
      });
    } catch (e) {}
  };

  _ItemSeparatorComponent = () => <View />;

  _onShowModal = (index, images) => {
    this.setState({showModal: true, imageIndex: index, images});
  };

  _onModalClosed = () => {
    this.setState({showModal: false});
  };

  _renderItem = data => {
    const {participants} = data;
    const part = participants.filter(
      participant => participant.is_master === 1,
    )[0];
    return (
      <View>
        <View style={styles.item_container}>
          <View style={styles.item_left}>
            {part.face && part.face !== 'null' ? (
              <Image style={styles.face} source={{uri: part.face}} />
            ) : (
              <Image style={styles.face} source={default_bg} />
            )}
            <Text>{part.name}</Text>
          </View>
          <View style={styles.item_right}>
            <Text style={styles.item_right_text}>
              还差{data.required_num - data.offered_num}人成团
            </Text>
            <BigButton
              title="去拼团"
              style={styles.item_btn}
              textStyles={styles.item_btn_text}
              onPress={() => this._pinTuanBuyBow({order_id: data.order_id})}
            />
          </View>
        </View>
      </View>
    );
  };

  /**
   * 拼团购买
   * @returns {Promise<void>}
   * @private
   */
  _pinTuanBuyBow = async data => {
    await API_Trade.pinTuanAddCart({sku_id: this.sku_id, num: 1});
    this._onModalClosed();
    navigate('Checkout', {way: 'PINTUAN', pintuan_order_id: data.order_id});
  };
  render() {
    const {dataList, showModal} = this.state;
    return (
      <View style={styles.container}>
        <View style={styles.filter_bar}>
          <TouchableOpacity
            style={styles.bar_btn}
            onPress={() => {
              this.setState({showModal: true});
            }}>
            <View>
              <Text style={[styles.bar_text, {color: colors.main}]}>
                {dataList && dataList.length > 0 ? dataList.length : 0}
                人在拼团可直接参与
              </Text>
            </View>
            <View style={styles.bar_btn_view_right}>
              <Text style={[styles.bar_text]}>查看更多</Text>
              <Icon name="ios-arrow-forward" color="#A8A9AB" size={20} />
            </View>
          </TouchableOpacity>
        </View>
        {dataList &&
          dataList.length > 0 &&
          dataList.map((data, index) => {
            console.log(data);
            if (index < 3) {
              return this._renderItem(data);
            }
          })}

        <Modal
          title="拼团"
          style={styles.modal_style}
          isOpen={showModal}
          onClosed={this._onModalClosed}>
          <ScrollView
            style={styles.coupon_scroll}
            showsHorizontalScrollIndicator={false}
            showsVerticalScrollIndicator={false}>
            {dataList &&
              dataList.length > 0 &&
              dataList.map(data => this._renderItem(data))}
          </ScrollView>
        </Modal>
      </View>
    );
  }
}

const PinTuanItem = ({data, onPress}) => {
  const {participants} = data;
  const part = participants.filter(
    participant => participant.is_master === 1,
  )[0];
  return (
    <View style={styles.item_container}>
      <View style={styles.item_left}>
        {part.face && part.face !== 'null' ? (
          <Image style={styles.face} source={{uri: part.face}} />
        ) : (
          <Image style={styles.face} source={default_bg} />
        )}
        <Text>{part.name}</Text>
      </View>
      <View style={styles.item_right}>
        <Text style={styles.item_right_text}>
          还差{data.required_num - data.offered_num}人成团
        </Text>
        <BigButton
          title="去拼团"
          style={styles.item_btn}
          textStyles={styles.item_btn_text}
          onPress={onPress}
        />
      </View>
    </View>
  );
};
const styles = StyleSheet.create({
  container: {
    flex: 1,
    marginBottom: 10,
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
  bar_btn: {
    flexDirection: 'row',
    width: Screen.width,
    justifyContent: 'space-between',
  },
  bar_text: {
    fontSize: 14,
    lineHeight: 25,
    paddingRight: 10,
    color: colors.text,
  },
  bar_text_cur: {
    color: colors.main,
  },
  bar_btn_view_right: {
    flexDirection: 'row',
    paddingRight: 20,
  },
  item_container: {
    flexDirection: 'row',
    backgroundColor: colors.tab_background,
    borderBottomWidth: 0.5,
    borderBottomColor: colors.tab_shadow,
    height: 45,
  },
  item_left: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'flex-start',
    width: Screen.width / 2,
    paddingLeft: 10,
  },
  item_right: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'flex-end',
    width: Screen.width / 2,
    paddingRight: 10,
  },
  item_right_text: {
    marginRight: 5,
  },
  face: {
    width: 35,
    height: 35,
    borderRadius: 50,
    marginRight: 10,
  },
  item_btn: {
    width: 60,
    height: 25,
    borderRadius: 5,
  },
  item_btn_text: {
    fontSize: 13,
  },
  pintuan_scroll: {
    width: Screen.width,
    height: 135,
  },
  modal_style: {
    height: Screen.height - 200,
    backgroundColor: colors.gray_background,
  },
});
